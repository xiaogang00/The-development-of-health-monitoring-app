package com.BloodSugar;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * @Name:�Զ���Ѫ����
 * 
 * @Description
 * 
 * @Date:2014-2-2
 * @author ������
 * @version 1.0
 */
public class CustomGlucometerView extends View {

	// ��ʾ��λ�úʹ�С
	private final int FRAME_X = 80;
	private final int FRAME_Y = 40;
	private final int FRAME_WIDTH = 230;
	private final int FRAME_HEIGHT = 130;

	// ������������Ѫ��Ũ��ֵ
	int inputVBGText;
	int inputFPGText;
	
	// Ѫ����ֵ����(0-bloodSugarInput)
	private int bloodSugarData = 0;

	// ��ʱ����Ѫ����ֵ�����������
	Timer timer;
	TimerTask timerTask;

	public CustomGlucometerView(Context context) {
		super(context);

		// BloodsugarData ��0��ʼ��0.1/��λʱ����ٶȵ���,�ﵽָ����Сʱֹͣ
		timerTask = new TimerTask() {
			@Override
			public void run() {
				bloodSugarData += 1;
				// �ػ�
				postInvalidate();
				// �ﵽָ����Сֹͣ

				if (bloodSugarData == inputVBGText) {
					timer.cancel();
				}
			}
		};
		// ������ʱ���̣߳�ÿ��һ����ʱ��ȥִ��
		timer = new Timer();
	}

	// �ػ���ʾ��
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();

		// ��ɫ���ʴֻ��߿�
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawRect(FRAME_X, FRAME_Y, FRAME_X + FRAME_WIDTH, FRAME_Y
				+ FRAME_HEIGHT, paint);

		// ��ʾ������ʾѪ����ֵ
		paint.setColor(Color.BLACK);
		paint.setTextSize(80);
		canvas.drawText(String.valueOf(bloodSugarData), FRAME_X + FRAME_WIDTH
				/ 2 - 20, FRAME_Y + FRAME_HEIGHT / 2 + 20, paint);
	}

}
