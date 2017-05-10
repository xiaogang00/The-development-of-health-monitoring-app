package com.BloodPressure;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @name 蓝牙操作线程
 * @descripation 完成蓝牙相关操作：开启、搜索发现、查询配对设备信息、建立与目标设备的连接、进行数据传输、释放连接等
 * @author 樊俊彬
 * @date 2014-3-22
 * @version 1.0
 */
@SuppressLint("NewApi")
public class BluetoothDeviceAdapter implements Runnable {

	protected Context context;
	protected UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	protected Map<String, String> measureResult;
	protected BluetoothDevice bluetoothDevice;
	protected BluetoothAdapter bluetoothAdapter;
	protected static BluetoothReciever bluetoothReceiver;
	protected BluetoothSocket socket;
	protected Handler handlerBluetooth;
	protected InputStream inputStream;
	protected OutputStream outputStream;

	public int dataLeng = 32;
	private byte dataHead = (byte) 0x0A;
	private String DEVICE_NAME = "BP:HC-503B";

	/**
	 * 构造方法:获取本地蓝牙实例，打开蓝牙，搜索设备信息，查询已配对的设备
	 * 
	 * @param handler
	 * @throws IOException
	 */
	public BluetoothDeviceAdapter(Context context, Handler handler) {
		Log.w("BluetoothDeviceAdapter()", "获取本地蓝牙实例，打开蓝牙，搜索设备信息，查询已配对的设备");
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		openBluetoothAdapter();
		// registerAndDiscover();
		queryPairedDevicesInfo();
		this.context = context;
		this.handlerBluetooth = handler;
		measureResult = new HashMap<String, String>();
	}

	/**
	 * 根据当前本地蓝牙适配器的状态选择性询问用户启动它
	 */
	protected void openBluetoothAdapter() {
		Log.w("openBluetoothAdapter()", "打开本地蓝牙" + bluetoothAdapter.getName());
		if (bluetoothAdapter != null) {
			if (!bluetoothAdapter.isEnabled()) {
				bluetoothAdapter.enable();
				Log.i("openBluetoothAdapter", "当前状态为关闭，系统自动打开");
			}

		} else {
			Log.i("openBluetoothAdapter()", "本地设备驱动异常!");
		}
	}

	/**
	 * 注册广播事件监听器，并开始扫描发现设备
	 */
	private void registerAndDiscover() {
		Log.w("registerScan()", "注册广播事件并准备扫描发现周边设备");
		bluetoothReceiver = new BluetoothReciever();
		IntentFilter infilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(bluetoothReceiver, infilter);
		if (bluetoothAdapter.startDiscovery()) {
			Log.i("bluetoothAdapter", "开始扫描");
		}
	}

	/**
	 * 查询已配对的设备
	 */
	private void queryPairedDevicesInfo() {
		// 通过getBondedDevices方法来获取已经与本设备配对的远程设备信息列表
		Log.w("queryPairedDevicesInfo()", "查询已配对的设备");
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				Log.i("已配对的设备名称", device.getName());
				Log.i("已配对的设备地址", device.getAddress());
				// 查找已配对的，按此目标创建远程bluetoothDevice
				if (DEVICE_NAME.equals(device.getName())) {
					Log.w("发现目标设备，按此创建远程端", DEVICE_NAME);
					bluetoothDevice = device;
					break;
				}
			}
		}
		if (bluetoothDevice == null)
			Log.i("queryPairedDevices2()", "没有与目标远程端配对的信息");
	}

	/**
	 * 线程体：执行连接和读取数据
	 */
	@Override
	public void run() {
		Log.w("run()", "线程体：执行连接和读取数据");
		// TODO Auto-generated method stub
		try {
			connect();
			readData();
		} catch (IOException e) {
			measureResult.put("errorInfo", e.getMessage());
		}
		Message msg = handlerBluetooth.obtainMessage();
		msg.obj = this.measureResult;
		handlerBluetooth.sendMessage(msg);
		Log.i("AbstractedAdapter", "run()");

	}

	/**
	 * 请求与服务端建立连接
	 */
	private void connect() {

		Log.w("connect()", "请求与服务端建立连接");
		// 客户端bluetoothDevice请求与Server建立连接socket
		BluetoothSocket socket = null;
		try {
			socket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
			socket.connect();
			if (socket.isConnected()) {
				Log.i("connect()", "成功连接");
			}
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			Log.e("connect()", "连接异常");
			destory();
		}
	}

	/**
	 * 读取socket上InputStream输入流数据
	 */
	protected void readData() throws IOException {

		Log.w("read()", "开始读取socket上InputStream");
		byte[] dataBuf = new byte[dataLeng];
		int recTotalCount = 0;

		try {
			if (inputStream == null) {
				destory();
				return;
			}
			
			int count = 0;
			while (true) {
				count = inputStream.available();
				Log.i("count", String.valueOf(count));
				Log.i("inputStream.available()",
						String.valueOf(inputStream.available()));
				if (count > 0) {
					int readCount = inputStream.read(dataBuf, recTotalCount,
							count);
					recTotalCount += readCount;

					if (readCount == dataLeng) {
						break;
					}
				} else {
					Log.i("Thread.sleep(100);", "线程阻塞");
					Thread.sleep(100);
				}
			}

			// 解析到高压、低压、心率数据
			String strTemp = this.parseData(dataBuf);

			String highBloodMeasure = Integer.valueOf(strTemp.substring(4, 8),
					16).toString();
			String lowBloodMeasure = Integer.valueOf(strTemp.substring(8, 10),
					16).toString();
			String pulseRate = Integer.valueOf(strTemp.substring(10, 12), 16)
					.toString();
			Log.i("测量到的高压数据", highBloodMeasure);
			Log.i("测量到的低压数据", lowBloodMeasure);
			Log.i("测量到的心率", pulseRate);
			measureResult.put("highBloodMeasure", highBloodMeasure);
			measureResult.put("lowBloodMeasure", lowBloodMeasure);
			measureResult.put("pulseRate", pulseRate);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("蓝牙数据传送异常!");
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException("未知异常，建议重启程序!");
		} finally {
			destory();
		}

	}

	/**
	 * 解析byte[]中的字节流到字符串cs中
	 * 
	 * @param bs
	 * @return
	 */
	private String parseData(byte[] bs) {
		Log.i("parseData()", "---");
		char[] cs = new char[bs.length];
		for (int i = 0; i < bs.length; i++) {
			cs[i] = (char) bs[i];
		}
		return new String(cs);

	}

	/**
	 * 关闭输入输出流，释放连接，关闭蓝牙
	 */
	protected void destory() {
		Log.w("destory()", "关闭输入输出流，释放连接，关闭蓝牙");
		try {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}

			if (socket != null) {
				socket.close();
				socket = null;
			}
			/*
			 * if (bluetoothAdapter != null) { bluetoothAdapter.disable(); }
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
