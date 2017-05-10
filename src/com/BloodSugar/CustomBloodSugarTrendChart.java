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
 * @Name:自定义血糖变化趋势图
 * @Description：血糖变化趋势二维坐标图，包括坐标轴，全血血糖最高最低区间，血浆血糖最高最低区间、糖尿病全血血糖和血浆血糖，本次测量的全血血糖和血浆血糖浓度
 * @author 樊俊彬
 * @Time 2014-2-5
 */
public class CustomBloodSugarTrendChart extends View {

	// 框架起点坐标、宽高
	private final int FRAME_X = 10;
	private final int FRAME_Y = 10;
	private final int FRAME_WIDTH = 700;
	private final int FRAME_HEIGHT = 570;

	// 原点坐标
	private final int Origin_X = FRAME_X + 40;
	private final int Origin_Y = FRAME_Y + FRAME_HEIGHT - 40;

	// X轴、Y轴终点坐标
	private final int XAxis_X = FRAME_X + FRAME_WIDTH - 60;
	private final int XAxis_Y = Origin_Y;
	private final int YAxis_X = Origin_X;
	private final int YAxis_Y = FRAME_Y + 25;

	// 分度值(x轴长度/17次)， y轴上血压分度值(y轴长度/10个基本单位)
	private final int TIME_INTERVAL = (XAxis_X - Origin_X) / 17;
	private final int PRESS_INTERVAL = (Origin_Y - YAxis_Y) / 11;

	// X轴上时间个数,y轴上的血压个数
	private final int TIME_COUNT = (XAxis_X - Origin_X) / TIME_INTERVAL;
	private final int PRESS_COUNT = (Origin_Y - YAxis_Y) / PRESS_INTERVAL;

	// 本地全血血糖和血浆血糖浓度数组（来源于血糖数据库）
	float[] CurrentVBG = { 90, 100, 80, 85, 95, 100, 102, 94, 95, 100, 92, 80,
			89, 93, 101, 98, 95 };
	float[] CurrentFPG = { 110, 120, 123, 120, 116, 128, 117, 105, 114, 105,
			117, 106, 110, 114, 120, 124, 110 };
	int PressArrayLength = 1;

	// 计时器与高低压动态更新进程任务
	Timer refreshTimer;
	TimerTask refreshTask;

	public CustomBloodSugarTrendChart(Context context) {
		super(context);

		refreshTask = new TimerTask() {
			@Override
			public void run() {

				// 刷新任务:数组长度从1递增到最大长度
				PressArrayLength++;
				postInvalidate();
				if (PressArrayLength == CurrentVBG.length) {
					refreshTimer.cancel();
				}

			}
		};
		// 创建计时器线程，每融一定的时间去执行高压和低压的重绘
		refreshTimer = new Timer();
	}

	// 画布与画笔重绘趋势图
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

		// 画Y轴上血糖浓度刻度(从坐标原点(50mg/dL)起，每隔PRESS_INTERVAL像素画一浓度值，到Y轴终点至)
		paint.setTextSize(15);
		for (int i = Origin_Y, j = 50; i > YAxis_Y + PRESS_INTERVAL; i -= PRESS_INTERVAL, j += 10) {
			canvas.drawText(j + "", Origin_X - 30, i + 3, paint);
		}
		canvas.drawText("空腹血糖浓度(mg/dL)", YAxis_X - 15, YAxis_Y - 5, paint);

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

		// 空腹血糖正常区间：全血血糖为3.9～6.1mmol/L(70～110mg/mL)，血浆血糖为3.9～6.9mmol/L(70～125mg/dL)

		// 空腹血糖最低70mg/mL
		paint.setColor(Color.DKGRAY);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - (7 - 5) * PRESS_INTERVAL, Origin_X
				+ (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y - (7 - 5)
				* PRESS_INTERVAL, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize(20);
		canvas.drawText("最低全血血浆", Origin_X + (TIME_COUNT - 3) * TIME_INTERVAL,
				Origin_Y - (7 - 5) * PRESS_INTERVAL + 20, paint);

		// 空腹全血血糖最高110mg/mL
		paint.setColor(Color.MAGENTA);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - (11 - 5) * PRESS_INTERVAL,
				Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y
						- (11 - 5) * PRESS_INTERVAL, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize(20);
		canvas.drawText("最高全血", Origin_X + (TIME_COUNT - 3) * TIME_INTERVAL,
				Origin_Y - (11 - 5) * PRESS_INTERVAL + 20, paint);

		// 糖尿病全血血糖120mg/mL
		paint.setColor(Color.MAGENTA);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - (12 - 5) * PRESS_INTERVAL,
				Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y
						- (12 - 5) * PRESS_INTERVAL, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize(20);
		canvas.drawText("糖尿病全血", Origin_X + (TIME_COUNT - 3) * TIME_INTERVAL,
				Origin_Y - (12 - 5) * PRESS_INTERVAL + 20, paint);

		// 空腹血浆血糖最高130mg/dL
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - (13 - 5) * PRESS_INTERVAL,
				Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y
						- (13 - 5) * PRESS_INTERVAL, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize(20);
		canvas.drawText("最高血浆", Origin_X + (TIME_COUNT - 3) * TIME_INTERVAL,
				Origin_Y - (13 - 5) * PRESS_INTERVAL + 20, paint);

		// 糖尿病血浆血糖140mg/dL
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawLine(Origin_X, Origin_Y - (14 - 5) * PRESS_INTERVAL,
				Origin_X + (TIME_COUNT - 1) * TIME_INTERVAL, Origin_Y
						- (14 - 5) * PRESS_INTERVAL, paint);
		paint.setStrokeWidth(0);
		paint.setTextSize(20);
		canvas.drawText("糖尿病血浆", Origin_X + (TIME_COUNT - 3) * TIME_INTERVAL,
				Origin_Y - (14 - 5) * PRESS_INTERVAL + 20, paint);

		// 当前测量全血血糖线,循环画出全血血糖数组CurrentVBG[]中保存的线段
		paint.setColor(Color.MAGENTA);
		paint.setStrokeWidth(4);
		for (int i = 1, CurrentHighPressStart_X = Origin_X; i < PressArrayLength; i++, CurrentHighPressStart_X += TIME_INTERVAL) {
			canvas.drawLine(CurrentHighPressStart_X, Origin_Y
					- (CurrentVBG[i - 1] / 10 - 5) * PRESS_INTERVAL,
					CurrentHighPressStart_X + TIME_INTERVAL, Origin_Y
							- (CurrentVBG[i] / 10 - 5) * PRESS_INTERVAL, paint);
		}

		// 当前测量血浆血糖线,循环画出血浆血糖数组CurrentFPG[]中保存的线段
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
