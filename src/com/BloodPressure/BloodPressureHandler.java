package com.BloodPressure;

import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @name 按消息处理数据
 * @descripation BluetoothHC503
 * @author 樊俊彬
 * @date 2014-3-22
 * @version 1.0
 */
public class BloodPressureHandler extends Handler {
	
	private Context context;
	private EditText txtHighBlood; // 血压(mmHg)1
	private EditText txtLowBlood; // 血压(mmHg)2
	private EditText txtRate; // 心率
	
	public BloodPressureHandler(Context context){
		this.context = context;	
	}
	public void handleMessage(Message msg) {
		//把消息上携带的obj给Map<> bluetoothMeasureData
		Map<String, String> bluetoothMeasureData = (Map) msg.obj;
		if (bluetoothMeasureData == null || bluetoothMeasureData.isEmpty())
			return;
		String errorInfo = bluetoothMeasureData.get("errorInfo");
		Log.i("测量到的高压数据", bluetoothMeasureData.get("highBloodMeasure"));
		Log.i("测量到的低压数据", bluetoothMeasureData.get("lowBloodMeasure"));
		Log.i("测量到的心率", bluetoothMeasureData.get("pulseRate"));
		Log.i("错误信息", bluetoothMeasureData.get("errorInfo"));
	}

}
