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
 * @Name:Ѫѹ����ҵ������߼�
 * @Description����ʼ�����������������������ڻ�ȡ����Ѫѹ��ֵ������ģ��Ѫѹ�ƣ��ز�ʱ�������β����� 
 *                                                         ����������ܸ������β����Ľ��ۣ��������ݽ����β������ݸ��µ�Ѫѹ���ݿ�
 *                                                         ��ˢ������������ʱ��������ˢ��ͼ������
 * @author ������
 * @Time 2014-1-1
 */
@SuppressWarnings("deprecation")
public class BloodPressureActivity extends TabActivity
{

    // ������һ����ǩҳ��Ѫѹ�������е����
    private Button startButton, restartButton, enterButton;
    private EditText highPressText, lowPressText;
    private LinearLayout customSphygmomanometerLayout;
    private CustomSphygmomanometerView customSphygmomanometerView;
    private TextView titleTextView, itemTextView;

    // �����ڶ�����ǩҳ����ʷѪѹ���е����
    private Button freshTrendChartButton, freshDataButton;
    private TextView showHighPressText, showLowPressText;
    private LinearLayout chartLayout;
    private CustomBloodPressureTrendChart customBloodPressureTrendChart;

    // Ѫѹ���ݿ��е���ʷ��¼
    private float[] HistoryHighPress =
    { 150, 170, 140, 176, 182, 154, 170, 140, 158, 140, 170, 140, 160, 170,
	    120, 150, 160 };
    private float[] HistoryLowPress =
    { 70, 78, 40, 76, 82, 54, 45, 40, 58, 65, 70, 80, 60, 76, 82, 55, 46 };

    // ���³���
    private int CurrentPressLength = 1;

    // ���β�����Ѫѹֵ(Ĭ��Ϊ0)
    private int currentHighPress = 0, currentLowPress = 0;

    // ������Ͻ��۽����
    private String[] itemString =
    { "����Ѫѹ����", "����Ѫѹ������", "���и�Ѫѹ����", "���е�Ѫѹ����" };
    protected Handler handler;
    protected BluetoothDeviceAdapter buBluetoothDeviceAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	TabHost tabHost = getTabHost();
	// ����ʹ��TabHost����
	LayoutInflater.from(this).inflate(R.layout.bloodpressure_layout,
		tabHost.getTabContentView(), true);

	// ��ӵ�һ����ǩҳ��Ѫѹ������
	tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Ѫѹ����")
		.setContent(R.id.tab01));

	// ��ӵڶ�����ǩҳ����ʷѪѹ��
	tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("��ʷѪѹ")
		.setContent(R.id.tab02));

	// ��ʼ����һ����ǩҳ��Ѫѹ�������е����
	initBloodPressureMeasureContext();

	// ��ʼ���ڶ�����ǩҳ����ʷѪѹ���е����
	initBloodPressureHistoryContext();

	// ������ť��ȡ�������ֵ������Ѫѹ����ʾ
	startButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {

		// ����������Ƿ����ݣ���ֵ����ѹ190-120����ѹ100-30��
		if (highPressText.getText().toString().equals("")
			|| lowPressText.getText().toString().equals("")
			|| Integer.valueOf(highPressText.getText().toString()) > 190
			|| Integer.valueOf(highPressText.getText().toString()) < 120
			|| Integer.valueOf(lowPressText.getText().toString()) > 100
			|| Integer.valueOf(lowPressText.getText().toString()) < 30) {

		    Toast.makeText(BloodPressureActivity.this, "������Ϸ��ĸ�ѹ��ѹֵ",
			    Toast.LENGTH_SHORT).show();
		    // ���ݴ���handler
		    handler = new BloodPressureHandler(BloodPressureActivity.this);
		    // �������������߳�bluetoothDeviceAdapterʵ��Runnalbe�ӿ�
		    buBluetoothDeviceAdapter = new BluetoothDeviceAdapter(
			    BloodPressureActivity.this, handler);
		    // �����߳����������ز������������������֡���ѯ����豸��Ϣ��������Ŀ���豸�����ӡ��������ݴ��䡢�ͷ����ӵ�
		    new Thread(buBluetoothDeviceAdapter).start();
		}
		else {

		    // ��ʼ����ѹ�͵�ѹˮ�����Ķ�̬�߶�

		    // ��ȡ�����ָ���߶�
		    customSphygmomanometerView.highPressInput = Integer
			    .valueOf(highPressText.getText().toString());
		    customSphygmomanometerView.lowPressInput = Integer
			    .valueOf(lowPressText.getText().toString());

		    // ����ģ��Ѫѹ��
		    customSphygmomanometerView.invalidate();
		    customSphygmomanometerView.highPressTimer.schedule(
			    customSphygmomanometerView.highPressTimerTask,
			    1000, 20);

		    // ������������
		    startButton.setClickable(false);
		}
	    }
	});

	// �زⰴť����drawView����,����һ���¶����ػ�,��ʼ������ִ��,�����ͷ����Ѿ�������δʹ�õĶ���
	restartButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {

		// �����Ѿ����������
		highPressText.setText("");
		lowPressText.setText("");

		/*
		 * // ������ѹˮ���� drawView.lowPressAgainTimer.schedule(
		 * drawView.lowPressAgainTimerTask, 1000, 30);
		 */

		// �ͷ�Ѫѹ�ƶ���
		customSphygmomanometerLayout
			.removeView(customSphygmomanometerView);
		customSphygmomanometerView = new CustomSphygmomanometerView(
			BloodPressureActivity.this);
		customSphygmomanometerLayout
			.addView(customSphygmomanometerView);
		startButton.setClickable(true);
	    }
	});

	// ��ɰ�ť�����εĲ�������������ݿ⣬������������ϸ�������
	enterButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {
		if (highPressText.getText().toString().equals("")
			|| lowPressText.getText().toString().equals("")) {
		    Toast.makeText(BloodPressureActivity.this, "������������",
			    Toast.LENGTH_SHORT).show();
		}
		else {

		    // ��ȡ��ǰ�������ֵ
		    currentHighPress = Integer.valueOf(highPressText.getText()
			    .toString());
		    currentLowPress = Integer.valueOf(lowPressText.getText()
			    .toString());

		    Log.w("currentHighPress", highPressText.getText()
			    .toString());
		    Log.w("currentLowPress", lowPressText.getText().toString());

		    // ����Ѫѹ��Χ
		    if (currentHighPress < 135 && currentHighPress > 125
			    && currentLowPress < 85 && currentLowPress > 75) {
			itemTextView.setText(itemString[0]);
		    }
		    else {
			itemTextView.setText(itemString[1]);
		    }

		    startButton.setClickable(false);
		    restartButton.setClickable(false);
		    titleTextView.setText("��Ͻ���");
		}

	    }
	});

	// �������ݰѲ�����Ѫѹ���ݸ��µ�Ѫѹ���ݿ�
	freshDataButton.setOnClickListener(new OnClickListener()
	{

	    @Override
	    public void onClick(View arg0)
	    {
		// ����ǰ������ѪѹֵΪ����Ѫѹ,���ж���û�в���
		if (currentHighPress == 0 && currentLowPress == 0) {
		    Toast.makeText(BloodPressureActivity.this, "����û�в���",
			    Toast.LENGTH_SHORT).show();
		}
		else {

		    // ���������ֵ����������Ѫѹ���ݿ�
		    HistoryHighPress[CurrentPressLength] = currentHighPress;
		    HistoryLowPress[CurrentPressLength] = currentLowPress;
		    CurrentPressLength++;
		    Toast.makeText(BloodPressureActivity.this, "���³ɹ�",
			    Toast.LENGTH_SHORT).show();

		    // ͬʱ�ڶ���������ʾ����
		    showHighPressText.setText(String.valueOf(currentHighPress));
		    showLowPressText.setText(String.valueOf(currentLowPress));

		    // �ͷŵ�ǰͼ�����,�������µ�ͼ�������ʾ
		    chartLayout.removeView(customBloodPressureTrendChart);
		    customBloodPressureTrendChart = new CustomBloodPressureTrendChart(
			    BloodPressureActivity.this);
		    chartLayout.addView(customBloodPressureTrendChart);
		}
	    }
	});

	// ˢ��ͼ��
	freshTrendChartButton.setOnClickListener(new OnClickListener()
	{
	    @Override
	    public void onClick(View arg0)
	    {

		// �ͷŵ�ǰͼ�����,�������µ�ͼ�������ʾ
		chartLayout.removeView(customBloodPressureTrendChart);
		customBloodPressureTrendChart = new CustomBloodPressureTrendChart(
			BloodPressureActivity.this);
		chartLayout.addView(customBloodPressureTrendChart);

		// �����ݿ��е�Ѫѹ���ݸ��ڱ�������
		customBloodPressureTrendChart.CurrentHighPressArray = HistoryHighPress;
		customBloodPressureTrendChart.CurrentLowPressArray = HistoryLowPress;
		// ˢ�½���
		customBloodPressureTrendChart.refreshTimer.schedule(
			customBloodPressureTrendChart.refreshTask, 100, 100);
	    }
	});

    }

    private void initBloodPressureHistoryContext()
    {
	// ��ʼ������������ʾ�ߵ�ѹ�ı������
	showHighPressText = (TextView) findViewById(R.id.showHighPressText);
	showLowPressText = (TextView) findViewById(R.id.showLowPressText);

	// ��ʼ���ײ�����������ݣ�ˢ��ͼ��
	freshTrendChartButton = (Button) findViewById(R.id.freshTrendChartButton);
	freshDataButton = (Button) findViewById(R.id.freshDataButton);

	// ��ʼ��chartLayoutѪѹ����ͼ���
	chartLayout = (LinearLayout) findViewById(R.id.chartlayout);
	customBloodPressureTrendChart = new CustomBloodPressureTrendChart(this);
	chartLayout.addView(customBloodPressureTrendChart);
    }

    private void initBloodPressureMeasureContext()
    {
	// ��ʼ���Ҳ����(�ߵ�ѹ�����ı����������ز⡢��ϰ�ť)
	highPressText = (EditText) findViewById(R.id.highPressText_F);
	lowPressText = (EditText) findViewById(R.id.lowPressText_F);
	startButton = (Button) findViewById(R.id.startButton);
	restartButton = (Button) findViewById(R.id.restartButton);
	enterButton = (Button) findViewById(R.id.enterButton);
	titleTextView = (TextView) findViewById(R.id.titleTextView);
	itemTextView = (TextView) findViewById(R.id.itemTextView);

	// ��ʼ�����Ѫѹ�����
	customSphygmomanometerLayout = (LinearLayout) findViewById(R.id.LeftLayout);
	customSphygmomanometerView = new CustomSphygmomanometerView(this);
	customSphygmomanometerLayout.addView(customSphygmomanometerView);
    }
}