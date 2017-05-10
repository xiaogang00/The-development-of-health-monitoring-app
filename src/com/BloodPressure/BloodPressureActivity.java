package com.BloodPressure;

import com.geniuseoe.demo.R;

import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @Name:血压测量业务控制逻辑
 * @Description：初始化界面的所有组件，启动用于获取输入血压数值并启动模拟血压计，重测时撤消本次测量， 
 *                                                         诊断用于智能给出本次测量的结论，更新数据将本次测量数据更新到血压数据库
 *                                                         ，刷新用于启动计时器和任务，刷新图表趋势
 * @author 樊俊彬
 * @Time 2014-1-1
 */
@SuppressWarnings("deprecation")
public class BloodPressureActivity extends TabActivity
{

    // 声明第一个标签页（血压测量）中的组件
    private Button startButton, restartButton, enterButton;
    private EditText highPressText, lowPressText;
    private LinearLayout customSphygmomanometerLayout;
    private CustomSphygmomanometerView customSphygmomanometerView;
    private TextView titleTextView, itemTextView;

    // 声明第二个标签页（历史血压）中的组件
    private Button freshTrendChartButton, freshDataButton;
    private TextView showHighPressText, showLowPressText;
    private LinearLayout chartLayout;
    private CustomBloodPressureTrendChart customBloodPressureTrendChart;

    // 血压数据库中的历史记录
    private float[] HistoryHighPress =
    { 150, 170, 140, 176, 182, 154, 170, 140, 158, 140, 170, 140, 160, 170,
	    120, 150, 160 };
    private float[] HistoryLowPress =
    { 70, 78, 40, 76, 82, 54, 45, 40, 58, 65, 70, 80, 60, 76, 82, 55, 46 };

    // 更新长度
    private int CurrentPressLength = 1;

    // 本次测量的血压值(默认为0)
    private int currentHighPress = 0, currentLowPress = 0;

    // 智能诊断结论结果集
    private String[] itemString =
    { "您的血压正常", "您的血压不理想", "你有高血压倾向", "您有低血压倾向" };
    protected Handler handler;
    protected BluetoothDeviceAdapter buBluetoothDeviceAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	TabHost tabHost = getTabHost();
	// 设置使用TabHost布局
	LayoutInflater.from(this).inflate(R.layout.bloodpressure_layout,
		tabHost.getTabContentView(), true);

	// 添加第一个标签页（血压测量）
	tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("血压测量")
		.setContent(R.id.tab01));

	// 添加第二个标签页（历史血压）
	tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("历史血压")
		.setContent(R.id.tab02));

	// 初始化第一个标签页（血压测量）中的组件
	initBloodPressureMeasureContext();

	// 初始化第二个标签页（历史血压）中的组件
	initBloodPressureHistoryContext();

	// 启动按钮获取输入的数值，启动血压计显示
	startButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {

		// 不允许输入非法数据（空值，高压190-120，低压100-30）
		if (highPressText.getText().toString().equals("")
			|| lowPressText.getText().toString().equals("")
			|| Integer.valueOf(highPressText.getText().toString()) > 190
			|| Integer.valueOf(highPressText.getText().toString()) < 120
			|| Integer.valueOf(lowPressText.getText().toString()) > 100
			|| Integer.valueOf(lowPressText.getText().toString()) < 30) {

		    Toast.makeText(BloodPressureActivity.this, "请输入合法的高压低压值",
			    Toast.LENGTH_SHORT).show();
		    // 数据处理handler
		    handler = new BloodPressureHandler(BloodPressureActivity.this);
		    // 本地蓝牙操作线程bluetoothDeviceAdapter实现Runnalbe接口
		    buBluetoothDeviceAdapter = new BluetoothDeviceAdapter(
			    BloodPressureActivity.this, handler);
		    // 启动线程完成蓝牙相关操作：开启、搜索发现、查询配对设备信息、建立与目标设备的连接、进行数据传输、释放连接等
		    new Thread(buBluetoothDeviceAdapter).start();
		}
		else {

		    // 初始化高压和低压水银柱的动态高度

		    // 获取输入的指定高度
		    customSphygmomanometerView.highPressInput = Integer
			    .valueOf(highPressText.getText().toString());
		    customSphygmomanometerView.lowPressInput = Integer
			    .valueOf(lowPressText.getText().toString());

		    // 启动模拟血压计
		    customSphygmomanometerView.invalidate();
		    customSphygmomanometerView.highPressTimer.schedule(
			    customSphygmomanometerView.highPressTimerTask,
			    1000, 20);

		    // 不能连续启动
		    startButton.setClickable(false);
		}
	    }
	});

	// 重测按钮撤消drawView对象,建立一个新对象重绘,初始化不能执行,否则释放了已经创建的未使用的对象
	restartButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {

		// 消除已经输入的数据
		highPressText.setText("");
		lowPressText.setText("");

		/*
		 * // 消除低压水银柱 drawView.lowPressAgainTimer.schedule(
		 * drawView.lowPressAgainTimerTask, 1000, 30);
		 */

		// 释放血压计对象
		customSphygmomanometerLayout
			.removeView(customSphygmomanometerView);
		customSphygmomanometerView = new CustomSphygmomanometerView(
			BloodPressureActivity.this);
		customSphygmomanometerLayout
			.addView(customSphygmomanometerView);
		startButton.setClickable(true);
	    }
	});

	// 完成按钮将本次的测量结果存入数据库，并进行智能诊断给出结论
	enterButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {
		if (highPressText.getText().toString().equals("")
			|| lowPressText.getText().toString().equals("")) {
		    Toast.makeText(BloodPressureActivity.this, "请先启动测量",
			    Toast.LENGTH_SHORT).show();
		}
		else {

		    // 获取当前输入的数值
		    currentHighPress = Integer.valueOf(highPressText.getText()
			    .toString());
		    currentLowPress = Integer.valueOf(lowPressText.getText()
			    .toString());

		    Log.w("currentHighPress", highPressText.getText()
			    .toString());
		    Log.w("currentLowPress", lowPressText.getText().toString());

		    // 正常血压范围
		    if (currentHighPress < 135 && currentHighPress > 125
			    && currentLowPress < 85 && currentLowPress > 75) {
			itemTextView.setText(itemString[0]);
		    }
		    else {
			itemTextView.setText(itemString[1]);
		    }

		    startButton.setClickable(false);
		    restartButton.setClickable(false);
		    titleTextView.setText("诊断结论");
		}

	    }
	});

	// 更新数据把测量的血压数据更新到血压数据库
	freshDataButton.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View arg0)
	    {
		// 若当前测量的血压值为理想血压,可判定还没有测量
		if (currentHighPress == 0 && currentLowPress == 0) {
		    Toast.makeText(BloodPressureActivity.this, "您还没有测量",
			    Toast.LENGTH_SHORT).show();
		}
		else {

		    // 将输入的数值更新至本地血压数据库
		    HistoryHighPress[CurrentPressLength] = currentHighPress;
		    HistoryLowPress[CurrentPressLength] = currentLowPress;
		    CurrentPressLength++;
		    Toast.makeText(BloodPressureActivity.this, "更新成功",
			    Toast.LENGTH_SHORT).show();

		    // 同时在顶层表格中显示出来
		    showHighPressText.setText(String.valueOf(currentHighPress));
		    showLowPressText.setText(String.valueOf(currentLowPress));

		    // 释放当前图表对象,并建立新的图表对象显示
		    chartLayout.removeView(customBloodPressureTrendChart);
		    customBloodPressureTrendChart = new CustomBloodPressureTrendChart(
			    BloodPressureActivity.this);
		    chartLayout.addView(customBloodPressureTrendChart);
		}
	    }
	});

	// 刷新图表
	freshTrendChartButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {

		// 释放当前图表对象,并建立新的图表对象显示
		chartLayout.removeView(customBloodPressureTrendChart);
		customBloodPressureTrendChart = new CustomBloodPressureTrendChart(
			BloodPressureActivity.this);
		chartLayout.addView(customBloodPressureTrendChart);

		// 把数据库中的血压数据赋于本地数组
		customBloodPressureTrendChart.CurrentHighPressArray = HistoryHighPress;
		customBloodPressureTrendChart.CurrentLowPressArray = HistoryLowPress;
		// 刷新进程
		customBloodPressureTrendChart.refreshTimer.schedule(
			customBloodPressureTrendChart.refreshTask, 100, 100);
	    }
	});

    }

    private void initBloodPressureHistoryContext()
    {
	// 初始化顶层表格中显示高低压文本的组件
	showHighPressText = (TextView) findViewById(R.id.showHighPressText);
	showLowPressText = (TextView) findViewById(R.id.showLowPressText);

	// 初始化底侧组件更新数据，刷新图表
	freshTrendChartButton = (Button) findViewById(R.id.freshTrendChartButton);
	freshDataButton = (Button) findViewById(R.id.freshDataButton);

	// 初始化chartLayout血压趋势图组件
	chartLayout = (LinearLayout) findViewById(R.id.chartlayout);
	customBloodPressureTrendChart = new CustomBloodPressureTrendChart(this);
	chartLayout.addView(customBloodPressureTrendChart);
    }

    private void initBloodPressureMeasureContext()
    {
	// 初始化右侧组件(高低压输入文本框，启动、重测、诊断按钮)
	highPressText = (EditText) findViewById(R.id.highPressText_F);
	lowPressText = (EditText) findViewById(R.id.lowPressText_F);
	startButton = (Button) findViewById(R.id.startButton);
	restartButton = (Button) findViewById(R.id.restartButton);
	enterButton = (Button) findViewById(R.id.enterButton);
	titleTextView = (TextView) findViewById(R.id.titleTextView);
	itemTextView = (TextView) findViewById(R.id.itemTextView);

	// 初始化左侧血压计组件
	customSphygmomanometerLayout = (LinearLayout) findViewById(R.id.LeftLayout);
	customSphygmomanometerView = new CustomSphygmomanometerView(this);
	customSphygmomanometerLayout.addView(customSphygmomanometerView);
    }
}