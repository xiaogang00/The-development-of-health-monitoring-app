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
 * @Name:�Զ���Ѫ�Ǳ仯����ͼ
 * @Description��Ѫ�Ǳ仯���ƶ�ά����ͼ�����������ᣬȫѪѪ�����������䣬Ѫ��Ѫ�����������䡢����ȫѪѪ�Ǻ�Ѫ��Ѫ�ǣ����β�����ȫѪѪ�Ǻ�Ѫ��Ѫ��Ũ��
 * @author ������
 * @Time 2014-2-5
 */
public class CustomBloodSugarTrendChart extends View {

	// ���������ꡢ���
	private final int FRAME_X = 10;
	private final int FRAME_Y = 10;
	private final int FRAME_WIDTH = 700;
	private final int FRAME_HEIGHT = 570;

	// ԭ������
	private final int Origin_X = FRAME_X + 40;
	private final int Origin_Y = FRAME_Y + FRAME_HEIGHT - 40;

	// X�ᡢY���յ�����
	private final int XAxis_X = FRAME_X + FRAME_WIDTH - 60;
	private final int XAxis_Y = Origin_Y;
	private final int YAxis_X = Origin_X;
	private final int YAxis_Y = FRAME_Y + 25;

	// �ֶ�ֵ(x�᳤��/17��)�� y����Ѫѹ�ֶ�ֵ(y�᳤��/10��������λ)
	private final int TIME_INTERVAL = (XAxis_X - Origin_X) / 17;
	private final int PRESS_INTERVAL = (Origin_Y - YAxis_Y) / 11;

	// X����ʱ�����,y���ϵ�Ѫѹ����
	private final int TIME_COUNT = (XAxis_X - Origin_X) / TIME_INTERVAL;
	private final int PRESS_COUNT = (Origin_Y - YAxis_Y) / PRESS_INTERVAL;

	// ����ȫѪѪ�Ǻ�Ѫ��Ѫ��Ũ�����飨��Դ��Ѫ�����ݿ⣩
	float[] CurrentVBG = { 90, 100, 80, 85, 95, 100, 102, 94, 95, 100, 92, 80,
			89, 93, 101, 98, 95 };
	float[] CurrentFPG = { 110, 120, 123, 120, 116, 128, 117, 105, 114, 105,
			117, 106, 110, 114, 120, 124, 110 };
	int PressArrayLength = 1;

	// ��ʱ����ߵ�ѹ��̬���½�������
	Timer refreshTimer;
	TimerTask refreshTask;

	public CustomBloodSugarTrendChart(Context context) {
		super(context);

		refreshTask = new TimerTask() {
			@Override
			public void run() {

				// ˢ������:���鳤�ȴ�1��������󳤶�
				PressArrayLength++;
				postInvalidate();
				if (PressArrayLength == CurrentVBG.length) {
					refreshTimer.cancel();
				}

			}
		};
		// ������ʱ���̣߳�ÿ��һ����ʱ��ȥִ�и�ѹ�͵�ѹ���ػ�
		refreshTimer = new Timer();
	}

	// �����뻭���ػ�����ͼ
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();

		// ��ɫ���߿�
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(6);
		canvas.drawRect(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT, paint);

		// ��������
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		// X�ἰ�����ͷ
		canvas.drawLine(Origin_X, Origin_Y, XAxis_X, XAxis_Y, paint);
		canvas.drawLine(XAxis_X, XAxis_Y, XAxis_X - 10, XAxis_Y - 10, paint);
		canvas.drawLine(XAxis_X, XAxis_Y, XAxis_X - 10, XAxis_Y + 10, paint);
		// Y�ἰ�����ͷ
		canvas.drawLine(Origin_X, Origin_Y, YAxis_X, YAxis_Y, paint);
		canvas.drawLine(YAxis_X, YAxis_Y, YAxis_X - 10, YAxis_Y + 10, paint);
		canvas.drawLine(YAxis_X, YAxis_Y, YAxis_X + 10, YAxis_Y + 10, paint);

		// ��X����ʱ��̶�(������ԭ����ÿ��TIME_INTERVAL(ʱ��ֶ�)���ػ�һʱ��㣬��X���յ���)
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);
		for (int i = Origin_X, j = 0; i < XAxis_X - TIME_INTERVAL; i += TIME_INTERVAL, j += 1) {
			canvas.drawText("1-" + j, i - 10, Origin_Y + 20, paint);
		}
		canvas.drawText("����", XAxis_X + 5, XAxis_Y + 5, paint);

		// ��Y����Ѫ��Ũ�ȿ̶�(������ԭ��(50mg/dL)��ÿ��PRESS_INTERVAL���ػ�һŨ��ֵ����Y���յ���)
		paint.setTextSize(15);
		for (int i = Origin_Y, j = 50; i > YAxis_Y + PRESS_INTERVAL; i -= PRESS_INTERVAL, j += 10) {
			canvas.drawText(j + "", Origin_X - 30, i + 3, paint);
		}
		canvas.drawText("�ո�Ѫ��Ũ��(mg/dL)", YAxis_X - 15, YAxis_Y - 5, paint);

		// ��������
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1);
		// ����
		for (int i = Origin_X + TIME_INTERVAL; i < XAxis_X - TIME_INTERVAL; i += TIME_INTERVAL) {
			canvas.drawLine(i, Origin_Y, i, Origin_Y - (PRESS_COUNT - 1)
					* PRESS_INTERVAL, paint);
		}
		// ����
		for (int i = Origin_Y - PRESS_INTERVAL; i > YAxis_Y + PRESS_INTERVAL; i -= PRESS_INTERVAL) {
			canvas.drawLine(Origin_X, i, Origin_X + (TIME_COUNT - 1)
					* TIME_INTERVAL, i, paint);
		}

		// �ո�Ѫ���������䣺ȫѪѪ��Ϊ3.9��6.1mmol/L(70��110mg/mL)��Ѫ��Ѫ��Ϊ3.9��6.9mmol/L(70��125mg/dL)

		// �ո�Ѫ�����70mg/mL
		paint.setColor(Color.DKGRAY);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - (7 - 5) * PRESS_INTERVAL, Origin_X
				+ (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y - (7 - 5)
				* PRESS_INTERVAL, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize(20);
		canvas.drawText("���ȫѪѪ��", Origin_X + (TIME_COUNT - 3) * TIME_INTERVAL,
				Origin_Y - (7 - 5) * PRESS_INTERVAL + 20, paint);

		// �ո�ȫѪѪ�����110mg/mL
		paint.setColor(Color.MAGENTA);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - (11 - 5) * PRESS_INTERVAL,
				Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y
						- (11 - 5) * PRESS_INTERVAL, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize(20);
		canvas.drawText("���ȫѪ", Origin_X + (TIME_COUNT - 3) * TIME_INTERVAL,
				Origin_Y - (11 - 5) * PRESS_INTERVAL + 20, paint);

		// ����ȫѪѪ��120mg/mL
		paint.setColor(Color.MAGENTA);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - (12 - 5) * PRESS_INTERVAL,
				Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y
						- (12 - 5) * PRESS_INTERVAL, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize(20);
		canvas.drawText("����ȫѪ", Origin_X + (TIME_COUNT - 3) * TIME_INTERVAL,
				Origin_Y - (12 - 5) * PRESS_INTERVAL + 20, paint);

		// �ո�Ѫ��Ѫ�����130mg/dL
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - (13 - 5) * PRESS_INTERVAL,
				Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y
						- (13 - 5) * PRESS_INTERVAL, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize(20);
		canvas.drawText("���Ѫ��", Origin_X + (TIME_COUNT - 3) * TIME_INTERVAL,
				Origin_Y - (13 - 5) * PRESS_INTERVAL + 20, paint);

		// ����Ѫ��Ѫ��140mg/dL
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - (14 - 5) * PRESS_INTERVAL,
				Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y
						- (14 - 5) * PRESS_INTERVAL, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize(20);
		canvas.drawText("����Ѫ��", Origin_X + (TIME_COUNT - 3) * TIME_INTERVAL,
				Origin_Y - (14 - 5) * PRESS_INTERVAL + 20, paint);

		// ��ǰ����ȫѪѪ����,ѭ������ȫѪѪ������CurrentVBG[]�б�����߶�
		paint.setColor(Color.MAGENTA);
		paint.setStrokeWidth(4);
		for (int i = 1, CurrentHighPressStart_X = Origin_X; i < PressArrayLength; i++, CurrentHighPressStart_X += TIME_INTERVAL) {
			canvas.drawLine(CurrentHighPressStart_X, Origin_Y
					- (CurrentVBG[i - 1] / 10 - 5) * PRESS_INTERVAL,
					CurrentHighPressStart_X + TIME_INTERVAL, Origin_Y
							- (CurrentVBG[i] / 10 - 5) * PRESS_INTERVAL, paint);
		}

		// ��ǰ����Ѫ��Ѫ����,ѭ������Ѫ��Ѫ������CurrentFPG[]�б�����߶�
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(4);
		for (int i = 1, CurrentLowPressStart_X = Origin_X; i < PressArrayLength; i++, CurrentLowPressStart_X += TIME_INTERVAL) {
			canvas.drawLine(CurrentLowPressStart_X, Origin_Y
					- (CurrentFPG[i - 1] / 10 - 5) * PRESS_INTERVAL,
					CurrentLowPressStart_X + TIME_INTERVAL, Origin_Y
							- (CurrentFPG[i] / 10 - 5) * PRESS_INTERVAL, paint);
			Log.w("(CurrentFPG[i - 1] / 10 - 5)* PRESS_INTERVAL",
					String.valueOf((CurrentFPG[i - 1] / 10 - 5)
							* PRESS_INTERVAL));
		}
	}
}
