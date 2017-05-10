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
 * 医疗档案管理模块，按照登陆的用户读取基本信息、健康信息、浏览本地电子病例照片，拍照保存
 * 
 * @author 樊俊彬
 * @Time 2013-11-25
 * 
 */
@SuppressWarnings("deprecation")
public class FileManagerActivity extends TabActivity {

	// 基本信息
	private TabHost tabHost;
	private EditText file_username, file_password, file_name, file_age,
			file_phone;
	private RadioGroup file_sex;
	private RadioButton file_male, file_female;

	// 电子病历
	private HashMap<String, SoftReference<Bitmap>> mImageCache;

	private Gallery gallery;
	private ImageSwitcher imageSwitcher;
	private List<String> fileList;
	private Button addImageButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 创建选项卡容器并添加基本信息和电子病例标签页
		tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.file_manager_layout,
				tabHost.getTabContentView(), true);
		//
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("基本信息")
				.setContent(R.id.tab01));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("健康信息")
				.setContent(R.id.tab02));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("电子病历")
				.setContent(R.id.tab03));

		// 处理基本信息
		excuteBaseInfo();

		// 处理健康信息
		excuteHealthInfo();

		// 处理电子病历信息
		excuteElecMedRecord();
	}

	// 处理基本信息
	private void excuteBaseInfo() {
		// 初始化界面
		file_username = (EditText) findViewById(R.id.file_username);
		file_password = (EditText) findViewById(R.id.file_password);
		file_name = (EditText) findViewById(R.id.file_name);
		file_sex = (RadioGroup) findViewById(R.id.file_sex);
		file_male = (RadioButton) findViewById(R.id.file_male);
		file_female = (RadioButton) findViewById(R.id.file_female);
		file_age = (EditText) findViewById(R.id.file_age);
		file_phone = (EditText) findViewById(R.id.file_phone);

		// 按用户名读取一条记录,以字符串数组形式并全部保存在userBaseInfo中
		UserDAO uService = new UserDAO(FileManagerActivity.this);
		String[] userBaseInfo = uService.readDisplay(getLoginBundleData());

		// 显示出基本信息
		file_username.setText(userBaseInfo[0]);
		file_password.setText(userBaseInfo[1]);
		file_name.setText(userBaseInfo[2]);
		if (userBaseInfo[3].equals("男")) {
			file_male.setChecked(true);
		} else {
			file_female.setChecked(true);
		}
		file_age.setText(userBaseInfo[4]);
		file_phone.setText(userBaseInfo[5]);
	}

	// 处理健康数据
	private void excuteHealthInfo() {
		Button file_savedButton = (Button) findViewById(R.id.file_savedButton);
		file_savedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(FileManagerActivity.this, "保存成功",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	// 处理电子病历
	private void excuteElecMedRecord() {
		// 创建一个锁定中心条目并且拥有水平滚动列表的Gallery视图，为它指定图片适配器、监听器监控选择项的改变

		fileList = getInSDPhoto();
		gallery = (Gallery) findViewById(R.id.gallery);
		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
		addImageButton = (Button) findViewById(R.id.addImageButton);
		addImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(FileManagerActivity.this, "摄像头启动",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(FileManagerActivity.this,TakePhotoes.class);
				startActivity(intent);
			}
		});

		// 为ImageSwitcher对象设置ViewFactory对象
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

		// 设置imageSwitcher对象的图片更换动画效果
		imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));

		// 为gallery对象提供选择图片项
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

			// 使用BitmapFactory工具从指定路径下解析取得一张图片创建bitmap对象，在imageView中显示该bitmap
			@Override
			public View getView(int position, View arg1, ViewGroup arg2) {

				ImageView imageView = new ImageView(FileManagerActivity.this);

				// 通过4种不同的方法:直接法，缓存法，缩放法，回收内存法，解析图片解决OOM问题
				// Bitmap bitmap = loadBitmapImage(fileList.get(position));
				// Bitmap bitmap = loadBitmapWithCache(fileList.get(position));
				Bitmap bitmap = adjustImageSize(fileList.get(position));

				imageView.setImageBitmap(bitmap);
				Log.w("BitmapFactory.decodeFile解析到图片", fileList.get(position));
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				imageView.setLayoutParams(new Gallery.LayoutParams(90, 100));

				// Gallery自定义显示样式

				/*
				 * TypedArray typedArray =
				 * obtainStyledAttributes(R.styleable.Gallery);
				 * imageView.setBackgroundResource(typedArray.getResourceId(
				 * R.styleable.Gallery_android_galleryItemBackground, 0));
				 * typedArray.recycle();
				 */
				Log.e("内存回收与否", String.valueOf(bitmap.isRecycled()));
				return imageView;
			}

		});

		// 当gallery对象中的选项发生改变时在imageSwitcher对象中显示对应图片资源
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				Log.w("设置imageSwitcher中当前图片", fileList.get(position));
				imageSwitcher.setImageURI(Uri.parse(fileList.get(position)));
				// releaseImage(fileList.get(position-3));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});
	}

	// 获得Login登陆用户名
	private String getLoginBundleData() {
		Intent intent = getIntent();
		Bundle Data = intent.getExtras();
		String login_username = Data.getString("swapUsername");
		// Toast.makeText(FileManager.this,login_username,Toast.LENGTH_SHORT).show();
		return login_username;
	}

	// 获取指定路径目录中文件列表,过滤要求的图片文件,存入fileList集合对象中(List<String>)
	private List<String> getInSDPhoto() {

		List<String> fileList = new ArrayList<String>();
		// 指定要访问的路径path
		String path = File.separator + "sdcard" + File.separator + "DCIM"
				+ File.separator + "Camera" + File.separator;
		File dir = new File(path);
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			Log.i("测试" + path + "下的文件信息" + i, files[i].toString());
			if (getAllImage(files[i].getPath()))
				fileList.add(files[i].getPath());
		}
		return fileList;
	}

	// 过滤出符合要求图片文件
	private boolean getAllImage(String fileName) {
		boolean flag;
		String expandedName = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length()).toLowerCase();
		if (expandedName.equals("jpg") || expandedName.equals("gif")
				|| expandedName.equals("png") || expandedName.equals("jpeg")
				|| expandedName.equals("bmp")) {
			flag = true;
			Log.w("图片文件,符合要求", fileName);
		} else {
			flag = false;
			Log.w("过滤非图片文件", fileName);
		}
		return flag;
	}

	/*
	 * 方法1：采用最简单的图片加载方式，不带任何图片缓存、调整大小或者回收内存方式
	 * 
	 * 效果如何：在模拟器上图片只能加载1-3张，之后便会出现OOM错误；在Defy上不会出现错误；原因是两者内存限制不同，
	 * Defy上运行的是第三方ROM内存分配有40MB。另外gallery每次显示一张图片时，都要重新解析获得一张图片，
	 * 尽管在Defy上还未曾出错但当图片量加大，GC回收不及时时，还是有可能出现OOM。
	 */
	public Bitmap loadBitmapImage(String pathName) {
		Log.w("BitmapFactory.decodeFile解析到图片", pathName);
		return BitmapFactory.decodeFile(pathName);
	}

	/*
	 * 方法2：为图片加载的添加一个软引用缓存，每次图片从缓存中获取图片对象，若缓存中不存在，才会从sdcard加载图片，
	 * 并将该对象加入缓存。同时软引用的对象也有助于GC在内存不足的时候回收它们。
	 * 
	 * 效果如何 ： 在模拟器上，能比无缓存时多加载1-2张图片，但还是会出现OOM；在Defy上不曾出错。当图片都相对比较占内存时，
	 * 在GC还未来得及回收软引用对象时，就又要申请超出剩余量的内存空间，因此仍然没能完全避免OOM。
	 * 如果换成加载大量的小图片，比如100*100规格的，缓存中软引用的作用可能就发挥出来了。
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
		Log.w("BitmapFactory.decodeFile缓存解析到图片", pathName);
		return bitmap;
	}

	/*
	 * 方法3： 回收内存，在尽可能少地缩小图片的前提下展示图片，此时手动去回收图片。
	 * 效果如何：图片压缩限制仍然维持在1*1024*1024，在adapter中，及时调用releaseImage方法，回收暂时不需要的图片
	 * 。此时模拟器中也从未出现过OOM，所以总的来讲，综合缓存、调整大小、回收等各种手段，还是能够有效避免OOM的。
	 */
	public void releaseImage(String pathName) {
		if (mImageCache.containsKey(pathName)) {
			SoftReference<Bitmap> reference = mImageCache.get(pathName);
			Bitmap bitmap = reference.get();
			if (null != bitmap) {
				bitmap.recycle();
				Log.w("内存回收", "recyling" + pathName);
			}
			mImageCache.remove(pathName);
		}
	}

	/*
	 * 方法4： 对图片进行压缩以减少内存占用，多数情况下调整图片大小并不会影响应用的表现力。
	 * 
	 * 效果如何：首先解码图片的边界，在不需要得到Bitmap对象的前提下就能获得图像宽高（宽高值分别被设置到options.
	 * outWidth和options.outHeight两个属性中）。
	 * computeSampleSize这个方法的参数分别为“解析图片所需的BitmapFactory
	 * .Options”、“调整后图片最小的宽或高值”、“调整后图片的内存占用量上限”。 结合原始图片的宽高，此方法可以计算得到一个调整比例
	 * ，再用此比例调整原始图片并加载到内存中，此时图片所消耗的内存不会超出事先指定的大小。 在模拟器中，限制图片所占内存大小为1*1024
	 * *1024时，比未压缩过时能加载更多图片，但仍然会出现OOM；若限制图片所占内存大小为0 .5*1024*1024，则能完整的载入所有图片。
	 * 所以调整图片大小还是能够有效节省内存的。在Defy中不会出错，原因同上。
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
		Log.w("BitmapFactory.decodeFile缩放解析到图片", pathName);
		return bitmap;
	}
}
