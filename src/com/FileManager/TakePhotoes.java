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
    SurfaceHolder surfaceHolder; // surfaceview控件框架//
    int screenWidth, screenHeight;
    Camera camera;
    boolean Preview = false; // 是否在预览中//
    
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	// 设置窗口全屏//
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);

	setContentView(R.layout.photoes_take);
	// 获取窗口管理器//
	WindowManager windowmanager = getWindowManager();
	Display display = windowmanager.getDefaultDisplay();
	DisplayMetrics displaymetrics = new DisplayMetrics();
	// 获取窗口的长和宽//
	display.getMetrics(displaymetrics);
	screenHeight = displaymetrics.heightPixels;
	screenWidth = displaymetrics.widthPixels;

	surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

	surfaceHolder = surfaceView.getHolder(); // 获取holder//

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
		initcream(); // 初始化摄像头//
	    }

	    @Override
	    public void surfaceDestroyed(SurfaceHolder holder)
	    {
		// TODO Auto-generated method stub
		// 如果camera不为null ,释放摄像头//
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
    protected void initcream() // 摄像头初始化方法//
    {
	// TODO Auto-generated method stub
	if (!Preview) { // 通过传参打开摄像头//
	    camera = Camera.open(0);
	    camera.setDisplayOrientation(90);// 旋转90度//
	}
	if (camera != null && !Preview) {
	    try { // camera.parameters为改变参数对象
		Camera.Parameters parameters = camera.getParameters();
		// 设置预览照片的大小//
		parameters.setPreviewSize(screenWidth, screenHeight);
		// 设置预览照片时每秒显示多少帧的最小值和最大值//
		parameters.setPreviewFpsRange(4, 10);
		// 设置图片格式//
		parameters.setPictureFormat(ImageFormat.JPEG);
		// 设置JPG照片的质量//
		parameters.set("jpeg-quality", 85);
		// 设置照片的大小//
		parameters.setPictureSize(screenWidth, screenHeight);
		// 通过SurfaceView显示取景画面//
		camera.setPreviewDisplay(surfaceHolder);
		// 开始预览//
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
	    // 控制摄像头自动对焦后才拍照//
	    camera.autoFocus(autoFocusCallback);
	}
    }

    AutoFocusCallback autoFocusCallback = new AutoFocusCallback() // 对焦回调//
    {
	// 自动对焦回调方法//
	@Override
	public void onAutoFocus(boolean success, Camera camera)
	{
	    // TODO Auto-generated method stub
	    if (success) {
		camera.takePicture(new ShutterCallback()
		{
		    public void onShutter()
		    {
			// 按下快门瞬间会执行此处代码//
		    }
		}, new PictureCallback()
		{
		    public void onPictureTaken(byte[] data, Camera c)
		    {
			// 此处代码可以决定是否需要保存原始照片信息//
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
	    // 根据拍照所得的数据创建位图//
	    final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
		    data.length);
	    // 加载picture_save.xml文件对应的布局资源//
	    View saveDialog = getLayoutInflater().inflate(
		    R.layout.photoes_save, null);
	    final EditText name = (EditText) saveDialog.findViewById(R.id.name);
	    // 获取saveDialog对话框上的ImageView组件//
	    ImageView show = (ImageView) saveDialog.findViewById(R.id.show);
	    // 显示刚刚拍得的照片//
	    show.setImageBitmap(bitmap);
	    // 使用对话框显示saveDialog组件//
	    new AlertDialog.Builder(TakePhotoes.this).setView(saveDialog)
		    .setPositiveButton("保存", new OnClickListener()
		    {
			@Override
			public void onClick(DialogInterface dialogInterface,
				int which)
			{
			    // 创建一个位于SD卡上的文件//
			    File file = new File(Environment
				    .getExternalStorageDirectory(), name
				    .getText().toString() + ".jpg");
			    FileOutputStream outStream = null;
			    try {
				// 打开指定文件对应的输出流//
				outStream = new FileOutputStream(file);
				// 把位图输出到指定文件中//
				bitmap.compress(CompressFormat.JPEG, 100,
					outStream);
				outStream.close();
			    }
			    catch (IOException e) {
				e.printStackTrace();
			    }
			}
		    }).setNegativeButton("取消", null).show();
	    // 重新浏览//
	    camera.stopPreview();
	    camera.startPreview();
	    Preview = true;
	}
    };
}
