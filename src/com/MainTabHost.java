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
 * 功能描述：将系统的各个功能模块添加在TabHost选项卡中，实现在各选项卡之间手动切换以显示不同模块的内容
 * 
 * @author 樊俊彬
 * @Time 2013-09-01
 * */
@SuppressWarnings("deprecation")
public class MainTabHost extends TabActivity
{
    private TabHost tabHost;
    private RadioGroup radioGroup;

    // Tab选项卡的图标数组
    private int[] tabIconViewArray =
    { R.drawable.tab_icon1, R.drawable.tab_icon2, R.drawable.tab_icon3,
	    R.drawable.tab_icon4, R.drawable.tab_icon5 };

    // Tab选项卡的文字数组
    private String[] tabNameTextArray =
    { "档案", "血压", "血糖", "听诊", "帮助" };

    // Tab选项卡中的内容(类)数组
    private Class[] tabContentClassArray =
    { FileManagerActivity.class, BloodPressureActivity.class,
	    BloodSugarActivity.class, HeartListen.class, SystemHelp.class };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main_tab_host);

	// 为每一个选项卡设置按钮、图标、文字和内容，将Tab按钮添加进Tab选项卡中
	tabHost = getTabHost();
	for (int i = 0; i < tabContentClassArray.length; i++) {
	    TabSpec tabSpec = tabHost.newTabSpec(tabNameTextArray[i])
		    .setIndicator(tabNameTextArray[i])
		    .setContent(getTabItemIntent(i));
	    tabHost.addTab(tabSpec);
	}
	initData();
    }

    // 对选项卡上的每个按钮进行监听，以实现模块的切换
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

    // 给Tab选项卡设置Activity内容,并且将LoginActivity传入的swapUsername数据转让每个Intent携带分发给每个Tab选项
    private Intent getTabItemIntent(int index)
    {
	Bundle swapData = new Bundle();
	swapData.putString("swapUsername", this.getBundleData());
	Intent intent = new Intent(this, tabContentClassArray[index]);
	intent.putExtras(swapData);
	return intent;
    }

    // 获取启动该MainTabHostAcitivity的Intent,传入Intent携带的 username数据
    public String getBundleData()
    {
	Intent intent = getIntent();
	Bundle data = intent.getExtras();
	String swapData = data.getString("username").toString();
	return swapData;
    }
}
