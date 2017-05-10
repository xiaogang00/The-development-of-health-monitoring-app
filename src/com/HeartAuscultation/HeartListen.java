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
    static final int frequency = 8000;// �ֱ���
    static final int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    static final int xMax = 6;// X����С�������ֵ,X���������޴����ײ���ˢ����ʱ
    static final int xMin = 1;// X����С������Сֵ
    static final int yMax = 1 / 2;// Y����С�������ֵ
    static final int yMin = 8;// Y����С������Сֵ

    int recBufSize;// ¼����Сbuffer��С
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
	// ��ӵ�һ����ǩҳ
	tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("��������")
		.setContent(R.id.tab01));
	// ��ӵڶ�����ǩҳ
	tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("��Ͻ���")
		.setContent(R.id.tab02));
	// ��ӵ�������ǩҳ
	tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("����˵��")
		.setContent(R.id.tab03));
	
	
	Button button1=(Button)findViewById(R.id.button1);
	Button button2=(Button)findViewById(R.id.button2);
	Button button3=(Button)findViewById(R.id.button3);
	
	/************************************/
	// ¼�����
		recBufSize = AudioRecord.getMinBufferSize(frequency,
		channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
		channelConfiguration, audioEncoding, recBufSize);
		// ����ͻ���s
		sfv = (SurfaceView) this.findViewById(R.id.surfaceViewc);
		//sfv.setOnTouchListener(new TouchEvent());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLUE);// ����Ϊ��ɫ
		mPaint.setStrokeWidth(7);// ���û��ʴ�ϸ
		
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
				Toast.makeText(HeartListen.this, "SD�������ڣ������SD����",
					Toast.LENGTH_SHORT).show();
				return;
			}
			try
			{
				// ��������¼������Ƶ�ļ�
				soundFile = new File(Environment
					.getExternalStorageDirectory().getCanonicalFile()
					+ "/����.amr");
				mRecorder = new MediaRecorder();
				// ����¼����������Դ,MIC����˷�
				mRecorder.setAudioSource(MediaRecorder
					.AudioSource.MIC);
				// ����¼�Ƶ������������ʽ���������������������ʽ֮ǰ���ã�
				mRecorder.setOutputFormat(MediaRecorder
					.OutputFormat.THREE_GPP);
				// ������������ĸ�ʽ
				mRecorder.setAudioEncoder(MediaRecorder
					.AudioEncoder.AMR_NB);
				mRecorder.setOutputFile(soundFile.getAbsolutePath());
				mRecorder.prepare();
				// ��ʼ¼��
				mRecorder.start();  //��
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
			// ֹͣ¼��
			mRecorder.stop();  //��
			// �ͷ���Դ
			mRecorder.release();  //��
			mRecorder = null;
		    }
		});
		/*button2.setOnClickListener(new OnClickListener()
		{
		    
		    @Override
		    public void onClick(View arg0)
		    {
			// TODO Auto-generated method stub
			// ֹͣ¼��
			mRecorder.stop();  //��
			// �ͷ���Դ
			mRecorder.release();  //��
			mRecorder = null;
		    }
		});
		*/
		
    }
		public void onDestroy()
		{
			if (soundFile != null && soundFile.exists())
			{
				// ֹͣ¼��
				mRecorder.stop();
				// �ͷ���Դ
				mRecorder.release();
				mRecorder = null;
			}
			super.onDestroy();
			
	
    }
}