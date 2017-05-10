package com;

import com.geniuseoe.demo.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 * 
 * @author 樊俊彬
 * @version 1.0
 * @created 2013-10-01
 */
public class WelcomeActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.welcome, null);
		setContentView(view);

		// 渐变展示启动屏(渐变度与持续时间)
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});
	}

	// 跳转到登陆界面LoginActivity
	private void redirectTo() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}