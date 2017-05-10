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
 * @Name:模拟血压计<br>
 * 
 * @Description:血压计由画布、外壳、左右刻度线、高低压动态水银柱构成。 定义3个计时器、3个任务线程分别处理高压水银柱的动态上升、
 *                                          低压水银柱动态下降、低压清零。 为了方便界面类对其控制,开放若干内置对象
 * @Date:2013-12-07
 * @author 樊俊彬
 * @version 1.0
 */
public class CustomSphygmomanometerView extends View {

	// 边框的位置（坐标）和大小（长宽）
	private final int FRAME_X = 30;
	private final int FRAME_Y = 30;
	private final int FRAME_WIDTH = 250;
	private final int FRAME_HEIGHT = 900;

	// 水银柱外壳MERCURY大小与位置
	private final int MERCURY_WIDTH = FRAME_WIDTH / 4;
	private final int MERCURY_HEIGHT = FRAME_HEIGHT - 20;
	private final int MERCURY_X = FRAME_X + FRAME_WIDTH / 2 - MERCURY_WIDTH / 2;
	private final int MERCURY_Y = FRAME_Y + 10;

	// 左侧、右侧0刻度线的起始坐标与长度
	private final int RightLineStart_X = MERCURY_X + MERCURY_WIDTH;
	private final int RightLineStart_Y = MERCURY_Y + MERCURY_HEIGHT;
	private final int LeftLineStart_X = MERCURY_X;
	private final int LeftLineStart_Y = MERCURY_Y + MERCURY_HEIGHT;

	// 刻度线长度，左、右侧刻度数目、最小分度
	private final int LINE_LENGTH = 15;
	private final int LINE_COUNT_RIGHT = 200;
	private final int LINE_COUNT_LEFT = 80;
	private final int LINE_INTERVAL_RIGHT = MERCURY_HEIGHT / LINE_COUNT_RIGHT;
	private final int LINE_INTERVAL_LEFT = MERCURY_HEIGHT / LINE_COUNT_LEFT;

	// 外界输入的高压、低压
	int highPressInput, lowPressInput;

	// 高、低压水银柱的动态高度
	int highPressHeight;
	int lowPressHeight;
	int startLowPressHeight = MERCURY_Y;

	// 高、低压水银柱计时器
	Timer highPressTimer, lowPressTimer, lowPressAgainTimer;
	TimerTask highPressTimerTask, lowPressTimerTask, lowPressAgainTimerTask;

	public CustomSphygmomanometerView(Context context) {

		super(context);

		// 低压水银柱下降进程
		lowPressTimerTask = new TimerTask() {
			public void run() {
				lowPressHeight += 1;
				// 低压重绘
				postInvalidate();
				// 达到指定下降高度时停止下降
				if (lowPressHeight == RightLineStart_Y - lowPressInput
						* LINE_INTERVAL_RIGHT - startLowPressHeight)
					lowPressTimer.cancel();

			}
		};

		/*// 低压水银柱二次下降进程
		lowPressAgainTimerTask = new TimerTask() {
			public void run() {
				lowPressHeight += 1;
				// 低压重绘
				postInvalidate();
				// 达到指定下降高度时停止下降
				if (lowPressHeight == highPressHeight)
					lowPressAgainTimer.cancel();
			}
		};*/

		// 高压水银柱上升进程
		highPressTimerTask = new TimerTask() {
			@Override
			public void run() {
				highPressHeight += 1;

				// 高压重绘
				postInvalidate();

				// 达到指定上升高度时停止上升，开始下降
				if (highPressHeight == highPressInput * LINE_INTERVAL_RIGHT) {
					highPressTimer.cancel();

					// 低压水银柱计时器嵌套于高压计时器内部,有先后顺序(高压先走,后低压)
					startLowPressHeight = RightLineStart_Y - highPressHeight;
					lowPressTimer = new Timer();
					lowPressTimer.schedule(lowPressTimerTask, 1000, 20);
				}
			}
		};
		// 创建计时器线程，每融一定的时间去执行高压和低压的重绘
		highPressTimer = new Timer();
		lowPressAgainTimer = new Timer();
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();

		// 黑色画笔粗画边框
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawRect(FRAME_X, FRAME_Y, FRAME_X + FRAME_WIDTH, FRAME_Y
				+ FRAME_HEIGHT, paint);

		// 画水银柱外壳
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.LTGRAY);
		canvas.drawRect(MERCURY_X, MERCURY_Y, MERCURY_X + MERCURY_WIDTH,
				MERCURY_Y + MERCURY_HEIGHT, paint);

		Log.w("刻度数度", String.valueOf(LINE_COUNT_RIGHT));
		Log.w("最小分度", String.valueOf(LINE_INTERVAL_RIGHT));

		// 右侧刻度线,画出所有最小分度线(纵坐标以RightMinLine_Y渐变,每隔LINE_INTERVAL画一最小分度线)
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(0);
		for (int RightMinLine_Y = RightLineStart_Y; RightMinLine_Y > MERCURY_Y; RightMinLine_Y -= LINE_INTERVAL_RIGHT) {
			canvas.drawLine(RightLineStart_X, RightMinLine_Y, RightLineStart_X
					+ LINE_LENGTH, RightMinLine_Y, paint);
		}

		// 右侧刻度线,画出所有10分度线(纵坐标以RightMinLine_Y渐变,每隔10*LINE_INTERVAL画一最小分度线)同时写上刻度
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(1);
		for (int RightMaxLine_Y = RightLineStart_Y, rightText = 0; RightMaxLine_Y > MERCURY_Y; RightMaxLine_Y -= 10 * LINE_INTERVAL_RIGHT, rightText += 10) {

			canvas.drawLine(RightLineStart_X, RightMaxLine_Y, RightLineStart_X
					+ LINE_LENGTH * 2, RightMaxLine_Y, paint);
			paint.setTextSize(20);
			canvas.drawText(rightText + "", RightLineStart_X + LINE_LENGTH * 3,
					RightMaxLine_Y + 4, paint);
		}

		// 左侧刻度线,画出所有最小分度线(纵坐标以LeftMinLine_Y渐变,每隔LINE_INTERVAL画一最小分度线)
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(0);
		for (int LeftMinLine_Y = LeftLineStart_Y; LeftMinLine_Y > MERCURY_Y; LeftMinLine_Y -= LINE_INTERVAL_LEFT) {
			canvas.drawLine(LeftLineStart_X, LeftMinLine_Y, LeftLineStart_X
					- LINE_LENGTH, LeftMinLine_Y, paint);
		}

		// 左侧刻度线,画出所有10分度线(纵坐标以LeftMaxLine_Y渐变,每隔10*LINE_INTERVAL画一最小分度线)同时写上刻度
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(1);
		for (int LeftMinLine_Y = LeftLineStart_Y, leftText = 0; LeftMinLine_Y > MERCURY_Y; LeftMinLine_Y -= 10 * LINE_INTERVAL_LEFT, leftText += 10) {

			canvas.drawLine(LeftLineStart_X, LeftMinLine_Y, LeftLineStart_X
					- LINE_LENGTH * 2, LeftMinLine_Y, paint);
			paint.setTextSize(20);
			canvas.drawText(leftText + "", LeftLineStart_X - LINE_LENGTH * 4,
					LeftMinLine_Y + 4, paint);
		}

		// 高压水银柱
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.RED);
		canvas.drawRect(MERCURY_X, RightLineStart_Y - highPressHeight,
				MERCURY_X + MERCURY_WIDTH, RightLineStart_Y, paint);

		// 低压高压水银柱
		paint.setColor(Color.LTGRAY);
		canvas.drawRect(MERCURY_X, MERCURY_Y, MERCURY_X + MERCURY_WIDTH,
				startLowPressHeight + lowPressHeight, paint);
	}
}