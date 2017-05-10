package com;

import com.Register.RegisterActivity;
import com.Register.UserDAO;
import com.geniuseoe.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	/**
	 * 用户登陆类,用户输入的信息与注册时填写的信息进行比对下发登陆使用权限
	 * 
	 * @Author:樊俊彬
	 * @Time: 2013-10-01
	 * @version:1.0
	 */

	Button registerButton, loginButton;
	EditText login_username, login_password;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		login_username = (EditText) findViewById(R.id.login_username);
		login_password = (EditText) findViewById(R.id.login_password);

		// 执行登陆
		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = login_username.getText().toString();
				String password = login_password.getText().toString();
				UserDAO uService = new UserDAO(LoginActivity.this);
				// 登陆成功失败的标记
				boolean flag = uService.login(username, password);
				if (flag) {
					Toast.makeText(LoginActivity.this, "登录成功",
							Toast.LENGTH_LONG).show();

					Bundle data = new Bundle();
					data.putString("username", username);

					// /////////////////执行登陆，转到MainTabHost

					Intent intent = new Intent(LoginActivity.this,
							MainTabHost.class);
					intent.putExtras(data);
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(LoginActivity.this, "用户名或密码错误",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		// 转交注册账号类处理
		registerButton = (Button) findViewById(R.id.registerButton);
		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoginActivity.this,
						RegisterActivity.class));
			}
		});
	}
}
