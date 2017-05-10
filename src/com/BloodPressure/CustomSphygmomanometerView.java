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
 * @Name:ģ��Ѫѹ��<br>
 * 
 * @Description:Ѫѹ���ɻ�������ǡ����ҿ̶��ߡ��ߵ�ѹ��̬ˮ�������ɡ� ����3����ʱ����3�������̷ֱ߳����ѹˮ�����Ķ�̬������
 *                                          ��ѹˮ������̬�½�����ѹ���㡣 Ϊ�˷��������������,�����������ö���
 * @Date:2013-12-07
 * @author ������
 * @version 1.0
 */
public class CustomSphygmomanometerView extends View {

	// �߿��λ�ã����꣩�ʹ�С������
	private final int FRAME_X = 30;
	private final int FRAME_Y = 30;
	private final int FRAME_WIDTH = 250;
	private final int FRAME_HEIGHT = 900;

	// ˮ�������MERCURY��С��λ��
	private final int MERCURY_WIDTH = FRAME_WIDTH / 4;
	private final int MERCURY_HEIGHT = FRAME_HEIGHT - 20;
	private final int MERCURY_X = FRAME_X + FRAME_WIDTH / 2 - MERCURY_WIDTH / 2;
	private final int MERCURY_Y = FRAME_Y + 10;

	// ��ࡢ�Ҳ�0�̶��ߵ���ʼ�����볤��
	private final int RightLineStart_X = MERCURY_X + MERCURY_WIDTH;
	private final int RightLineStart_Y = MERCURY_Y + MERCURY_HEIGHT;
	private final int LeftLineStart_X = MERCURY_X;
	private final int LeftLineStart_Y = MERCURY_Y + MERCURY_HEIGHT;

	// �̶��߳��ȣ����Ҳ�̶���Ŀ����С�ֶ�
	private final int LINE_LENGTH = 15;
	private final int LINE_COUNT_RIGHT = 200;
	private final int LINE_COUNT_LEFT = 80;
	private final int LINE_INTERVAL_RIGHT = MERCURY_HEIGHT / LINE_COUNT_RIGHT;
	private final int LINE_INTERVAL_LEFT = MERCURY_HEIGHT / LINE_COUNT_LEFT;

	// �������ĸ�ѹ����ѹ
	int highPressInput, lowPressInput;

	// �ߡ���ѹˮ�����Ķ�̬�߶�
	int highPressHeight;
	int lowPressHeight;
	int startLowPressHeight = MERCURY_Y;

	// �ߡ���ѹˮ������ʱ��
	Timer highPressTimer, lowPressTimer, lowPressAgainTimer;
	TimerTask highPressTimerTask, lowPressTimerTask, lowPressAgainTimerTask;

	public CustomSphygmomanometerView(Context context) {

		super(context);

		// ��ѹˮ�����½�����
		lowPressTimerTask = new TimerTask() {
			public void run() {
				lowPressHeight += 1;
				// ��ѹ�ػ�
				postInvalidate();
				// �ﵽָ���½��߶�ʱֹͣ�½�
				if (lowPressHeight == RightLineStart_Y - lowPressInput
						* LINE_INTERVAL_RIGHT - startLowPressHeight)
					lowPressTimer.cancel();

			}
		};

		/*// ��ѹˮ���������½�����
		lowPressAgainTimerTask = new TimerTask() {
			public void run() {
				lowPressHeight += 1;
				// ��ѹ�ػ�
				postInvalidate();
				// �ﵽָ���½��߶�ʱֹͣ�½�
				if (lowPressHeight == highPressHeight)
					lowPressAgainTimer.cancel();
			}
		};*/

		// ��ѹˮ������������
		highPressTimerTask = new TimerTask() {
			@Override
			public void run() {
				highPressHeight += 1;

				// ��ѹ�ػ�
				postInvalidate();

				// �ﵽָ�������߶�ʱֹͣ��������ʼ�½�
				if (highPressHeight == highPressInput * LINE_INTERVAL_RIGHT) {
					highPressTimer.cancel();

					// ��ѹˮ������ʱ��Ƕ���ڸ�ѹ��ʱ���ڲ�,���Ⱥ�˳��(��ѹ����,���ѹ)
					startLowPressHeight = RightLineStart_Y - highPressHeight;
					lowPressTimer = new Timer();
					lowPressTimer.schedule(lowPressTimerTask, 1000, 20);
				}
			}
		};
		// ������ʱ���̣߳�ÿ��һ����ʱ��ȥִ�и�ѹ�͵�ѹ���ػ�
		highPressTimer = new Timer();
		lowPressAgainTimer = new Timer();
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();

		// ��ɫ���ʴֻ��߿�
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawRect(FRAME_X, FRAME_Y, FRAME_X + FRAME_WIDTH, FRAME_Y
				+ FRAME_HEIGHT, paint);

		// ��ˮ�������
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.LTGRAY);
		canvas.drawRect(MERCURY_X, MERCURY_Y, MERCURY_X + MERCURY_WIDTH,
				MERCURY_Y + MERCURY_HEIGHT, paint);

		Log.w("�̶�����", String.valueOf(LINE_COUNT_RIGHT));
		Log.w("��С�ֶ�", String.valueOf(LINE_INTERVAL_RIGHT));

		// �Ҳ�̶���,����������С�ֶ���(��������RightMinLine_Y����,ÿ��LINE_INTERVAL��һ��С�ֶ���)
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(0);
		for (int RightMinLine_Y = RightLineStart_Y; RightMinLine_Y > MERCURY_Y; RightMinLine_Y -= LINE_INTERVAL_RIGHT) {
			canvas.drawLine(RightLineStart_X, RightMinLine_Y, RightLineStart_X
					+ LINE_LENGTH, RightMinLine_Y, paint);
		}

		// �Ҳ�̶���,��������10�ֶ���(��������RightMinLine_Y����,ÿ��10*LINE_INTERVAL��һ��С�ֶ���)ͬʱд�Ͽ̶�
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(1);
		for (int RightMaxLine_Y = RightLineStart_Y, rightText = 0; RightMaxLine_Y > MERCURY_Y; RightMaxLine_Y -= 10 * LINE_INTERVAL_RIGHT, rightText += 10) {

			canvas.drawLine(RightLineStart_X, RightMaxLine_Y, RightLineStart_X
					+ LINE_LENGTH * 2, RightMaxLine_Y, paint);
			paint.setTextSize(20);
			canvas.drawText(rightText + "", RightLineStart_X + LINE_LENGTH * 3,
					RightMaxLine_Y + 4, paint);
		}

		// ���̶���,����������С�ֶ���(��������LeftMinLine_Y����,ÿ��LINE_INTERVAL��һ��С�ֶ���)
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(0);
		for (int LeftMinLine_Y = LeftLineStart_Y; LeftMinLine_Y > MERCURY_Y; LeftMinLine_Y -= LINE_INTERVAL_LEFT) {
			canvas.drawLine(LeftLineStart_X, LeftMinLine_Y, LeftLineStart_X
					- LINE_LENGTH, LeftMinLine_Y, paint);
		}

		// ���̶���,��������10�ֶ���(��������LeftMaxLine_Y����,ÿ��10*LINE_INTERVAL��һ��С�ֶ���)ͬʱд�Ͽ̶�
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(1);
		for (int LeftMinLine_Y = LeftLineStart_Y, leftText = 0; LeftMinLine_Y > MERCURY_Y; LeftMinLine_Y -= 10 * LINE_INTERVAL_LEFT, leftText += 10) {

			canvas.drawLine(LeftLineStart_X, LeftMinLine_Y, LeftLineStart_X
					- LINE_LENGTH * 2, LeftMinLine_Y, paint);
			paint.setTextSize(20);
			canvas.drawText(leftText + "", LeftLineStart_X - LINE_LENGTH * 4,
					LeftMinLine_Y + 4, paint);
		}

		// ��ѹˮ����
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.RED);
		canvas.drawRect(MERCURY_X, RightLineStart_Y - highPressHeight,
				MERCURY_X + MERCURY_WIDTH, RightLineStart_Y, paint);

		// ��ѹ��ѹˮ����
		paint.setColor(Color.LTGRAY);
		canvas.drawRect(MERCURY_X, MERCURY_Y, MERCURY_X + MERCURY_WIDTH,
				startLowPressHeight + lowPressHeight, paint);
	}
}