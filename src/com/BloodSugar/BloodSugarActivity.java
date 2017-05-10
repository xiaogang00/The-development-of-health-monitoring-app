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
 * @Name:Ѫ�ǲ���ҵ������߼�
 * @Description: ��ʼ�����������������������ڻ�ȡ����Ѫѹ��ֵ������ģ��Ѫѹ�ƣ��ز�ʱ�������β�����
 *               ����������ܸ������β����Ľ��ۣ��������ݽ����β������ݸ��µ�Ѫѹ���ݿ� �� ˢ������������ʱ��������ˢ��ͼ������
 * @author ������
 * @Time 2014-2-3
 */

public class BloodSugarActivity extends TabActivity {

	// ������һ����ǩҳ��Ѫ�ǲ������е����
	private TextView titleTextView, itemTextView;
	private Button startButton, restartButton, enterButton;
	private EditText inputVBGText, inputFPGText;
	private LinearLayout customGlucometerLayout;
	private CustomGlucometerView customGlucometerView;

	// �����ڶ�����ǩҳ����ʷѪ�ǣ��е����
	private Button freshDataButton, freshTrendChartButton;
	private TextView measureVBGText, measureFPGText;
	private LinearLayout chartLayout;
	private CustomBloodSugarTrendChart customBloodSugarTrendChart;

	// Ѫѹ���ݿ��е���ʷ��¼
	private float[] HistoryVBG = { 90, 100, 80, 85, 95, 100, 102, 94, 95, 100,
			92, 80, 89, 93, 101, 98, 95 };
	private float[] HistoryFPG = { 110, 120, 123, 120, 116, 128, 117, 105, 114,
			105, 117, 106, 110, 114, 120, 124, 110 };

	// ��ǰ���³���
	private int CurrentPressLength = 1;

	// ��ǰ�����ľ���ȫѪѪ��Ũ��ֵ�;���Ѫ��Ѫ��Ũ��ֵ
	private int currentMeasureVBG = 0;
	private int currentMeasureFPG = 0;

	// Ѫ��������Ͻ����
	private String[] itemString = {
			"�����˵�Ѫ��ˮƽ��:�ո�Ѫ�� 3.9-6.1mmol/l,�ͺ�2СʱѪ�� 3.9-7.8mmol/l,Ѫ���ǵĲ����������Ϲ涨����Χ�� 20%�������ж�����Ѫ�Ǵ�������ˮƽ��",
			"�����˵�Ѫ��ˮƽ��:�ո�Ѫ�� 3.9-6.1mmol/l,�ͺ�2СʱѪ�� 3.9-7.8mmol/l,Ѫ���ǵĲ����������Ϲ涨����Χ�� 20%�������ж�����Ѫ�Ǵ��ڷ�����ˮƽ��" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.bloodsugar_layout,
				tabHost.getTabContentView(), true);

		// ��ӵ�һ����ǩҳ(Ѫ�ǲ���)
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Ѫ�ǲ���")
				.setContent(R.id.tab1));
		// ��ӵڶ�����ǩҳ
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("��ʷѪ��")
				.setContent(R.id.tab2));

		// ��ʼ����һ����ǩҳ��Ѫ�ǲ������е����
		initBloodSugarMeasureContext();

		// ��ʼ���ڶ�����ǩҳ����ʷѪ�ǣ��е����
		initBloodSugarHistoryContext();

		// ������ť����Ѫ������ʾ����
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (inputVBGText.getText().toString().equals("")
						|| inputFPGText.getText().toString().equals("")
						|| Integer.valueOf(inputVBGText.getText().toString()) > 160
						|| Integer.valueOf(inputVBGText.getText().toString()) < 50
						|| Integer.valueOf(inputFPGText.getText().toString()) > 180
						|| Integer.valueOf(inputFPGText.getText().toString()) < 50) {

					Toast.makeText(BloodSugarActivity.this, "������Ϸ���Ѫ��Ũ��ֵ",
							Toast.LENGTH_SHORT).show();
				} else {

					// �������Ѫ��Ũ��ֵ���ݸ�Ѫ����
					customGlucometerView.inputVBGText = Integer
							.valueOf(inputVBGText.getText().toString());
					customGlucometerView.inputFPGText = Integer
							.valueOf(inputFPGText.getText().toString());

					// ********************
					// ����Ѫ����
					customGlucometerView.invalidate();
					customGlucometerView.timer.schedule(
							customGlucometerView.timerTask, 1000, 50);

					// ������������
					startButton.setClickable(false);
				}
			}
		});

		// �زⰴť�������,����customGlucometerView����,���½���
		restartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// �����Ѿ����������
				inputVBGText.setText("");
				inputFPGText.setText("");

				// �ͷ��Զ���Ѫ���Ƕ���
				customGlucometerLayout.removeView(customGlucometerView);
				customGlucometerView = new CustomGlucometerView(
						BloodSugarActivity.this);
				customGlucometerLayout.addView(customGlucometerView);

				// ����������ť
				startButton.setClickable(true);
			}
		});

		// ��ϰ�ť�����εĲ�������������ݿ⣬������������ϸ�������
		enterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (inputVBGText.getText().toString().equals("")
						|| inputFPGText.getText().toString().equals("")) {

					Toast.makeText(BloodSugarActivity.this, "������������",
							Toast.LENGTH_SHORT).show();
				} else {

					// ��ȡ��ǰ�������ֵ
					currentMeasureVBG = Integer.valueOf(inputVBGText.getText()
							.toString());
					currentMeasureFPG = Integer.valueOf(inputFPGText.getText()
							.toString());

					// ����Ѫ�Ƿ�Χ
					// ȫѪѪ��Ϊ3.9��6.1mmol/L(70��110mg/mL)��Ѫ��Ѫ��Ϊ3.9��6.9mmol/L(70��125mg/dL)
					if (currentMeasureVBG < 110 && currentMeasureVBG > 70
							&& currentMeasureFPG < 130
							&& currentMeasureVBG > 70) {
						itemTextView.setText(itemString[0]);
					} else {
						itemTextView.setText(itemString[1]);
					}

					// �����ڴβ�����
					startButton.setClickable(false);
					restartButton.setClickable(false);
					titleTextView.setText("��Ͻ���");
				}

			}
		});

		// �������ݰѲ�����Ѫ�����ݸ��µ�Ѫ�����ݿ�
		freshDataButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// ����ǰ������ѪѹֵΪ����Ѫѹ,���ж���û�в���
				if (currentMeasureVBG == 0) {
					Toast.makeText(BloodSugarActivity.this, "����û�в���",
							Toast.LENGTH_SHORT).show();
				} else {

					// ���������ֵ����������Ѫѹ���ݿ�
					HistoryVBG[CurrentPressLength] = currentMeasureVBG;
					HistoryFPG[CurrentPressLength] = currentMeasureFPG;
					CurrentPressLength++;
					Toast.makeText(BloodSugarActivity.this, "���³ɹ�",
							Toast.LENGTH_SHORT).show();

					// ͬʱ�ڶ���������ʾ����
					measureVBGText.setText(String.valueOf(currentMeasureVBG));
					measureFPGText.setText(String.valueOf(currentMeasureFPG));

					// �ͷŵ�ǰͼ�����,�������µ�ͼ�������ʾ
					chartLayout.removeView(customBloodSugarTrendChart);
					customBloodSugarTrendChart = new CustomBloodSugarTrendChart(
							BloodSugarActivity.this);
					chartLayout.addView(customBloodSugarTrendChart);
				}
			}
		});

		// ˢ��ͼ��
		freshTrendChartButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// �ͷŵ�ǰͼ�����,�������µ�ͼ�������ʾ
				chartLayout.removeView(customBloodSugarTrendChart);
				customBloodSugarTrendChart = new CustomBloodSugarTrendChart(
						BloodSugarActivity.this);
				chartLayout.addView(customBloodSugarTrendChart);

				// �����ݿ��е�Ѫ�����ݸ��ڱ�������
				customBloodSugarTrendChart.CurrentVBG = HistoryVBG;
				customBloodSugarTrendChart.CurrentFPG = HistoryFPG;
				// ˢ�½���
				customBloodSugarTrendChart.refreshTimer.schedule(
						customBloodSugarTrendChart.refreshTask, 100, 200);
			}
		});
	}

	private void initBloodSugarHistoryContext() {
		// ��ʼ�����β����������ı�
		measureVBGText = (TextView) findViewById(R.id.measureVBGText);
		measureFPGText = (TextView) findViewById(R.id.measureFPGText);

		// ��ʼ���������ݡ�ˢ��ͼ��ť
		freshDataButton = (Button) findViewById(R.id.freshDataButton);
		freshTrendChartButton = (Button) findViewById(R.id.freshTrendChartButton);

		// ��ʼ��Ѫ�Ǳ仯����ͼ
		chartLayout = (LinearLayout) findViewById(R.id.chartLayout);
		customBloodSugarTrendChart = new CustomBloodSugarTrendChart(this);
		chartLayout.addView(customBloodSugarTrendChart);

	}

	private void initBloodSugarMeasureContext() {

		// ��ʼ��Ѫ�ǲ����򵼱��������
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		itemTextView = (TextView) findViewById(R.id.itemTextView);

		// ��ʼ���������ز⡢��ϰ�ť
		startButton = (Button) findViewById(R.id.startButton);
		restartButton = (Button) findViewById(R.id.restartButton);
		enterButton = (Button) findViewById(R.id.enterButton);

		// ��ʼ��Ѫ��Ũ��ֵ�����
		inputVBGText = (EditText) findViewById(R.id.inputVBGText);
		inputFPGText = (EditText) findViewById(R.id.inputFPGText);

		// ��ʼ���Զ���Ѫ����
		customGlucometerLayout = (LinearLayout) findViewById(R.id.customGlucometerLayout);
		customGlucometerView = new CustomGlucometerView(this);
		customGlucometerLayout.addView(customGlucometerView);
	}
}