package com.xhydh.main;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.xhydh.fragment.MyFragmentPageAadpter;
import com.xhydh.fragment.WodeFragment;
import com.xhydh.fragment.XiaoxiFragment;
import com.xhydh.fragment.ZhuyeFragment;
import com.xhydh.map.R;
import com.xhydh.map.R.drawable;
import com.xhydh.map.R.id;
import com.xhydh.map.R.layout;
import com.xhydh.utils.ExampleUtil;

public class MainActivity extends FragmentActivity {

	// private final static Logger LOG = Logger.getLogger(MainActivity.class);

	// 定义3个Fragment的对象
	private ZhuyeFragment zhuyeFragment;
	private XiaoxiFragment xiaoxiFragment;
	private WodeFragment wodeFragment;
	// 定义一个ViewPager容器
	private ViewPager mPager;
	// 定义存储fragment的列表
	private ArrayList<Fragment> fragmentsList;
	// 定义适配器
	private MyFragmentPageAadpter mAdapter;
	// 定义底部导航栏的三个布局
	private RelativeLayout zhuye_layout;
	private RelativeLayout xiaoxi_layout;
	private RelativeLayout wode_layout;
	// 定义底部导航栏中的ImageView与TextView
	private ImageView zhuye_image;
	private ImageView xiaoxi_image;
	private ImageView wode_image;
	private TextView zhuye_text;
	private TextView xiaoxi_text;
	private TextView wode_text;
	// 定义要用的颜色值
	private int white = 0xFFFFFFFF;
	private int gray = 0xFF7597B3;
	private int blue = 0xFF0AB2FB;
	// 定义FragmentManager对象
	FragmentManager fManager;
	// 定义一个Onclick全局对象
	public MyOnClick myClick;
	// 定义一个OnPageChangeListener全局对象
	public MyPageChangeListener myPageChange;
	public static boolean isForeground = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();
		fManager = getSupportFragmentManager();
		initViewPager();
		initViews();
		initState();
		
	}

	// 完成组件的初始化
	public void initViews() {
		myClick = new MyOnClick();
		myPageChange = new MyPageChangeListener();
		mPager = (ViewPager) findViewById(R.id.viewPager);
		zhuye_image = (ImageView) findViewById(R.id.zhuye_image);
		xiaoxi_image = (ImageView) findViewById(R.id.xiaoxi_image);
		wode_image = (ImageView) findViewById(R.id.wode_image);
		zhuye_text = (TextView) findViewById(R.id.zhuye_text);
		xiaoxi_text = (TextView) findViewById(R.id.xiaoxi_text);
		wode_text = (TextView) findViewById(R.id.wode_text);
		zhuye_layout = (RelativeLayout) findViewById(R.id.zhuye_layout);
		xiaoxi_layout = (RelativeLayout) findViewById(R.id.xiaoxi_layout);
		wode_layout = (RelativeLayout) findViewById(R.id.wode_layout);
		mPager.setAdapter(mAdapter);//fragment+viewpager的adapter
		mPager.setOnPageChangeListener(myPageChange);//页面滑动监听器
		zhuye_layout.setOnClickListener(myClick);
		xiaoxi_layout.setOnClickListener(myClick);
		wode_layout.setOnClickListener(myClick);
	}

	// 初始化viewpager
	private void initViewPager() {
		fragmentsList = new ArrayList<Fragment>();
		zhuyeFragment = new ZhuyeFragment();
		xiaoxiFragment = new XiaoxiFragment();
		wodeFragment = new WodeFragment();
		fragmentsList.add(zhuyeFragment);
		fragmentsList.add(xiaoxiFragment);
		fragmentsList.add(wodeFragment);
		mAdapter = new MyFragmentPageAadpter(fManager, fragmentsList);
	}

	// 定义一个设置初始状态的方法
	private void initState() {
		zhuye_image.setImageResource(R.drawable.icon_1_d);
		zhuye_text.setTextColor(blue);
		mPager.setCurrentItem(0);
	}

	// 定义fragment点击事件
	public class MyOnClick implements OnClickListener {
		@Override
		public void onClick(View view) {
			clearChioce();
			setChioceItem(view.getId());
		}
	}

	// 定义页面change事件
	public class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// if (arg0 == 2) {
			int i = mPager.getCurrentItem();
			clearChioce();
			setChioceItem(i);
			// }
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int index) {
		}

	}

	// 定义一个选中一个item/滑动page后的处理
	public void setChioceItem(int index) {
		switch (index) {
		case R.id.zhuye_layout:
		case 0:
			zhuye_image.setImageResource(R.drawable.icon_1_d);
			zhuye_text.setTextColor(blue);
			mPager.setCurrentItem(0);
			// LOG.error("您点击了主页");
			break;
		case R.id.xiaoxi_layout:
		case 1:
			xiaoxi_image.setImageResource(R.drawable.icon_2_d);
			xiaoxi_text.setTextColor(blue);
			mPager.setCurrentItem(1);
			// LOG.error("您点击了消息");
			break;
		case R.id.wode_layout:
		case 2:
			wode_image.setImageResource(R.drawable.icon_3_d);
			wode_text.setTextColor(blue);
			mPager.setCurrentItem(3);
			// LOG.error("您点击了我的");
			break;
		}
	}

	// 定义一个重置所有选项的方法
	public void clearChioce() {
		zhuye_image.setImageResource(R.drawable.icon_1_n);
		zhuye_layout.setBackgroundColor(white);
		zhuye_text.setTextColor(gray);
		xiaoxi_image.setImageResource(R.drawable.icon_2_n);
		xiaoxi_layout.setBackgroundColor(white);
		xiaoxi_text.setTextColor(gray);
		wode_image.setImageResource(R.drawable.icon_3_n);
		wode_layout.setBackgroundColor(white);
		wode_text.setTextColor(gray);
	}

	
}
