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
 * @Name:自定义血糖仪
 * 
 * @Description
 * 
 * @Date:2014-2-2
 * @author 樊俊彬
 * @version 1.0
 */
public class CustomGlucometerView extends View {

	// 显示器位置和大小
	private final int FRAME_X = 80;
	private final int FRAME_Y = 40;
	private final int FRAME_WIDTH = 230;
	private final int FRAME_HEIGHT = 130;

	// 接收外界输入的血糖浓度值
	int inputVBGText;
	int inputFPGText;
	
	// 血糖数值变量(0-bloodSugarInput)
	private int bloodSugarData = 0;

	// 计时器与血糖数值递增任务进程
	Timer timer;
	TimerTask timerTask;

	public CustomGlucometerView(Context context) {
		super(context);

		// BloodsugarData 从0开始以0.1/单位时间的速度递增,达到指定大小时停止
		timerTask = new TimerTask() {
			@Override
			public void run() {
				bloodSugarData += 1;
				// 重绘
				postInvalidate();
				// 达到指定大小停止

				if (bloodSugarData == inputVBGText) {
					timer.cancel();
				}
			}
		};
		// 创建计时器线程，每融一定的时间去执行
		timer = new Timer();
	}

	// 重绘显示器
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();

		// 黑色画笔粗画边框
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawRect(FRAME_X, FRAME_Y, FRAME_X + FRAME_WIDTH, FRAME_Y
				+ FRAME_HEIGHT, paint);

		// 显示器上显示血糖数值
		paint.setColor(Color.BLACK);
		paint.setTextSize(80);
		canvas.drawText(String.valueOf(bloodSugarData), FRAME_X + FRAME_WIDTH
				/ 2 - 20, FRAME_Y + FRAME_HEIGHT / 2 + 20, paint);
	}

}
