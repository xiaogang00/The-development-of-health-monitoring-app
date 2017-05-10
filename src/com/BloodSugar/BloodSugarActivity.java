package com.BloodSugar;

import com.geniuseoe.demo.R;
import android.app.TabActivity;
import android.os.Bundle;
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
 * @Name:血糖测量业务控制逻辑
 * @Description: 初始化界面的所有组件，启动用于获取输入血压数值并启动模拟血压计，重测时撤消本次测量，
 *               诊断用于智能给出本次测量的结论，更新数据将本次测量数据更新到血压数据库 ， 刷新用于启动计时器和任务，刷新图表趋势
 * @author 樊俊彬
 * @Time 2014-2-3
 */

public class BloodSugarActivity extends TabActivity {

	// 声明第一个标签页（血糖测量）中的组件
	private TextView titleTextView, itemTextView;
	private Button startButton, restartButton, enterButton;
	private EditText inputVBGText, inputFPGText;
	private LinearLayout customGlucometerLayout;
	private CustomGlucometerView customGlucometerView;

	// 声明第二个标签页（历史血糖）中的组件
	private Button freshDataButton, freshTrendChartButton;
	private TextView measureVBGText, measureFPGText;
	private LinearLayout chartLayout;
	private CustomBloodSugarTrendChart customBloodSugarTrendChart;

	// 血压数据库中的历史记录
	private float[] HistoryVBG = { 90, 100, 80, 85, 95, 100, 102, 94, 95, 100,
			92, 80, 89, 93, 101, 98, 95 };
	private float[] HistoryFPG = { 110, 120, 123, 120, 116, 128, 117, 105, 114,
			105, 117, 106, 110, 114, 120, 124, 110 };

	// 当前更新长度
	private int CurrentPressLength = 1;

	// 当前测量的静脉全血血糖浓度值和静脉血浆血糖浓度值
	private int currentMeasureVBG = 0;
	private int currentMeasureFPG = 0;

	// 血糖智能诊断结果集
	private String[] itemString = {
			"正常人的血糖水平是:空腹血糖 3.9-6.1mmol/l,餐后2小时血糖 3.9-7.8mmol/l,血糖仪的测量误差国际上规定的误差范围在 20%，可以判断您的血糖处于正常水平。",
			"正常人的血糖水平是:空腹血糖 3.9-6.1mmol/l,餐后2小时血糖 3.9-7.8mmol/l,血糖仪的测量误差国际上规定的误差范围在 20%，可以判断您的血糖处于非正常水平。" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.bloodsugar_layout,
				tabHost.getTabContentView(), true);

		// 添加第一个标签页(血糖测量)
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("血糖测量")
				.setContent(R.id.tab1));
		// 添加第二个标签页
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("历史血糖")
				.setContent(R.id.tab2));

		// 初始化第一个标签页（血糖测量）中的组件
		initBloodSugarMeasureContext();

		// 初始化第二个标签页（历史血糖）中的组件
		initBloodSugarHistoryContext();

		// 启动按钮启动血糖仪显示数据
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (inputVBGText.getText().toString().equals("")
						|| inputFPGText.getText().toString().equals("")
						|| Integer.valueOf(inputVBGText.getText().toString()) > 160
						|| Integer.valueOf(inputVBGText.getText().toString()) < 50
						|| Integer.valueOf(inputFPGText.getText().toString()) > 180
						|| Integer.valueOf(inputFPGText.getText().toString()) < 50) {

					Toast.makeText(BloodSugarActivity.this, "请输入合法的血糖浓度值",
							Toast.LENGTH_SHORT).show();
				} else {

					// 把输入的血糖浓度值传递给血糖仪
					customGlucometerView.inputVBGText = Integer
							.valueOf(inputVBGText.getText().toString());
					customGlucometerView.inputFPGText = Integer
							.valueOf(inputFPGText.getText().toString());

					// ********************
					// 启动血糖仪
					customGlucometerView.invalidate();
					customGlucometerView.timer.schedule(
							customGlucometerView.timerTask, 1000, 50);

					// 不能连续启动
					startButton.setClickable(false);
				}
			}
		});

		// 重测按钮清除数据,撤消customGlucometerView对象,重新建立
		restartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// 消除已经输入的数据
				inputVBGText.setText("");
				inputFPGText.setText("");

				// 释放自定义血糖仪对象
				customGlucometerLayout.removeView(customGlucometerView);
				customGlucometerView = new CustomGlucometerView(
						BloodSugarActivity.this);
				customGlucometerLayout.addView(customGlucometerView);

				// 激活启动按钮
				startButton.setClickable(true);
			}
		});

		// 诊断按钮将本次的测量结果存入数据库，并进行智能诊断给出结论
		enterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (inputVBGText.getText().toString().equals("")
						|| inputFPGText.getText().toString().equals("")) {

					Toast.makeText(BloodSugarActivity.this, "请先启动测量",
							Toast.LENGTH_SHORT).show();
				} else {

					// 获取当前输入的数值
					currentMeasureVBG = Integer.valueOf(inputVBGText.getText()
							.toString());
					currentMeasureFPG = Integer.valueOf(inputFPGText.getText()
							.toString());

					// 正常血糖范围
					// 全血血糖为3.9～6.1mmol/L(70～110mg/mL)，血浆血糖为3.9～6.9mmol/L(70～125mg/dL)
					if (currentMeasureVBG < 110 && currentMeasureVBG > 70
							&& currentMeasureFPG < 130
							&& currentMeasureVBG > 70) {
						itemTextView.setText(itemString[0]);
					} else {
						itemTextView.setText(itemString[1]);
					}

					// 不能在次测量了
					startButton.setClickable(false);
					restartButton.setClickable(false);
					titleTextView.setText("诊断结论");
				}

			}
		});

		// 更新数据把测量的血糖数据更新到血糖数据库
		freshDataButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 若当前测量的血压值为理想血压,可判定还没有测量
				if (currentMeasureVBG == 0) {
					Toast.makeText(BloodSugarActivity.this, "您还没有测量",
							Toast.LENGTH_SHORT).show();
				} else {

					// 将输入的数值更新至本地血压数据库
					HistoryVBG[CurrentPressLength] = currentMeasureVBG;
					HistoryFPG[CurrentPressLength] = currentMeasureFPG;
					CurrentPressLength++;
					Toast.makeText(BloodSugarActivity.this, "更新成功",
							Toast.LENGTH_SHORT).show();

					// 同时在顶层表格中显示出来
					measureVBGText.setText(String.valueOf(currentMeasureVBG));
					measureFPGText.setText(String.valueOf(currentMeasureFPG));

					// 释放当前图表对象,并建立新的图表对象显示
					chartLayout.removeView(customBloodSugarTrendChart);
					customBloodSugarTrendChart = new CustomBloodSugarTrendChart(
							BloodSugarActivity.this);
					chartLayout.addView(customBloodSugarTrendChart);
				}
			}
		});

		// 刷新图表
		freshTrendChartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// 释放当前图表对象,并建立新的图表对象显示
				chartLayout.removeView(customBloodSugarTrendChart);
				customBloodSugarTrendChart = new CustomBloodSugarTrendChart(
						BloodSugarActivity.this);
				chartLayout.addView(customBloodSugarTrendChart);

				// 把数据库中的血糖数据赋于本地数组
				customBloodSugarTrendChart.CurrentVBG = HistoryVBG;
				customBloodSugarTrendChart.CurrentFPG = HistoryFPG;
				// 刷新进程
				customBloodSugarTrendChart.refreshTimer.schedule(
						customBloodSugarTrendChart.refreshTask, 100, 200);
			}
		});
	}

	private void initBloodSugarHistoryContext() {
		// 初始化本次测量的三个文本
		measureVBGText = (TextView) findViewById(R.id.measureVBGText);
		measureFPGText = (TextView) findViewById(R.id.measureFPGText);

		// 初始化更新数据、刷新图表按钮
		freshDataButton = (Button) findViewById(R.id.freshDataButton);
		freshTrendChartButton = (Button) findViewById(R.id.freshTrendChartButton);

		// 初始化血糖变化趋势图
		chartLayout = (LinearLayout) findViewById(R.id.chartLayout);
		customBloodSugarTrendChart = new CustomBloodSugarTrendChart(this);
		chartLayout.addView(customBloodSugarTrendChart);

	}

	private void initBloodSugarMeasureContext() {

		// 初始化血糖测量向导标题和内容
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		itemTextView = (TextView) findViewById(R.id.itemTextView);

		// 初始化启动、重测、诊断按钮
		startButton = (Button) findViewById(R.id.startButton);
		restartButton = (Button) findViewById(R.id.restartButton);
		enterButton = (Button) findViewById(R.id.enterButton);

		// 初始化血糖浓度值输入框
		inputVBGText = (EditText) findViewById(R.id.inputVBGText);
		inputFPGText = (EditText) findViewById(R.id.inputFPGText);

		// 初始化自定义血糖仪
		customGlucometerLayout = (LinearLayout) findViewById(R.id.customGlucometerLayout);
		customGlucometerView = new CustomGlucometerView(this);
		customGlucometerLayout.addView(customGlucometerView);
	}
}