package com.BloodPressure;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * @Name:�Զ���Ѫѹ�仯����ͼ
 * @Description��Ѫѹ�仯���ƶ�ά����ͼ�����������ᣬ�����ѹ����ѹ��׼�ߣ���̬��ѹ����ѹ�ߣ��ֶ�ֵ��
 * @author ������
 * @Time 2014-1-1
 */
public class CustomBloodPressureTrendChart extends View {

	// ���������ꡢ���
	private final int FRAME_X = 10;
	private final int FRAME_Y = 10;
	private final int FRAME_WIDTH = 700;
	private final int FRAME_HEIGHT = 450;

	// ԭ������
	private final int Origin_X = FRAME_X + 40;
	private final int Origin_Y = FRAME_Y + FRAME_HEIGHT - 40;

	// X�ᡢY���յ�����
	private final int XAxis_X = FRAME_X + FRAME_WIDTH - 60;
	private final int XAxis_Y = Origin_Y;
	private final int YAxis_X = Origin_X;
	private final int YAxis_Y = FRAME_Y + 20;

	// X���ϵ�ʱ��ֶ�ֵ(x�᳤��/17��������λ)�� y����Ѫѹ�ֶ�ֵ(y�᳤��/20��������λ)
	private final int TIME_INTERVAL = (XAxis_X - Origin_X) / 17;
	private final int PRESS_INTERVAL = (Origin_Y - YAxis_Y) / 20;

	// X����ʱ�����,y���ϵ�Ѫѹ����
	private final int TIME_COUNT = (XAxis_X - Origin_X) / TIME_INTERVAL;
	private final int PRESS_COUNT = (Origin_Y - YAxis_Y) / PRESS_INTERVAL;

	// ���ظ�ѹ�͵�ѹֵ���飨��Դ��Ѫѹ���ݿ⣩Ĭ��Ϊ����Ѫѹ
	float[] CurrentHighPressArray = { 150, 150, 150, 150, 150, 150,
			150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150 };
	float[] CurrentLowPressArray = { 70, 85, 70, 70, 70, 70, 70, 70, 70, 70,
			70, 70, 70, 70, 70, 70, 70 };
	int PressArrayLength = 1;

	// ��ʱ����ߵ�ѹ��̬���½�������
	Timer refreshTimer;
	TimerTask refreshTask;

	public CustomBloodPressureTrendChart(Context context) {
		super(context);
		// ˢ������:���鳤�ȴ�1��������󳤶�
		refreshTask = new TimerTask() {
			@Override
			public void run() {
				// ��ѹ��̬����
				PressArrayLength++;
				postInvalidate();
				if (PressArrayLength == CurrentHighPressArray.length) {
					refreshTimer.cancel();
				}

			}
		};
		// ������ʱ���̣߳�ÿ��һ����ʱ��ȥִ�и�ѹ�͵�ѹ���ػ�
		refreshTimer = new Timer();
	}

	// �����뻭���ػ�Ѫѹ����ͼ
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

		// ��Y����Ѫѹ�̶�(������ԭ����ÿ��10���ػ�һѹ��ֵ����Y���յ���)
		for (int i = Origin_Y, j = 0; i > YAxis_Y + PRESS_INTERVAL; i -= PRESS_INTERVAL, j += 10) {
			canvas.drawText(j + "", Origin_X - 30, i + 3, paint);
		}
		canvas.drawText("Ѫѹ/mmgh", YAxis_X - 5, YAxis_Y - 5, paint);

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

		// ����Ѫѹ��׼��
		// ��ѹ
		paint.setColor(Color.MAGENTA);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - 15 * PRESS_INTERVAL, Origin_X
				+ (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y - 15
				* PRESS_INTERVAL, paint);
		paint.setStrokeWidth(1);
		canvas.drawText("�����ѹ��", Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL
				+ 3, Origin_Y - 15 * PRESS_INTERVAL + 3, paint);
		// ��ѹ
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - 7 * PRESS_INTERVAL, Origin_X
				+ (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y - 7
				* PRESS_INTERVAL, paint);
		paint.setStrokeWidth(1);
		canvas.drawText("�����ѹ��", Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL
				+ 3, Origin_Y - 7 * PRESS_INTERVAL + 3, paint);

		// ��ǰ������ѹ��,ѭ��������ѹ����CurrentHighPressInput[]�б�����߶�
		paint.setColor(Color.MAGENTA);
		paint.setStrokeWidth(4);
		for (int i = 1, CurrentHighPressStart_X = Origin_X; i < PressArrayLength; i++, CurrentHighPressStart_X += TIME_INTERVAL) {
			canvas.drawLine(CurrentHighPressStart_X, Origin_Y
					- CurrentHighPressArray[i - 1] / 10 * PRESS_INTERVAL,
					CurrentHighPressStart_X + TIME_INTERVAL, Origin_Y
							- CurrentHighPressArray[i] / 10 * PRESS_INTERVAL,
					paint);
		}

		// ��ǰ������ѹ��,ѭ��������ѹ����CurrentLowPressInput[]�б�����߶�
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(4);
		for (int i = 1, CurrentLowPressStart_X = Origin_X; i < PressArrayLength; i++, CurrentLowPressStart_X += TIME_INTERVAL) {
			canvas.drawLine(CurrentLowPressStart_X, Origin_Y
					- CurrentLowPressArray[i - 1] / 10 * PRESS_INTERVAL,
					CurrentLowPressStart_X + TIME_INTERVAL, Origin_Y
							- CurrentLowPressArray[i] / 10 * PRESS_INTERVAL,
					paint);
		}
	}
}
