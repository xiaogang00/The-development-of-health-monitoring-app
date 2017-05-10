package com.BloodPressure;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @name ���ղ��ҵ��������豸��Ϣ�Ĺ㲥
 * @descripation ע��һ��BroadcastReceiver��ACTION_FOUND����ͨ��Filter������ACTION_FOUND���
 *               Intent�����Ի�ȡÿ��Զ���豸����ϸ��Ϣ��ͨ��Intent�ֶ�EXTRA_DEVICE ��
 *               EXTRA_CLASS���Ի�ð�����ÿ��BluetoothDevice ����Ͷ���ĸ��豸���� BluetoothClass
 * @author ������
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
	Log.w("onReceive()", "�����豸...");
	String action = intent.getAction();
	if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	    BluetoothDevice device = intent
		    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	    Log.i("�豸����", device.getName());
	    Log.i("�豸��ַ", device.getAddress());
	}
    }

}
