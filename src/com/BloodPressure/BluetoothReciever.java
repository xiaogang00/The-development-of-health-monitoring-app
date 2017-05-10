package com.BloodPressure;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @name 接收查找到的蓝牙设备信息的广播
 * @descripation 注册一个BroadcastReceiver的ACTION_FOUND对象，通过Filter来过滤ACTION_FOUND这个
 *               Intent动作以获取每个远程设备的详细信息，通过Intent字段EXTRA_DEVICE 和
 *               EXTRA_CLASS可以获得包含了每个BluetoothDevice 对象和对象的该设备类型 BluetoothClass
 * @author 樊俊彬
 * @date 2014-3-22
 * @version 1.0
 */
public class BluetoothReciever extends BroadcastReceiver
{

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent)
    {
	// TODO Auto-generated method stub
	Log.w("onReceive()", "发现设备...");
	String action = intent.getAction();
	if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	    BluetoothDevice device = intent
		    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	    Log.i("设备名称", device.getName());
	    Log.i("设备地址", device.getAddress());
	}
    }

}
