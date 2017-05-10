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
 * @Name:自定义血压变化趋势图
 * @Description：血压变化趋势二维坐标图，包括坐标轴，理想高压、低压基准线，动态高压、低压线，分度值等
 * @author 樊俊彬
 * @Time 2014-1-1
 */
public class CustomBloodPressureTrendChart extends View {

	// 框架起点坐标、宽高
	private final int FRAME_X = 10;
	private final int FRAME_Y = 10;
	private final int FRAME_WIDTH = 700;
	private final int FRAME_HEIGHT = 450;

	// 原点坐标
	private final int Origin_X = FRAME_X + 40;
	private final int Origin_Y = FRAME_Y + FRAME_HEIGHT - 40;

	// X轴、Y轴终点坐标
	private final int XAxis_X = FRAME_X + FRAME_WIDTH - 60;
	private final int XAxis_Y = Origin_Y;
	private final int YAxis_X = Origin_X;
	private final int YAxis_Y = FRAME_Y + 20;

	// X轴上的时间分度值(x轴长度/17个基本单位)， y轴上血压分度值(y轴长度/20个基本单位)
	private final int TIME_INTERVAL = (XAxis_X - Origin_X) / 17;
	private final int PRESS_INTERVAL = (Origin_Y - YAxis_Y) / 20;

	// X轴上时间个数,y轴上的血压个数
	private final int TIME_COUNT = (XAxis_X - Origin_X) / TIME_INTERVAL;
	private final int PRESS_COUNT = (Origin_Y - YAxis_Y) / PRESS_INTERVAL;

	// 本地高压和低压值数组（来源于血压数据库）默认为理想血压
	float[] CurrentHighPressArray = { 150, 150, 150, 150, 150, 150,
			150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150 };
	float[] CurrentLowPressArray = { 70, 85, 70, 70, 70, 70, 70, 70, 70, 70,
			70, 70, 70, 70, 70, 70, 70 };
	int PressArrayLength = 1;

	// 计时器与高低压动态更新进程任务
	Timer refreshTimer;
	TimerTask refreshTask;

	public CustomBloodPressureTrendChart(Context context) {
		super(context);
		// 刷新任务:数组长度从1递增到最大长度
		refreshTask = new TimerTask() {
			@Override
			public void run() {
				// 高压动态趋势
				PressArrayLength++;
				postInvalidate();
				if (PressArrayLength == CurrentHighPressArray.length) {
					refreshTimer.cancel();
				}

			}
		};
		// 创建计时器线程，每融一定的时间去执行高压和低压的重绘
		refreshTimer = new Timer();
	}

	// 画布与画笔重绘血压趋势图
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint();

		// 灰色画边框
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(6);
		canvas.drawRect(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT, paint);

		// 画坐标轴
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		// X轴及方向箭头
		canvas.drawLine(Origin_X, Origin_Y, XAxis_X, XAxis_Y, paint);
		canvas.drawLine(XAxis_X, XAxis_Y, XAxis_X - 10, XAxis_Y - 10, paint);
		canvas.drawLine(XAxis_X, XAxis_Y, XAxis_X - 10, XAxis_Y + 10, paint);
		// Y轴及方向箭头
		canvas.drawLine(Origin_X, Origin_Y, YAxis_X, YAxis_Y, paint);
		canvas.drawLine(YAxis_X, YAxis_Y, YAxis_X - 10, YAxis_Y + 10, paint);
		canvas.drawLine(YAxis_X, YAxis_Y, YAxis_X + 10, YAxis_Y + 10, paint);

		// 画X轴上时间刻度(从坐标原点起，每隔TIME_INTERVAL(时间分度)像素画一时间点，到X轴终点至)
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(2);
		for (int i = Origin_X, j = 0; i < XAxis_X - TIME_INTERVAL; i += TIME_INTERVAL, j += 1) {
			canvas.drawText("1-" + j, i - 10, Origin_Y + 20, paint);
		}
		canvas.drawText("日期", XAxis_X + 5, XAxis_Y + 5, paint);

		// 画Y轴上血压刻度(从坐标原点起，每隔10像素画一压力值，到Y轴终点至)
		for (int i = Origin_Y, j = 0; i > YAxis_Y + PRESS_INTERVAL; i -= PRESS_INTERVAL, j += 10) {
			canvas.drawText(j + "", Origin_X - 30, i + 3, paint);
		}
		canvas.drawText("血压/mmgh", YAxis_X - 5, YAxis_Y - 5, paint);

		// 画网格线
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1);
		// 竖线
		for (int i = Origin_X + TIME_INTERVAL; i < XAxis_X - TIME_INTERVAL; i += TIME_INTERVAL) {
			canvas.drawLine(i, Origin_Y, i, Origin_Y - (PRESS_COUNT - 1)
					* PRESS_INTERVAL, paint);
		}
		// 横线
		for (int i = Origin_Y - PRESS_INTERVAL; i > YAxis_Y + PRESS_INTERVAL; i -= PRESS_INTERVAL) {
			canvas.drawLine(Origin_X, i, Origin_X + (TIME_COUNT - 1)
					* TIME_INTERVAL, i, paint);
		}

		// 理想血压基准线
		// 高压
		paint.setColor(Color.MAGENTA);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - 15 * PRESS_INTERVAL, Origin_X
				+ (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y - 15
				* PRESS_INTERVAL, paint);
		paint.setStrokeWidth(1);
		canvas.drawText("理想高压线", Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL
				+ 3, Origin_Y - 15 * PRESS_INTERVAL + 3, paint);
		// 低压
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - 7 * PRESS_INTERVAL, Origin_X
				+ (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y - 7
				* PRESS_INTERVAL, paint);
		paint.setStrokeWidth(1);
		canvas.drawText("理想低压线", Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL
				+ 3, Origin_Y - 7 * PRESS_INTERVAL + 3, paint);

		// 当前测量高压线,循环画出高压数组CurrentHighPressInput[]中保存的线段
		paint.setColor(Color.MAGENTA);
		paint.setStrokeWidth(4);
		for (int i = 1, CurrentHighPressStart_X = Origin_X; i < PressArrayLength; i++, CurrentHighPressStart_X += TIME_INTERVAL) {
			canvas.drawLine(CurrentHighPressStart_X, Origin_Y
					- CurrentHighPressArray[i - 1] / 10 * PRESS_INTERVAL,
					CurrentHighPressStart_X + TIME_INTERVAL, Origin_Y
							- CurrentHighPressArray[i] / 10 * PRESS_INTERVAL,
					paint);
		}

		// 当前测量低压线,循环画出低压数组CurrentLowPressInput[]中保存的线段
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
