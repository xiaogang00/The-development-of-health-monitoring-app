package com.AskHelp;

import com.geniuseoe.demo.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SystemHelp extends Activity {

	private final static String TAG = "Activity6";
	private Button btn_exit;
	private TextView bloodpressure_details;
	private TextView bloodsugar_details;
	private TextView heartlisten_details;
	private Button sendSmsButton;
	private EditText dear_phoneNumber;
	private String mearsureContext;
	private SmsManager smsManager;
	private PendingIntent pendingIntent;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_help_layout);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		bloodpressure_details = (TextView) findViewById(R.id.bloodpressure_details);
		bloodsugar_details = (TextView) findViewById(R.id.bloodsugar_details);
		heartlisten_details = (TextView) findViewById(R.id.heartlisten_details);
		sendSmsButton = (Button) findViewById(R.id.sendImageButton);
		dear_phoneNumber = (EditText) findViewById(R.id.dear_phoneNumber);
		mearsureContext = "本次测量的各项内容如下" + "\n" + "血压信息：高压(150),低压(80),心率(95)";
		
		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);
		smsManager = SmsManager.getDefault();
		
		btn_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});

		bloodpressure_details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent(SystemHelp.this,
						OpreattingDetailsHelp.class);
				Bundle data = new Bundle();
				data.putInt("flag", 1);
				intent.putExtras(data);
				startActivity(intent);
			}
		});

		bloodsugar_details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent(SystemHelp.this,
						OpreattingDetailsHelp.class);
				Bundle data = new Bundle();
				data.putInt("flag", 2);
				intent.putExtras(data);
				startActivity(intent);

			}
		});

		heartlisten_details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent = new Intent(SystemHelp.this,
						OpreattingDetailsHelp.class);
				Bundle data = new Bundle();
				data.putInt("flag", 3);
				intent.putExtras(data);
				startActivity(intent);
			}
		});

		sendSmsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// dearPhoneNumberString =
				// dear_phoneNumber.getText().toString();

				if (dear_phoneNumber.getText().toString().equals("")) {
					Toast.makeText(SystemHelp.this, "请输正确亲情号码",
							Toast.LENGTH_SHORT).show();
				} else {
					smsManager.sendTextMessage(dear_phoneNumber.getText().toString(), null,
							mearsureContext, pendingIntent, null);
					Toast.makeText(SystemHelp.this, "信息已发送", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "=============>onResume");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "=============>onDestroy");
	}
}