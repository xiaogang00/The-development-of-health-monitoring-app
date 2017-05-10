package com.AskHelp;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.geniuseoe.demo.R;

public class OpreattingDetailsHelp extends Activity {

	private TextView details_title;
	private TextView details_content;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_use_help_details);
		details_title = (TextView) findViewById(R.id.details_title);
		details_content = (TextView) findViewById(R.id.details_content);
		Intent intent = getIntent();
		Bundle data = intent.getExtras();

		switch (data.getInt("flag")) {
		case 1: {
			details_title.setText("ÑªÑ¹¼ÆÊ¹ÓÃ°ïÖú");
			details_content.setText("ÑªÑ¹");
		}

			break;
		case 2: {
			details_title.setText("ÑªÌÇÒÇÊ¹ÓÃ°ïÖú");
			details_content.setText("ÑªÌÇ");
		}

			break;
		case 3: {
			details_title.setText("ĞÄÔàÌıÕï°ïÖú");
			details_content.setText("ÌıÕï");
		}

			break;

		default:
			break;
		}

	}
}
