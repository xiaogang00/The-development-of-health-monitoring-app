package com.Register;

import com.geniuseoe.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * 用户注册类,该类中用户注册时填写的信息保存在本地SQLite数据库user.db中
 * 
 * @author 樊俊彬
 * @Time 2013-10-01
 */
public class RegisterActivity extends Activity {

	EditText reg_username, reg_password, reg_name, reg_age, reg_phone;
	RadioGroup reg_sex;
	Button reg_register;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);

		// 注册输入信息
		reg_username = (EditText) findViewById(R.id.reg_account);
		reg_password = (EditText) findViewById(R.id.reg_password);
		reg_name = (EditText) findViewById(R.id.reg_name);
		reg_sex = (RadioGroup) findViewById(R.id.reg_sex);
		reg_age = (EditText) findViewById(R.id.reg_age);
		reg_phone = (EditText) findViewById(R.id.reg_phone);
		reg_register = (Button) findViewById(R.id.reg_register);

		reg_register.setOnClickListener(new OnClickListener() {
			public void onClick(View source) {
				// 要求填写完整的信息
				if (reg_username.getText().toString().equals("")
						|| reg_password.getText().toString().equals("")
						|| reg_name.getText().toString().equals("")
						|| reg_age.getText().toString().equals("")
						|| reg_phone.getText().toString().equals("")) {
					Toast.makeText(RegisterActivity.this, "信息填写不完整",
							Toast.LENGTH_SHORT).show();
				} else {
					// 输入的各项信息转为字符
					String username = reg_username.getText().toString().trim();
					String password = reg_password.getText().toString().trim();
					String name = reg_name.getText().toString().trim();
					String sex = "";
					for (int i = 0; i < reg_sex.getChildCount(); i++) {
						RadioButton radioButton = (RadioButton) reg_sex
								.getChildAt(i);
						if (radioButton.isChecked()) {
							sex = radioButton.getText().toString();
							break;
						}
					}
					String age = reg_age.getText().toString().trim();
					String phone = reg_phone.getText().toString().trim();

					// 填写的信息由用户类收集后统一存入数据库
					User user = new User();
					user.setUsername(username);
					user.setPassword(password);
					user.setName(name);
					user.setSex(sex);
					user.setAge(Integer.parseInt(age));
					user.setPhone(phone);
					
					// 存入数据库
					UserDAO uService = new UserDAO(
							RegisterActivity.this);
					uService.register(user);
					
					Toast.makeText(RegisterActivity.this, "注册成功",
							Toast.LENGTH_LONG).show();
					finish();
				}
			}
		});
	}
}