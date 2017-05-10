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
 * @name ���������߳�
 * @descripation ���������ز������������������֡���ѯ����豸��Ϣ��������Ŀ���豸�����ӡ��������ݴ��䡢�ͷ����ӵ�
 * @author ������
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
	 * ���췽��:��ȡ��������ʵ�����������������豸��Ϣ����ѯ����Ե��豸
	 * 
	 * @param handler
	 * @throws IOException
	 */
	public BluetoothDeviceAdapter(Context context, Handler handler) {
		Log.w("BluetoothDeviceAdapter()", "��ȡ��������ʵ�����������������豸��Ϣ����ѯ����Ե��豸");
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		openBluetoothAdapter();
		// registerAndDiscover();
		queryPairedDevicesInfo();
		this.context = context;
		this.handlerBluetooth = handler;
		measureResult = new HashMap<String, String>();
	}

	/**
	 * ���ݵ�ǰ����������������״̬ѡ����ѯ���û�������
	 */
	protected void openBluetoothAdapter() {
		Log.w("openBluetoothAdapter()", "�򿪱�������" + bluetoothAdapter.getName());
		if (bluetoothAdapter != null) {
			if (!bluetoothAdapter.isEnabled()) {
				bluetoothAdapter.enable();
				Log.i("openBluetoothAdapter", "��ǰ״̬Ϊ�رգ�ϵͳ�Զ���");
			}

		} else {
			Log.i("openBluetoothAdapter()", "�����豸�����쳣!");
		}
	}

	/**
	 * ע��㲥�¼�������������ʼɨ�跢���豸
	 */
	private void registerAndDiscover() {
		Log.w("registerScan()", "ע��㲥�¼���׼��ɨ�跢���ܱ��豸");
		bluetoothReceiver = new BluetoothReciever();
		IntentFilter infilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(bluetoothReceiver, infilter);
		if (bluetoothAdapter.startDiscovery()) {
			Log.i("bluetoothAdapter", "��ʼɨ��");
		}
	}

	/**
	 * ��ѯ����Ե��豸
	 */
	private void queryPairedDevicesInfo() {
		// ͨ��getBondedDevices��������ȡ�Ѿ��뱾�豸��Ե�Զ���豸��Ϣ�б�
		Log.w("queryPairedDevicesInfo()", "��ѯ����Ե��豸");
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				Log.i("����Ե��豸����", device.getName());
				Log.i("����Ե��豸��ַ", device.getAddress());
				// ��������Եģ�����Ŀ�괴��Զ��bluetoothDevice
				if (DEVICE_NAME.equals(device.getName())) {
					Log.w("����Ŀ���豸�����˴���Զ�̶�", DEVICE_NAME);
					bluetoothDevice = device;
					break;
				}
			}
		}
		if (bluetoothDevice == null)
			Log.i("queryPairedDevices2()", "û����Ŀ��Զ�̶���Ե���Ϣ");
	}

	/**
	 * �߳��壺ִ�����ӺͶ�ȡ����
	 */
	@Override
	public void run() {
		Log.w("run()", "�߳��壺ִ�����ӺͶ�ȡ����");
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
	 * ���������˽�������
	 */
	private void connect() {

		Log.w("connect()", "���������˽�������");
		// �ͻ���bluetoothDevice������Server��������socket
		BluetoothSocket socket = null;
		try {
			socket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
			socket.connect();
			if (socket.isConnected()) {
				Log.i("connect()", "�ɹ�����");
			}
			inputStream = socket.getInputStream();
		} catch (IOException e) {
			Log.e("connect()", "�����쳣");
			destory();
		}
	}

	/**
	 * ��ȡsocket��InputStream����������
	 */
	protected void readData() throws IOException {

		Log.w("read()", "��ʼ��ȡsocket��InputStream");
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
					Log.i("Thread.sleep(100);", "�߳�����");
					Thread.sleep(100);
				}
			}

			// ��������ѹ����ѹ����������
			String strTemp = this.parseData(dataBuf);

			String highBloodMeasure = Integer.valueOf(strTemp.substring(4, 8),
					16).toString();
			String lowBloodMeasure = Integer.valueOf(strTemp.substring(8, 10),
					16).toString();
			String pulseRate = Integer.valueOf(strTemp.substring(10, 12), 16)
					.toString();
			Log.i("�������ĸ�ѹ����", highBloodMeasure);
			Log.i("�������ĵ�ѹ����", lowBloodMeasure);
			Log.i("������������", pulseRate);
			measureResult.put("highBloodMeasure", highBloodMeasure);
			measureResult.put("lowBloodMeasure", lowBloodMeasure);
			measureResult.put("pulseRate", pulseRate);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("�������ݴ����쳣!");
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException("δ֪�쳣��������������!");
		} finally {
			destory();
		}

	}

	/**
	 * ����byte[]�е��ֽ������ַ���cs��
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
	 * �ر�������������ͷ����ӣ��ر�����
	 */
	protected void destory() {
		Log.w("destory()", "�ر�������������ͷ����ӣ��ر�����");
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
