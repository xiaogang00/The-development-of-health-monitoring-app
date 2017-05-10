package com.FileManager;

import java.io.File;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.Register.UserDAO;
import com.geniuseoe.demo.R;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;

/**
 * ҽ�Ƶ�������ģ�飬���յ�½���û���ȡ������Ϣ��������Ϣ��������ص��Ӳ�����Ƭ�����ձ���
 * 
 * @author ������
 * @Time 2013-11-25
 * 
 */
@SuppressWarnings("deprecation")
public class FileManagerActivity extends TabActivity {

	// ������Ϣ
	private TabHost tabHost;
	private EditText file_username, file_password, file_name, file_age,
			file_phone;
	private RadioGroup file_sex;
	private RadioButton file_male, file_female;

	// ���Ӳ���
	private HashMap<String, SoftReference<Bitmap>> mImageCache;

	private Gallery gallery;
	private ImageSwitcher imageSwitcher;
	private List<String> fileList;
	private Button addImageButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ����ѡ���������ӻ�����Ϣ�͵��Ӳ�����ǩҳ
		tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.file_manager_layout,
				tabHost.getTabContentView(), true);
		//
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("������Ϣ")
				.setContent(R.id.tab01));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("������Ϣ")
				.setContent(R.id.tab02));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("���Ӳ���")
				.setContent(R.id.tab03));

		// ���������Ϣ
		excuteBaseInfo();

		// ��������Ϣ
		excuteHealthInfo();

		// ������Ӳ�����Ϣ
		excuteElecMedRecord();
	}

	// ���������Ϣ
	private void excuteBaseInfo() {
		// ��ʼ������
		file_username = (EditText) findViewById(R.id.file_username);
		file_password = (EditText) findViewById(R.id.file_password);
		file_name = (EditText) findViewById(R.id.file_name);
		file_sex = (RadioGroup) findViewById(R.id.file_sex);
		file_male = (RadioButton) findViewById(R.id.file_male);
		file_female = (RadioButton) findViewById(R.id.file_female);
		file_age = (EditText) findViewById(R.id.file_age);
		file_phone = (EditText) findViewById(R.id.file_phone);

		// ���û�����ȡһ����¼,���ַ���������ʽ��ȫ��������userBaseInfo��
		UserDAO uService = new UserDAO(FileManagerActivity.this);
		String[] userBaseInfo = uService.readDisplay(getLoginBundleData());

		// ��ʾ��������Ϣ
		file_username.setText(userBaseInfo[0]);
		file_password.setText(userBaseInfo[1]);
		file_name.setText(userBaseInfo[2]);
		if (userBaseInfo[3].equals("��")) {
			file_male.setChecked(true);
		} else {
			file_female.setChecked(true);
		}
		file_age.setText(userBaseInfo[4]);
		file_phone.setText(userBaseInfo[5]);
	}

	// ����������
	private void excuteHealthInfo() {
		Button file_savedButton = (Button) findViewById(R.id.file_savedButton);
		file_savedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(FileManagerActivity.this, "����ɹ�",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	// ������Ӳ���
	private void excuteElecMedRecord() {
		// ����һ������������Ŀ����ӵ��ˮƽ�����б��Gallery��ͼ��Ϊ��ָ��ͼƬ�����������������ѡ����ĸı�

		fileList = getInSDPhoto();
		gallery = (Gallery) findViewById(R.id.gallery);
		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
		addImageButton = (Button) findViewById(R.id.addImageButton);
		addImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(FileManagerActivity.this, "����ͷ����",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(FileManagerActivity.this,TakePhotoes.class);
				startActivity(intent);
			}
		});

		// ΪImageSwitcher��������ViewFactory����
		imageSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(FileManagerActivity.this);
				imageView.setBackgroundColor(0xff0000);
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				return imageView;
			}
		});

		// ����imageSwitcher�����ͼƬ��������Ч��
		imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));

		// Ϊgallery�����ṩѡ��ͼƬ��
		gallery.setAdapter(new BaseAdapter() {

			@Override
			public int getCount() {
				return fileList.size();
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			// ʹ��BitmapFactory���ߴ�ָ��·���½���ȡ��һ��ͼƬ����bitmap������imageView����ʾ��bitmap
			@Override
			public View getView(int position, View arg1, ViewGroup arg2) {

				ImageView imageView = new ImageView(FileManagerActivity.this);

				// ͨ��4�ֲ�ͬ�ķ���:ֱ�ӷ������淨�����ŷ��������ڴ淨������ͼƬ���OOM����
				// Bitmap bitmap = loadBitmapImage(fileList.get(position));
				// Bitmap bitmap = loadBitmapWithCache(fileList.get(position));
				Bitmap bitmap = adjustImageSize(fileList.get(position));

				imageView.setImageBitmap(bitmap);
				Log.w("BitmapFactory.decodeFile������ͼƬ", fileList.get(position));
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setLayoutParams(new Gallery.LayoutParams(90, 100));

				// Gallery�Զ�����ʾ��ʽ

				/*
				 * TypedArray typedArray =
				 * obtainStyledAttributes(R.styleable.Gallery);
				 * imageView.setBackgroundResource(typedArray.getResourceId(
				 * R.styleable.Gallery_android_galleryItemBackground, 0));
				 * typedArray.recycle();
				 */
				Log.e("�ڴ�������", String.valueOf(bitmap.isRecycled()));
				return imageView;
			}

		});

		// ��gallery�����е�ѡ����ı�ʱ��imageSwitcher��������ʾ��ӦͼƬ��Դ
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				Log.w("����imageSwitcher�е�ǰͼƬ", fileList.get(position));
				imageSwitcher.setImageURI(Uri.parse(fileList.get(position)));
				// releaseImage(fileList.get(position-3));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});
	}

	// ���Login��½�û���
	private String getLoginBundleData() {
		Intent intent = getIntent();
		Bundle Data = intent.getExtras();
		String login_username = Data.getString("swapUsername");
		// Toast.makeText(FileManager.this,login_username,Toast.LENGTH_SHORT).show();
		return login_username;
	}

	// ��ȡָ��·��Ŀ¼���ļ��б�,����Ҫ���ͼƬ�ļ�,����fileList���϶�����(List<String>)
	private List<String> getInSDPhoto() {

		List<String> fileList = new ArrayList<String>();
		// ָ��Ҫ���ʵ�·��path
		String path = File.separator + "sdcard" + File.separator + "DCIM"
				+ File.separator + "Camera" + File.separator;
		File dir = new File(path);
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			Log.i("����" + path + "�µ��ļ���Ϣ" + i, files[i].toString());
			if (getAllImage(files[i].getPath()))
				fileList.add(files[i].getPath());
		}
		return fileList;
	}

	// ���˳�����Ҫ��ͼƬ�ļ�
	private boolean getAllImage(String fileName) {
		boolean flag;
		String expandedName = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length()).toLowerCase();
		if (expandedName.equals("jpg") || expandedName.equals("gif")
				|| expandedName.equals("png") || expandedName.equals("jpeg")
				|| expandedName.equals("bmp")) {
			flag = true;
			Log.w("ͼƬ�ļ�,����Ҫ��", fileName);
		} else {
			flag = false;
			Log.w("���˷�ͼƬ�ļ�", fileName);
		}
		return flag;
	}

	/*
	 * ����1��������򵥵�ͼƬ���ط�ʽ�������κ�ͼƬ���桢������С���߻����ڴ淽ʽ
	 * 
	 * Ч����Σ���ģ������ͼƬֻ�ܼ���1-3�ţ�֮�������OOM������Defy�ϲ�����ִ���ԭ���������ڴ����Ʋ�ͬ��
	 * Defy�����е��ǵ�����ROM�ڴ������40MB������galleryÿ����ʾһ��ͼƬʱ����Ҫ���½������һ��ͼƬ��
	 * ������Defy�ϻ�δ��������ͼƬ���Ӵ�GC���ղ���ʱʱ�������п��ܳ���OOM��
	 */
	public Bitmap loadBitmapImage(String pathName) {
		Log.w("BitmapFactory.decodeFile������ͼƬ", pathName);
		return BitmapFactory.decodeFile(pathName);
	}

	/*
	 * ����2��ΪͼƬ���ص����һ�������û��棬ÿ��ͼƬ�ӻ����л�ȡͼƬ�����������в����ڣ��Ż��sdcard����ͼƬ��
	 * �����ö�����뻺�档ͬʱ�����õĶ���Ҳ������GC���ڴ治���ʱ��������ǡ�
	 * 
	 * Ч����� �� ��ģ�����ϣ��ܱ��޻���ʱ�����1-2��ͼƬ�������ǻ����OOM����Defy�ϲ���������ͼƬ����ԱȽ�ռ�ڴ�ʱ��
	 * ��GC��δ���ü����������ö���ʱ������Ҫ���볬��ʣ�������ڴ�ռ䣬�����Ȼû����ȫ����OOM��
	 * ������ɼ��ش�����СͼƬ������100*100���ģ������������õ����ÿ��ܾͷ��ӳ����ˡ�
	 */
	public Bitmap loadBitmapWithCache(String pathName) {

		if (mImageCache.containsKey(pathName)) {
			SoftReference<Bitmap> softReference = mImageCache.get(pathName);
			Bitmap bitmap = softReference.get();
			if (null != bitmap)
				return bitmap;
		}

		Bitmap bitmap = BitmapFactory.decodeFile(pathName);
		mImageCache.put(pathName, new SoftReference<Bitmap>(bitmap));
		Log.w("BitmapFactory.decodeFile���������ͼƬ", pathName);
		return bitmap;
	}

	/*
	 * ����3�� �����ڴ棬�ھ������ٵ���СͼƬ��ǰ����չʾͼƬ����ʱ�ֶ�ȥ����ͼƬ��
	 * Ч����Σ�ͼƬѹ��������Ȼά����1*1024*1024����adapter�У���ʱ����releaseImage������������ʱ����Ҫ��ͼƬ
	 * ����ʱģ������Ҳ��δ���ֹ�OOM�������ܵ��������ۺϻ��桢������С�����յȸ����ֶΣ������ܹ���Ч����OOM�ġ�
	 */
	public void releaseImage(String pathName) {
		if (mImageCache.containsKey(pathName)) {
			SoftReference<Bitmap> reference = mImageCache.get(pathName);
			Bitmap bitmap = reference.get();
			if (null != bitmap) {
				bitmap.recycle();
				Log.w("�ڴ����", "recyling" + pathName);
			}
			mImageCache.remove(pathName);
		}
	}

	/*
	 * ����4�� ��ͼƬ����ѹ���Լ����ڴ�ռ�ã���������µ���ͼƬ��С������Ӱ��Ӧ�õı�������
	 * 
	 * Ч����Σ����Ƚ���ͼƬ�ı߽磬�ڲ���Ҫ�õ�Bitmap�����ǰ���¾��ܻ��ͼ���ߣ����ֵ�ֱ����õ�options.
	 * outWidth��options.outHeight���������У���
	 * computeSampleSize��������Ĳ����ֱ�Ϊ������ͼƬ�����BitmapFactory
	 * .Options������������ͼƬ��С�Ŀ���ֵ������������ͼƬ���ڴ�ռ�������ޡ��� ���ԭʼͼƬ�Ŀ�ߣ��˷������Լ���õ�һ����������
	 * �����ô˱�������ԭʼͼƬ�����ص��ڴ��У���ʱͼƬ�����ĵ��ڴ治�ᳬ������ָ���Ĵ�С�� ��ģ�����У�����ͼƬ��ռ�ڴ��СΪ1*1024
	 * *1024ʱ����δѹ����ʱ�ܼ��ظ���ͼƬ������Ȼ�����OOM��������ͼƬ��ռ�ڴ��СΪ0 .5*1024*1024��������������������ͼƬ��
	 * ���Ե���ͼƬ��С�����ܹ���Ч��ʡ�ڴ�ġ���Defy�в������ԭ��ͬ�ϡ�
	 */
	public Bitmap adjustImageSize(String pathName) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inSampleSize = 5;
		float imagew = 150;
		float imageh = 150;
		int yRatio = (int) Math.ceil(options.outHeight / imageh);
		int xRatio = (int) Math.ceil(options.outWidth / imagew);

		if (yRatio > 1 || xRatio > 1) {
			if (yRatio > xRatio) {
				options.inSampleSize = yRatio;
			} else {
				options.inSampleSize = xRatio;
			}
		}
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
		Log.w("BitmapFactory.decodeFile���Ž�����ͼƬ", pathName);
		return bitmap;
	}
}
