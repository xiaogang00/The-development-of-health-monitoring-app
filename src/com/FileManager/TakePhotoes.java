package com.FileManager;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;

import com.geniuseoe.demo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
public class TakePhotoes extends Activity
{
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder; // surfaceview�ؼ����//
    int screenWidth, screenHeight;
    Camera camera;
    boolean Preview = false; // �Ƿ���Ԥ����//
    
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	// ���ô���ȫ��//
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);

	setContentView(R.layout.photoes_take);
	// ��ȡ���ڹ�����//
	WindowManager windowmanager = getWindowManager();
	Display display = windowmanager.getDefaultDisplay();
	DisplayMetrics displaymetrics = new DisplayMetrics();
	// ��ȡ���ڵĳ��Ϳ�//
	display.getMetrics(displaymetrics);
	screenHeight = displaymetrics.heightPixels;
	screenWidth = displaymetrics.widthPixels;

	surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

	surfaceHolder = surfaceView.getHolder(); // ��ȡholder//

	surfaceHolder.addCallback(new Callback()
	{

	    @Override
	    public void surfaceChanged(SurfaceHolder holder, int format,
		    int width, int height)
	    {
		// TODO Auto-generated method stub

	    }

	    @Override
	    public void surfaceCreated(SurfaceHolder holder)
	    {
		// TODO Auto-generated method stub
		initcream(); // ��ʼ������ͷ//
	    }

	    @Override
	    public void surfaceDestroyed(SurfaceHolder holder)
	    {
		// TODO Auto-generated method stub
		// ���camera��Ϊnull ,�ͷ�����ͷ//
		if (camera != null) {
		    if (Preview) camera.stopPreview();
		    camera.release();
		    camera = null;
		}
	    }
	});
     /*****************************************/
    }
    
    
    
    /******************************************/
    protected void initcream() // ����ͷ��ʼ������//
    {
	// TODO Auto-generated method stub
	if (!Preview) { // ͨ�����δ�����ͷ//
	    camera = Camera.open(0);
	    camera.setDisplayOrientation(90);// ��ת90��//
	}
	if (camera != null && !Preview) {
	    try { // camera.parametersΪ�ı��������
		Camera.Parameters parameters = camera.getParameters();
		// ����Ԥ����Ƭ�Ĵ�С//
		parameters.setPreviewSize(screenWidth, screenHeight);
		// ����Ԥ����Ƭʱÿ����ʾ����֡����Сֵ�����ֵ//
		parameters.setPreviewFpsRange(4, 10);
		// ����ͼƬ��ʽ//
		parameters.setPictureFormat(ImageFormat.JPEG);
		// ����JPG��Ƭ������//
		parameters.set("jpeg-quality", 85);
		// ������Ƭ�Ĵ�С//
		parameters.setPictureSize(screenWidth, screenHeight);
		// ͨ��SurfaceView��ʾȡ������//
		camera.setPreviewDisplay(surfaceHolder);
		// ��ʼԤ��//
		camera.startPreview();
	    }
	    catch (Exception e) {
		e.printStackTrace();
	    }
	    Preview = true;
	}
    }
    /**************************************/
    
    
    /*************************************/
    public void capture(View source)
    {
	if (camera != null) {
	    // ��������ͷ�Զ��Խ��������//
	    camera.autoFocus(autoFocusCallback);
	}
    }

    AutoFocusCallback autoFocusCallback = new AutoFocusCallback() // �Խ��ص�//
    {
	// �Զ��Խ��ص�����//
	@Override
	public void onAutoFocus(boolean success, Camera camera)
	{
	    // TODO Auto-generated method stub
	    if (success) {
		camera.takePicture(new ShutterCallback()
		{
		    public void onShutter()
		    {
			// ���¿���˲���ִ�д˴�����//
		    }
		}, new PictureCallback()
		{
		    public void onPictureTaken(byte[] data, Camera c)
		    {
			// �˴�������Ծ����Ƿ���Ҫ����ԭʼ��Ƭ��Ϣ//
		    }
		}, JpegCallback);

	    }
	}
    };

    PictureCallback JpegCallback = new PictureCallback()
    {
	@Override
	public void onPictureTaken(byte[] data, Camera camera)
	{
	    // �����������õ����ݴ���λͼ//
	    final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
		    data.length);
	    // ����picture_save.xml�ļ���Ӧ�Ĳ�����Դ//
	    View saveDialog = getLayoutInflater().inflate(
		    R.layout.photoes_save, null);
	    final EditText name = (EditText) saveDialog.findViewById(R.id.name);
	    // ��ȡsaveDialog�Ի����ϵ�ImageView���//
	    ImageView show = (ImageView) saveDialog.findViewById(R.id.show);
	    // ��ʾ�ո��ĵõ���Ƭ//
	    show.setImageBitmap(bitmap);
	    // ʹ�öԻ�����ʾsaveDialog���//
	    new AlertDialog.Builder(TakePhotoes.this).setView(saveDialog)
		    .setPositiveButton("����", new OnClickListener()
		    {
			@Override
			public void onClick(DialogInterface dialogInterface,
				int which)
			{
			    // ����һ��λ��SD���ϵ��ļ�//
			    File file = new File(Environment
				    .getExternalStorageDirectory(), name
				    .getText().toString() + ".jpg");
			    FileOutputStream outStream = null;
			    try {
				// ��ָ���ļ���Ӧ�������//
				outStream = new FileOutputStream(file);
				// ��λͼ�����ָ���ļ���//
				bitmap.compress(CompressFormat.JPEG, 100,
					outStream);
				outStream.close();
			    }
			    catch (IOException e) {
				e.printStackTrace();
			    }
			}
		    }).setNegativeButton("ȡ��", null).show();
	    // �������//
	    camera.stopPreview();
	    camera.startPreview();
	    Preview = true;
	}
    };
}
