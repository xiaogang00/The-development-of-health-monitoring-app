package com;

import android.app.TabActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import com.AskHelp.SystemHelp;
import com.BloodPressure.BloodPressureActivity;
import com.BloodSugar.BloodSugarActivity;
import com.FileManager.FileManagerActivity;
import com.HeartAuscultation.HeartListen;
import com.geniuseoe.demo.R;

/**
 * ������������ϵͳ�ĸ�������ģ�������TabHostѡ��У�ʵ���ڸ�ѡ�֮���ֶ��л�����ʾ��ͬģ�������
 * 
 * @author ������
 * @Time 2013-09-01
 * */
@SuppressWarnings("deprecation")
public class MainTabHost extends TabActivity
{
    private TabHost tabHost;
    private RadioGroup radioGroup;

    // Tabѡ���ͼ������
    private int[] tabIconViewArray =
    { R.drawable.tab_icon1, R.drawable.tab_icon2, R.drawable.tab_icon3,
	    R.drawable.tab_icon4, R.drawable.tab_icon5 };

    // Tabѡ�����������
    private String[] tabNameTextArray =
    { "����", "Ѫѹ", "Ѫ��", "����", "����" };

    // Tabѡ��е�����(��)����
    private Class[] tabContentClassArray =
    { FileManagerActivity.class, BloodPressureActivity.class,
	    BloodSugarActivity.class, HeartListen.class, SystemHelp.class };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main_tab_host);

	// Ϊÿһ��ѡ����ð�ť��ͼ�ꡢ���ֺ����ݣ���Tab��ť��ӽ�Tabѡ���
	tabHost = getTabHost();
	for (int i = 0; i < tabContentClassArray.length; i++) {
	    TabSpec tabSpec = tabHost.newTabSpec(tabNameTextArray[i])
		    .setIndicator(tabNameTextArray[i])
		    .setContent(getTabItemIntent(i));
	    tabHost.addTab(tabSpec);
	}
	initData();
    }

    // ��ѡ��ϵ�ÿ����ť���м�������ʵ��ģ����л�
    private void initData()
    {
	radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
	radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
	{
	    @Override
	    public void onCheckedChanged(RadioGroup group, int checkedId)
	    {
		switch (checkedId)
		{
		case R.id.RadioButton0:
		    tabHost.setCurrentTabByTag(tabNameTextArray[0]);
		    break;
		case R.id.RadioButton1:
		    tabHost.setCurrentTabByTag(tabNameTextArray[1]);
		    break;
		case R.id.RadioButton2:
		    tabHost.setCurrentTabByTag(tabNameTextArray[2]);
		    break;
		case R.id.RadioButton3:
		    tabHost.setCurrentTabByTag(tabNameTextArray[3]);
		    break;
		case R.id.RadioButton4:
		    tabHost.setCurrentTabByTag(tabNameTextArray[4]);
		    break;
		
		}
	    }
	});
	((RadioButton) radioGroup.getChildAt(0)).toggle();
    }

    // ��Tabѡ�����Activity����,���ҽ�LoginActivity�����swapUsername����ת��ÿ��IntentЯ���ַ���ÿ��Tabѡ��
    private Intent getTabItemIntent(int index)
    {
	Bundle swapData = new Bundle();
	swapData.putString("swapUsername", this.getBundleData());
	Intent intent = new Intent(this, tabContentClassArray[index]);
	intent.putExtras(swapData);
	return intent;
    }

    // ��ȡ������MainTabHostAcitivity��Intent,����IntentЯ���� username����
    public String getBundleData()
    {
	Intent intent = getIntent();
	Bundle data = intent.getExtras();
	String swapData = data.getString("username").toString();
	return swapData;
    }
}
