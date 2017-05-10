package com.HeartAuscultation;

import java.io.File;

import com.geniuseoe.demo.R;
import android.app.Activity;
import android.app.TabActivity;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

public class HeartListen extends TabActivity
{
    /*******************************/
    SurfaceView sfv;
    ClsOscilloscope clsOscilloscope = new ClsOscilloscope();
    static final int frequency = 8000;// 分辨率
    static final int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    static final int xMax = 6;// X轴缩小比例最大值,X轴数据量巨大，容易产生刷新延时
    static final int xMin = 1;// X轴缩小比例最小值
    static final int yMax = 1 / 2;// Y轴缩小比例最大值
    static final int yMin = 8;// Y轴缩小比例最小值

    int recBufSize;// 录音最小buffer大小
    AudioRecord audioRecord;
    Paint mPaint;
    /*******************************/

    /******************************/

    File soundFile;
    MediaRecorder mRecorder;
    Button button1, button2;

    /******************************/

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	TabHost tabHost = getTabHost();
	LayoutInflater.from(this).inflate(R.layout.heart_listen,
		tabHost.getTabContentView(), true);
	// 添加第一个标签页
	tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("心脏听诊")
		.setContent(R.id.tab01));
	// 添加第二个标签页
	tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("诊断结论")
		.setContent(R.id.tab02));
	// 添加第三个标签页
	tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("操作说明")
		.setContent(R.id.tab03));
	
	
	Button button1=(Button)findViewById(R.id.button1);
	Button button2=(Button)findViewById(R.id.button2);
	Button button3=(Button)findViewById(R.id.button3);
	
	/************************************/
	// 录音组件
		recBufSize = AudioRecord.getMinBufferSize(frequency,
		channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
		channelConfiguration, audioEncoding, recBufSize);
		// 画板和画笔s
		sfv = (SurfaceView) this.findViewById(R.id.surfaceViewc);
		//sfv.setOnTouchListener(new TouchEvent());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLUE);// 画笔为绿色
		mPaint.setStrokeWidth(7);// 设置画笔粗细
		
	/**********************************/	
		
		
		
		
		
		button1.setOnClickListener(new OnClickListener()
		{
		    
		    @Override
		    public void onClick(View arg0)
		    {
			// TODO Auto-generated method stub

	                clsOscilloscope.initOscilloscope(xMax / 2, yMax / 2,
				sfv.getHeight());
			clsOscilloscope.baseLine = sfv.getHeight() / 2;
			clsOscilloscope.Start(audioRecord, recBufSize, sfv, mPaint);
		    }
		});
		
		
		
		button2.setOnClickListener(new OnClickListener()
		{
		    
		    @Override
		    public void onClick(View arg0)
		    {
			// TODO Auto-generated method stub
			if (!Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			{
				Toast.makeText(HeartListen.this, "SD卡不存在，请插入SD卡！",
					Toast.LENGTH_SHORT).show();
				return;
			}
			try
			{
				// 创建保存录音的音频文件
				soundFile = new File(Environment
					.getExternalStorageDirectory().getCanonicalFile()
					+ "/心音.amr");
				mRecorder = new MediaRecorder();
				// 设置录音的声音来源,MIC即麦克风
				mRecorder.setAudioSource(MediaRecorder
					.AudioSource.MIC);
				// 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
				mRecorder.setOutputFormat(MediaRecorder
					.OutputFormat.THREE_GPP);
				// 设置声音编码的格式
				mRecorder.setAudioEncoder(MediaRecorder
					.AudioEncoder.AMR_NB);
				mRecorder.setOutputFile(soundFile.getAbsolutePath());
				mRecorder.prepare();
				// 开始录音
				mRecorder.start();  //①
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		    }
		});
		
		button3.setOnClickListener(new OnClickListener()
		{
		    
		    @Override
		    public void onClick(View v)
		    {
			// TODO Auto-generated method stub
			// 停止录音
			mRecorder.stop();  //②
			// 释放资源
			mRecorder.release();  //③
			mRecorder = null;
		    }
		});
		/*button2.setOnClickListener(new OnClickListener()
		{
		    
		    @Override
		    public void onClick(View arg0)
		    {
			// TODO Auto-generated method stub
			// 停止录音
			mRecorder.stop();  //②
			// 释放资源
			mRecorder.release();  //③
			mRecorder = null;
		    }
		});
		*/
		
    }
		public void onDestroy()
		{
			if (soundFile != null && soundFile.exists())
			{
				// 停止录音
				mRecorder.stop();
				// 释放资源
				mRecorder.release();
				mRecorder = null;
			}
			super.onDestroy();
			
	
    }
}