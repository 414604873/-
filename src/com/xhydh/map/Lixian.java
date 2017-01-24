package com.xhydh.map;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

public class Lixian extends Activity implements MKOfflineMapListener,
		android.content.DialogInterface.OnClickListener {

	private MKOfflineMap mOffline = null;
	private TextView city_id;// 城市id
	private TextView dd_state;// 地图下载状态
	private EditText input_city;// 输入城市名
	private int number;// 城市
	private boolean flag = true;// 判断按钮显示值的状态
	// 已下载的离线地图信息列表
	private ArrayList<MKOLUpdateElement> localMapList = null;
	// 本地地图适配器
	private LocalMapAdapter LMAdapter = null;
	// 热门城市
	private ListView hotCityList;
	private ArrayList<String> hotCities;
	private ArrayList<MKOLSearchRecord> records1;
	private ListAdapter hCAdapter;
	// 全部城市
	private ListView allCityList;
	private ArrayList<String> allCities;
	private ArrayList<MKOLSearchRecord> records2;
	private ListAdapter aCAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lixian);
		// 离线地图服务
		mOffline = new MKOfflineMap();
		mOffline.init(this);
		initView();
	}

	// 初始化组件
	private void initView() {

		city_id = (TextView) findViewById(R.id.cityid);
		input_city = (EditText) findViewById(R.id.input_city);
		dd_state = (TextView) findViewById(R.id.state);

		hotCityList = (ListView) findViewById(R.id.hotcity_list);
		hotCities = new ArrayList<String>();
		// 获取热门城市列表
		records1 = mOffline.getHotCityList();
		// 将热门城市拿出来放到本地数据中
		if (records1 != null) {
			for (MKOLSearchRecord r : records1) {
				hotCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
						+ this.formatDataSize(r.size));
			}
		}
		// 使用arrayAdapter来绑定数据
		hCAdapter = (ListAdapter) new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, hotCities);
		hotCityList.setAdapter(hCAdapter);
		hotCityList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				number = records1.get(position).cityID;

				new AlertDialog.Builder(Lixian.this)
						.setTitle("提示")
						.setMessage(
								"是否下载" + records1.get(position).cityName
										+ "地图?")
						.setNegativeButton("取消", Lixian.this)
						.setPositiveButton("确认", Lixian.this).create().show();
			}
		});

		allCityList = (ListView) findViewById(R.id.allcity_list);
		allCities = new ArrayList<String>();
		// 获取所有支持离线地图的城市
		records2 = mOffline.getOfflineCityList();
		if (records2 != null) {
			for (MKOLSearchRecord r : records2) {
				allCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
						+ this.formatDataSize(r.size));
			}
		}
		aCAdapter = (ListAdapter) new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, allCities);
		allCityList.setAdapter(aCAdapter);
		allCityList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				number = records2.get(position).cityID;

				new AlertDialog.Builder(Lixian.this)
						.setTitle("提示")
						.setMessage(
								"是否下载" + records2.get(position).cityName
										+ "地图?")
						.setPositiveButton("确认", Lixian.this)
						.setNegativeButton("取消", Lixian.this).create().show();
			}
		});
		// 获取已下过的离线地图的更新信息
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			// MKOLUpdateElement 离线地图更新信息类
			localMapList = new ArrayList<MKOLUpdateElement>();
		}

		ListView localMapListView = (ListView) findViewById(R.id.localmap_list);
		LMAdapter = new LocalMapAdapter();
		localMapListView.setAdapter(LMAdapter);

	}

	/**
	 * 点击切换至城市列表
	 * 
	 * @param view
	 */
	public void clickCityListButton(View view) {
		LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
		LinearLayout lm = (LinearLayout) findViewById(R.id.localmap_layout);
		lm.setVisibility(View.GONE);
		cl.setVisibility(View.VISIBLE);

	}

	/**
	 * 点击切换至下载管理列表
	 * 
	 * @param view
	 */
	public void clickLocalMapListButton(View view) {
		LinearLayout cl = (LinearLayout) findViewById(R.id.citylist_layout);
		LinearLayout lm = (LinearLayout) findViewById(R.id.localmap_layout);
		lm.setVisibility(View.VISIBLE);
		cl.setVisibility(View.GONE);
	}

	/**
	 * 搜索离线城市
	 * 
	 * @param view
	 */
	public void search(View view) {
		// MKOLSearchRecord 离线地图搜索城市记录结构
		ArrayList<MKOLSearchRecord> records = mOffline.searchCity(input_city
				.getText().toString());
		if (records == null || records.size() != 1) {
			Toast.makeText(this, "未找到输入城市，请重新输入！", Toast.LENGTH_SHORT).show();
			return;
		}
		city_id.setText(String.valueOf(records.get(0).cityID));
		LinearLayout layout = (LinearLayout) findViewById(R.id.off_line);
		layout.setVisibility(View.VISIBLE);
	}

	/**
	 * 开始下载
	 * 
	 * @param view
	 */
	public void start(View view) {
		String cityName = input_city.getText().toString();
		int cityID = Integer.parseInt(city_id.getText().toString());
		mOffline.start(cityID);
		clickLocalMapListButton(null);
		Toast.makeText(this, "开始下载离线地图: " + cityName, Toast.LENGTH_SHORT)
				.show();
		updateView();
		LinearLayout layout = (LinearLayout) findViewById(R.id.off_line);
		layout.setVisibility(View.GONE);
	}

	/**
	 * 暂停下载 未使用
	 * 
	 * @param view
	 */
	public void stop(View view) {
		int cityid = Integer.parseInt(city_id.getText().toString());
		Button button = (Button) findViewById(R.id.pause);
		if (flag) {
			flag = false;
			mOffline.pause(cityid);
			Toast.makeText(this, "暂停下载离线地图. cityid: " + cityid,
					Toast.LENGTH_SHORT).show();
			button.setText("继续");
		} else {
			flag = true;
			mOffline.start(cityid);
			Toast.makeText(this, "继续下载离线地图. cityid: " + cityid,
					Toast.LENGTH_SHORT).show();
			button.setText("暂停");
		}
		updateView();
	}

	/**
	 * 删除离线地图 未使用
	 * 
	 * @param view
	 */
	public void remove(View view) {
		int cityid = Integer.parseInt(city_id.getText().toString());
		mOffline.remove(cityid);
		Toast.makeText(this, "删除离线地图. cityid: " + cityid, Toast.LENGTH_SHORT)
				.show();
		updateView();
	}

	/**
	 * 更新状态显示
	 */
	public void updateView() {
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}
		LMAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onPause() {
		int cityid = Integer.parseInt(city_id.getText().toString());
		MKOLUpdateElement temp = mOffline.getUpdateInfo(cityid);
		if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
			mOffline.pause(cityid);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// 定义地图包大小格式
	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			// 字符串格式化
			// %d 整数类型（十进制）
			ret = String.format("%dK", size / 1024);
		} else {
			// 带一位小数点的float
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

	@Override
	protected void onDestroy() {
		/**
		 * 退出时，销毁离线地图模块
		 */
		mOffline.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		// 离线地图下载更新事件类型
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// 处理下载进度更新提示
			if (update != null) {
				dd_state.setText(String.format("%s : %d%%", update.cityName,
						update.ratio));
				updateView();
			}
		}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// 有新离线地图安装
			Log.d("离线", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// 版本更新提示
			// MKOLUpdateElement e = mOffline.getUpdateInfo(state);

			break;
		default:
			break;
		}

	}

	/**
	 * 离线地图管理列表适配器
	 */
	public class LocalMapAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localMapList.size();
		}

		@Override
		public Object getItem(int index) {
			return localMapList.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View view, ViewGroup arg2) {
			MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
			view = View.inflate(Lixian.this, R.layout.offline_localmap_list,
					null);
			initViewItem(view, e);
			return view;
		}

		void initViewItem(View view, final MKOLUpdateElement e) {
			Button display = (Button) view.findViewById(R.id.display);
			Button remove = (Button) view.findViewById(R.id.remove);
			final Button pause = (Button) view.findViewById(R.id.pause);
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView update = (TextView) view.findViewById(R.id.update);
			TextView ratio = (TextView) view.findViewById(R.id.ratio);
			ratio.setText(e.ratio + "%");
			title.setText(e.cityName);
			if (e.update) {
				update.setText("可更新");
			} else {
				update.setText("最新");
			}
			if (e.ratio != 100) {
				display.setEnabled(false);
			} else {
				display.setEnabled(true);
			}
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mOffline.remove(e.cityID);
					Toast.makeText(getApplicationContext(),
							"删除离线地图: " + e.cityName, Toast.LENGTH_SHORT).show();
					updateView();
				}
			});
			display.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("x", e.geoPt.longitude);
					intent.putExtra("y", e.geoPt.latitude);
					intent.setClass(Lixian.this, LixianMap.class);
					startActivity(intent);
				}
			});
			pause.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (flag) {
						flag = false;
						mOffline.pause(e.cityID);
						pause.setText("继续");
						pause.setBackgroundColor(0xFF7597B3);
						Toast.makeText(getApplicationContext(),
								"暂停下载离线地图: " + e.cityName, Toast.LENGTH_SHORT)
								.show();

					} else {
						flag = true;
						mOffline.start(e.cityID);
						pause.setBackgroundColor(0xFF0AB2FB);
						pause.setText("暂停");
						Toast.makeText(getApplicationContext(),
								"继续下载离线地图: " + e.cityName, Toast.LENGTH_SHORT)
								.show();

					}
					updateView();
				}
			});
		}

	}

	// Dialog确认是否下载离线地图
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		switch (arg1) {
		case DialogInterface.BUTTON_POSITIVE:
			mOffline.start(number);
			clickLocalMapListButton(null);
			updateView();
			break;

		case DialogInterface.BUTTON_NEGATIVE:

			break;
		}
	}
}