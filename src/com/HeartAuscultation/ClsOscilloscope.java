package com.HeartAuscultation;

import java.io.File;
import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;

public class ClsOscilloscope	//ʾ������
{

    private ArrayList<short[]> inBuf = new ArrayList<short[]>();

    private boolean isRecording = false;	//�߳̿��Ʊ��

    private Object lock = null;
    int mDivisions = 2;
    boolean mTop = false;
    float[] mPoints;
    
    File soundFile;

    /**
     * 
     * X����С�ı���
     */

    public int rateX = 4;

    /**
     * 
     * Y����С�ı���
     */

    public int rateY = 4;

    /**
     * 
     * Y�����
     */

    public int baseLine = 0;

    /**
     * 
     * ��ʼ��
     */

    public void initOscilloscope(int rateX, int rateY, int baseLine)
    {

	this.rateX = rateX;

	this.rateY = rateY;

	this.baseLine = baseLine;//��׼��

	lock = new Object();

    }

    /**
     * 
     * ��ʼ
     * 
     * 
     * @param recBufSize
     * 
     *            AudioRecord��MinBufferSize
     */

    public void Start(AudioRecord audioRecord, int recBufSize, SurfaceView sfv,

    Paint mPaint)
    {

	isRecording = true;

	new RecordThread(audioRecord, recBufSize).start();// ��ʼ¼���߳�

	new DrawThread(sfv, mPaint).start();// ��ʼ�����߳�

    }

    /**
     * 
     * ֹͣ
     */

    public void Stop()
    {

	isRecording = false;

	inBuf.clear();// ���

    }

    /**
     * 
     * �����MIC�������ݵ�inBuf
     * 
     * 
     */

    class RecordThread extends Thread//¼���߳�
    {

	private int recBufSize;

	private AudioRecord audioRecord;

	public RecordThread(AudioRecord audioRecord, int recBufSize)
	{

	    this.audioRecord = audioRecord;

	    this.recBufSize = recBufSize;

	}

	public void run()	//���߳�
	{

	    try {

		short[] buffer = new short[recBufSize];

		audioRecord.startRecording();// ��ʼ¼��

		while (isRecording) {

		    // ��MIC�������ݵ�������
		   
		    
			int bufferReadResult= audioRecord.read(buffer, 0,

		    recBufSize);
		    short[] tmpBuf = new short[bufferReadResult / rateX];

		    for (int i = 0, ii = 0; i < tmpBuf.length; i++, ii = i

		    * rateX) {

			tmpBuf[i] = buffer[ii];
			
			
		    }

		    synchronized (lock) {//

			inBuf.add(tmpBuf);// �������

		    }

		}

		audioRecord.stop();

	    }
	    catch (Throwable t) {

	    }

	}

    };

    /**
     * 
     * �������inBuf�е�����
     * 
     * 
     * @author GV
     * 
     * 
     */

    class DrawThread extends Thread	//�����߳�
    {

	private int oldX = 0;// �ϴλ��Ƶ�X����

	private int oldY = 0;// �ϴλ��Ƶ�Y����

	private SurfaceView sfv;// ����

	private int X_index = 0;// ��ǰ��ͼ������ĻX�������

	private Paint mPaint;// ����

	public DrawThread(SurfaceView sfv, Paint mPaint)
	{

	    this.sfv = sfv;

	    this.mPaint = mPaint;

	}

	@SuppressWarnings("unchecked")
	public void run()
	{

	    while (isRecording) {

		ArrayList<short[]> buf = new ArrayList<short[]>();

		synchronized (lock) {

		    if (inBuf.size() == 0)

		    continue;

		    buf = (ArrayList<short[]>) inBuf.clone();// ����

		    inBuf.clear();// ���

		}

		for (int i = 0; i < buf.size(); i++) {

		    short[] tmpBuf = buf.get(i);

		    SimpleDraw(X_index, tmpBuf, rateY, baseLine);// �ѻ��������ݻ�����

		    X_index += tmpBuf.length;

		    if (X_index > sfv.getWidth()) {

			X_index = 0;

		    }

		}

	    }

	}

	/**
	 * 
	 * ����ָ������
	 * 
	 * 
	 * @param start
	 * 
	 *            X�Ὺʼ��λ��(ȫ��)
	 * 
	 * @param buffer
	 * 
	 *            ������
	 * 
	 * @param rate
	 * 
	 *            Y��������С�ı���
	 * 
	 * @param baseLine
	 * 
	 *            Y�����
	 */

	void SimpleDraw(int start, short[] buffer, int rate, int baseLine)
	{
	    if (start == 0) oldX = 0;
	    Rect rect = new Rect();
	    if (mPoints == null || mPoints.length < buffer.length * 4) {
		mPoints = new float[buffer.length * 4];
	    }
	    Canvas canvas = sfv.getHolder().lockCanvas();// ��ȡ����
	    rect.set(start, 0, start + buffer.length, sfv.getHeight());
	    canvas.drawColor(Color.BLACK);		// �������
	    for (int i = 0; i < buffer.length / mDivisions; i++) {
		mPoints[i * 4] = i * 4 * mDivisions;
		mPoints[i * 4 + 2] = i * 4 * mDivisions;
		short rfk = buffer[mDivisions * i];
		short ifk = buffer[mDivisions * i + 1];
		float magnitude = (rfk * rfk + ifk * ifk);
		int dbValue = (int) (30 * Math.log10(magnitude));
		if (mTop) {
		    mPoints[i * 4 + 1] = 0;
		    mPoints[i * 4 + 3] = (dbValue * 1 - 10);
		    Log.v("log", "shiduoshao" + mPoints);
		}
		else {
		    mPoints[i * 4 + 1] = rect.height();
		    mPoints[i * 4 + 3] = rect.height() - (dbValue * 1 - 12);
		}
	    }

	    canvas.drawLines(mPoints, mPaint);
	    sfv.getHolder().unlockCanvasAndPost(canvas);	// �����������ύ���õ�ͼ��
	}

    }

}
