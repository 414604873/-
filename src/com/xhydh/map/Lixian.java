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
	private TextView city_id;// ����id
	private TextView dd_state;// ��ͼ����״̬
	private EditText input_city;// ���������
	private int number;// ����
	private boolean flag = true;// �жϰ�ť��ʾֵ��״̬
	// �����ص����ߵ�ͼ��Ϣ�б�
	private ArrayList<MKOLUpdateElement> localMapList = null;
	// ���ص�ͼ������
	private LocalMapAdapter LMAdapter = null;
	// ���ų���
	private ListView hotCityList;
	private ArrayList<String> hotCities;
	private ArrayList<MKOLSearchRecord> records1;
	private ListAdapter hCAdapter;
	// ȫ������
	private ListView allCityList;
	private ArrayList<String> allCities;
	private ArrayList<MKOLSearchRecord> records2;
	private ListAdapter aCAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lixian);
		// ���ߵ�ͼ����
		mOffline = new MKOfflineMap();
		mOffline.init(this);
		initView();
	}

	// ��ʼ�����
	private void initView() {

		city_id = (TextView) findViewById(R.id.cityid);
		input_city = (EditText) findViewById(R.id.input_city);
		dd_state = (TextView) findViewById(R.id.state);

		hotCityList = (ListView) findViewById(R.id.hotcity_list);
		hotCities = new ArrayList<String>();
		// ��ȡ���ų����б�
		records1 = mOffline.getHotCityList();
		// �����ų����ó����ŵ�����������
		if (records1 != null) {
			for (MKOLSearchRecord r : records1) {
				hotCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
						+ this.formatDataSize(r.size));
			}
		}
		// ʹ��arrayAdapter��������
		hCAdapter = (ListAdapter) new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, hotCities);
		hotCityList.setAdapter(hCAdapter);
		hotCityList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				number = records1.get(position).cityID;

				new AlertDialog.Builder(Lixian.this)
						.setTitle("��ʾ")
						.setMessage(
								"�Ƿ�����" + records1.get(position).cityName
										+ "��ͼ?")
						.setNegativeButton("ȡ��", Lixian.this)
						.setPositiveButton("ȷ��", Lixian.this).create().show();
			}
		});

		allCityList = (ListView) findViewById(R.id.allcity_list);
		allCities = new ArrayList<String>();
		// ��ȡ����֧�����ߵ�ͼ�ĳ���
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
						.setTitle("��ʾ")
						.setMessage(
								"�Ƿ�����" + records2.get(position).cityName
										+ "��ͼ?")
						.setPositiveButton("ȷ��", Lixian.this)
						.setNegativeButton("ȡ��", Lixian.this).create().show();
			}
		});
		// ��ȡ���¹������ߵ�ͼ�ĸ�����Ϣ
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			// MKOLUpdateElement ���ߵ�ͼ������Ϣ��
			localMapList = new ArrayList<MKOLUpdateElement>();
		}

		ListView localMapListView = (ListView) findViewById(R.id.localmap_list);
		LMAdapter = new LocalMapAdapter();
		localMapListView.setAdapter(LMAdapter);

	}

	/**
	 * ����л��������б�
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
	 * ����л������ع����б�
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
	 * �������߳���
	 * 
	 * @param view
	 */
	public void search(View view) {
		// MKOLSearchRecord ���ߵ�ͼ�������м�¼�ṹ
		ArrayList<MKOLSearchRecord> records = mOffline.searchCity(input_city
				.getText().toString());
		if (records == null || records.size() != 1) {
			Toast.makeText(this, "δ�ҵ�������У����������룡", Toast.LENGTH_SHORT).show();
			return;
		}
		city_id.setText(String.valueOf(records.get(0).cityID));
		LinearLayout layout = (LinearLayout) findViewById(R.id.off_line);
		layout.setVisibility(View.VISIBLE);
	}

	/**
	 * ��ʼ����
	 * 
	 * @param view
	 */
	public void start(View view) {
		String cityName = input_city.getText().toString();
		int cityID = Integer.parseInt(city_id.getText().toString());
		mOffline.start(cityID);
		clickLocalMapListButton(null);
		Toast.makeText(this, "��ʼ�������ߵ�ͼ: " + cityName, Toast.LENGTH_SHORT)
				.show();
		updateView();
		LinearLayout layout = (LinearLayout) findViewById(R.id.off_line);
		layout.setVisibility(View.GONE);
	}

	/**
	 * ��ͣ���� δʹ��
	 * 
	 * @param view
	 */
	public void stop(View view) {
		int cityid = Integer.parseInt(city_id.getText().toString());
		Button button = (Button) findViewById(R.id.pause);
		if (flag) {
			flag = false;
			mOffline.pause(cityid);
			Toast.makeText(this, "��ͣ�������ߵ�ͼ. cityid: " + cityid,
					Toast.LENGTH_SHORT).show();
			button.setText("����");
		} else {
			flag = true;
			mOffline.start(cityid);
			Toast.makeText(this, "�����������ߵ�ͼ. cityid: " + cityid,
					Toast.LENGTH_SHORT).show();
			button.setText("��ͣ");
		}
		updateView();
	}

	/**
	 * ɾ�����ߵ�ͼ δʹ��
	 * 
	 * @param view
	 */
	public void remove(View view) {
		int cityid = Integer.parseInt(city_id.getText().toString());
		mOffline.remove(cityid);
		Toast.makeText(this, "ɾ�����ߵ�ͼ. cityid: " + cityid, Toast.LENGTH_SHORT)
				.show();
		updateView();
	}

	/**
	 * ����״̬��ʾ
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

	// �����ͼ����С��ʽ
	public String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			// �ַ�����ʽ��
			// %d �������ͣ�ʮ���ƣ�
			ret = String.format("%dK", size / 1024);
		} else {
			// ��һλС�����float
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

	@Override
	protected void onDestroy() {
		/**
		 * �˳�ʱ���������ߵ�ͼģ��
		 */
		mOffline.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		// ���ߵ�ͼ���ظ����¼�����
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// �������ؽ��ȸ�����ʾ
			if (update != null) {
				dd_state.setText(String.format("%s : %d%%", update.cityName,
						update.ratio));
				updateView();
			}
		}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// �������ߵ�ͼ��װ
			Log.d("����", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// �汾������ʾ
			// MKOLUpdateElement e = mOffline.getUpdateInfo(state);

			break;
		default:
			break;
		}

	}

	/**
	 * ���ߵ�ͼ�����б�������
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
				update.setText("�ɸ���");
			} else {
				update.setText("����");
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
							"ɾ�����ߵ�ͼ: " + e.cityName, Toast.LENGTH_SHORT).show();
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
						pause.setText("����");
						pause.setBackgroundColor(0xFF7597B3);
						Toast.makeText(getApplicationContext(),
								"��ͣ�������ߵ�ͼ: " + e.cityName, Toast.LENGTH_SHORT)
								.show();

					} else {
						flag = true;
						mOffline.start(e.cityID);
						pause.setBackgroundColor(0xFF0AB2FB);
						pause.setText("��ͣ");
						Toast.makeText(getApplicationContext(),
								"�����������ߵ�ͼ: " + e.cityName, Toast.LENGTH_SHORT)
								.show();

					}
					updateView();
				}
			});
		}

	}

	// Dialogȷ���Ƿ��������ߵ�ͼ
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