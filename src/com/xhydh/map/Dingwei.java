package com.xhydh.map;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.xhydh.fragment.WodeFragment;
import com.xhydh.utils.LogUtils;
import com.xhydh.utils.OverlayUtils;
import com.xhydh.utils.SchoolInfo;

/**
 * ��������չʾ��ν�϶�λSDKʵ�ֶ�λ����ʹ��MyLocation Overlay���ƶ�λλ�� ͬʱչʾ���ʹ���Զ���ͼ����Ʋ����ʱ��������
 */
public class Dingwei extends Activity implements OnClickListener,
		OnGetPoiSearchResultListener, OnGetSuggestionResultListener,
		TextWatcher,OnMapClickListener {

	// ��λ���
	LocationClient mLocClient;
	// ��λSDK��������
	public MyLocationListenner myListener;
	// ��λͼ����ʾ��ʽ
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	// ��ͼ��ʾ���
	MapView mMapView;
	BaiduMap mBaiduMap;
	// ��ť����¼�
	OnCheckedChangeListener radioButtonListener;
	// ��λͼ�갴ť
	Button requestLocButton;
	// �л���ͼ����
	Button zhengchang;
	Button weixing;
	Button jiaotong;
	Button reli;
	// poi����
	Button poi_search;
	// �Ƿ��״ζ�λ
	boolean isFirstLoc = true;
	// ����
	private PopupWindow popupWindow;
	// �洢�л���ͼ���������ֵ�id
	List<String> mapName = new ArrayList<String>();
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch suggestionSearch;
	// �����ؼ������봰��
	private AutoCompleteTextView keyWords = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index = 0;
	private String city;// ��ǰ������
	public static String new_city;// poi���ڳ���
	private LatLng latLng;// ��ǰ��γ��
	private TextView popupText = null; // ����view
	private Logger log = Logger.getLogger(getClass());
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dingwei);
		/*
		 * myOrientationListener = new myOrientationListener(
		 * getApplicationContext());
		 */
		// mLocClient.start();
		initMap();
		InitView();
		// ��ʼ��overlay
		initOverlay();
		LogUtils.configLog();
	}

	// ��ʼ���ؼ�
	private void InitView() {
		// TODO Auto-generated method stub
		// �ؼ���ʼ��
		requestLocButton = (Button) findViewById(R.id.button1);
		zhengchang = (Button) findViewById(R.id.button2);
		weixing = (Button) findViewById(R.id.button3);
		jiaotong = (Button) findViewById(R.id.button4);
		reli = (Button) findViewById(R.id.button5);
		poi_search = (Button) findViewById(R.id.button6);
		zhengchang.getBackground().setAlpha(100);
		weixing.getBackground().setAlpha(100);
		jiaotong.getBackground().setAlpha(100);
		reli.getBackground().setAlpha(100);
		// ��λͼ����ʾ��ʽ��Ĭ��Ϊnormal
		mCurrentMode = LocationMode.NORMAL;
		requestLocButton.setText("��ͨ");
		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
				case NORMAL:
					requestLocButton.setText("����");
					// ����̬�����ֶ�λͼ���ڵ�ͼ����
					mCurrentMode = LocationMode.FOLLOWING;
					// ���ö�λͼ��������Ϣ��ֻ��������λͼ������ö�λͼ��������Ϣ�Ż���
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case COMPASS:
					requestLocButton.setText("��ͨ");
					// ��̬ͨ�� ���¶�λ����ʱ���Ե�ͼ���κβ���
					mCurrentMode = LocationMode.NORMAL;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case FOLLOWING:
					requestLocButton.setText("����");
					// ����̬����ʾ��λ����Ȧ�����ֶ�λͼ���ڵ�ͼ����
					mCurrentMode = LocationMode.COMPASS;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				default:
					break;
				}
			}
		};
		requestLocButton.setOnClickListener(btnClickListener);
		zhengchang.setOnClickListener(this);
		weixing.setOnClickListener(this);
		jiaotong.setOnClickListener(this);
		reli.setOnClickListener(this);
		// POIʵ������ע�����
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		// ��ʼ������ģ�飬ע�������¼�����
		keyWords = (AutoCompleteTextView) findViewById(R.id.poi_search);
		keyWords.addTextChangedListener(this);
		suggestionSearch = SuggestionSearch.newInstance();
		suggestionSearch.setOnGetSuggestionResultListener(this);
		sugAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
		keyWords.setAdapter(sugAdapter);
	}

	// ��ʼ����ͼ
	private void initMap() {
		// TODO Auto-generated method stub
		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.lmapView);
		// ������λͼ��
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapClickListener(this);
		// ���õ�ͼ�Ŵ����
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(30.0f);
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setMyLocationEnabled(true);
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		myListener = new MyLocationListenner();
		mLocClient.registerLocationListener(myListener);
		//��λ�����������
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
	}

	// ��ʼ���Զ���ͼ��
	private void initOverlay() {
		// TODO Auto-generated method stub
		final OverlayUtils utils = new OverlayUtils();
		utils.initOverlay(mBaiduMap);
		mBaiduMap
				.setOnMarkerClickListener(new com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker arg0) {
						// TODO Auto-generated method stub
						List<SchoolInfo> list = utils.getList();
						List<Marker> lMarkers = utils.getListMarkers();
						for (int i = 0; i < list.size(); i++) {
							if(arg0 == lMarkers.get(i)){
							final SchoolInfo schoolInfo = list.get(i);
							popText(schoolInfo).setOnClickListener(
									new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											alert(schoolInfo);
										}

									});
							}
						}
						return true;
					}
				});

	}

	// ��λSDK��������

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.e("Dingwei", "---->>>>>>>>>>>." + location.getLocType());
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null) {
				return;
			}
			// �õ�����
			city = location.getCity();
			// accuracy ��λ����
			// direction GPS��λʱ����Ƕ�
			// latitude �ٶ�γ������
			// longitude�ٶȾ�������
			// satellitesNum GPS��λʱ������Ŀ
			// speedGPS��λʱ�ٶ�
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				latLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				/*
				 * MapStatus.Builder builder = new MapStatus.Builder();
				 * builder.target(ll).zoom(18.0f);
				 * mBaiduMap.animateMapStatus(MapStatusUpdateFactory
				 * .newMapStatus(builder.build()));
				 */
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(u, 100);

			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onStart() {
		// ����ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocClient.isStarted()) {
			mLocClient.start();
		}
		// �������򴫸���
		// myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// �ر�ͼ�㶨λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocClient.stop();

		// �رշ��򴫸���
		// myOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// �˳�ʱ���ٶ�λ
		mLocClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		mPoiSearch.destroy();
		suggestionSearch.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	// �л���ͼ��ʾ
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button2:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			mBaiduMap.setBaiduHeatMapEnabled(false);
			mBaiduMap.setTrafficEnabled(false);
			break;
		case R.id.button3:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			mBaiduMap.setBaiduHeatMapEnabled(false);
			mBaiduMap.setTrafficEnabled(false);
			break;
		case R.id.button4:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			mBaiduMap.setBaiduHeatMapEnabled(false);
			mBaiduMap.setTrafficEnabled(true);
			break;
		case R.id.button5:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			mBaiduMap.setTrafficEnabled(false);
			mBaiduMap.setBaiduHeatMapEnabled(true);
			break;
		}
	}

	// ������ť����¼�

	public void poiSearch(View v) {
		if (keyWords.getText().toString().isEmpty()) {
			Toast.makeText(Dingwei.this, "�������ݲ���Ϊ�գ�", Toast.LENGTH_LONG).show();
		} else {
			mPoiSearch.searchNearby((new PoiNearbySearchOption())
					.location(latLng).keyword(keyWords.getText().toString())
					.radius(10 * 1000).pageNum(load_Index));
			if( WodeFragment.flag == true) {
				log.debug(WodeFragment.phoneString+"������"+keyWords.getText().toString());
			}
		}
	}

	// ��poi�����õ��Ľ����ע
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(Dingwei.this, "δ�ҵ����", Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			// ����PoiOverlay
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			// ����overlay���Դ����ע����¼�
			mBaiduMap.setOnMarkerClickListener(overlay);
			// ����PoiOverlay����
			overlay.setData(result);
			// ���PoiOverlay����ͼ��
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// ������ؼ����ڱ���û���ҵ����������������ҵ�ʱ�����ذ����ùؼ�����Ϣ�ĳ����б�
			String strInfo = "��";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "�ҵ����";
			Toast.makeText(Dingwei.this, strInfo, Toast.LENGTH_LONG).show();
		}
	}

	// �ٵ���õ�poi���������ϸ��Ϣ
	public void onGetPoiDetailResult(final PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(Dingwei.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(Dingwei.this,
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
			// �ƶ��ڵ�������
			// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
			// show popup
			popText(result).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					alert(result);
				}
			});
			if (WodeFragment.flag == true) {
				log.debug(WodeFragment.phoneString+"�����"+result.getName());
			}
		}
	}

	// ������ʾpoi_reult����
	public TextView popText(final PoiDetailResult result) {
		popupText = new TextView(Dingwei.this);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFF000000);
		popupText.setText(result.getName() + ": " + result.getAddress());
		mBaiduMap.showInfoWindow(new InfoWindow(popupText,
				result.getLocation(), 0));
		return popupText;
	}

	// ������ʾSchoolInfo����
	public TextView popText(final SchoolInfo schoolInfo) {
		popupText = new TextView(Dingwei.this);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFF000000);
		popupText.setText(schoolInfo.getPoint_name() + ": "
				+ schoolInfo.getAddress());
		mBaiduMap.showInfoWindow(new InfoWindow(popupText, schoolInfo
				.getPoint(), 0));
		return popupText;
	}

	// ������poi_reult����
	public void alert(final PoiDetailResult result) {
		new AlertDialog.Builder(Dingwei.this)
				.setTitle("�Ƿ񵼺���")
				.setMessage("Ŀ���ַ��" + result.getAddress())
				.setPositiveButton("ȷ��",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(Dingwei.this,
										Poi_Daohang.class);
								intent.putExtra("latitude",
										result.getLocation().latitude);
								intent.putExtra("longitude",
										result.getLocation().longitude);
								intent.putExtra("address", result.getName());
								startActivity(intent);
							}
						})
				.setNegativeButton("ȡ��",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).create().show();
	}

	// ������SchoolInfo����
	public void alert(final SchoolInfo schoolInfo) {
		new AlertDialog.Builder(Dingwei.this)
				.setTitle("�Ƿ񵼺���")
				.setMessage("Ŀ���ַ��" + schoolInfo.getAddress())
				.setPositiveButton("ȷ��",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(Dingwei.this,
										Poi_Daohang.class);
								intent.putExtra("latitude",
										schoolInfo.getPoint().latitude);
								intent.putExtra("longitude",
										schoolInfo.getPoint().longitude);
								intent.putExtra("address",
										schoolInfo.getAddress());
								startActivity(intent);
							}
						})
				.setNegativeButton("ȡ��",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).create().show();
	}

	// �õ�������ʾ
	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

	// �Զ���PoiOverlay
	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);

			// �õ���Ȥ�����Ϣ
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			// �õ���Ȥ�����
			new_city = poi.city;
			return true;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	// �༭�����ָı����
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

		try {
			if (s.length() <= 0) {
				return;
			}
			String cityname = city;
			/**
			 * ʹ�ý������������ȡ�����б������onSuggestionResult()�и���
			 */
			suggestionSearch.requestSuggestion((new SuggestionSearchOption())
					.keyword(s.toString()).city(cityname));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(Dingwei.this, "�����������磡", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapClick(final LatLng arg0) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(Dingwei.this)
		.setTitle("�Ƿ񵼺���")
		.setMessage("Ŀ�꣺δ֪")
		.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Dingwei.this,
								Poi_Daohang.class);
						intent.putExtra("latitude",
								arg0.latitude);
						intent.putExtra("longitude",
								arg0.longitude);
						intent.putExtra("address",
								"δ֪");
						startActivity(intent);
					}
				})
		.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

					}
				}).create().show();
	}

	@Override
	public boolean onMapPoiClick(final MapPoi arg0) {
		// TODO Auto-generated method stub
		
		new AlertDialog.Builder(Dingwei.this)
		.setTitle("�Ƿ񵼺���")
		.setMessage("Ŀ�꣺" + arg0.getName())
		.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Dingwei.this,
								Poi_Daohang.class);
						intent.putExtra("latitude",
								arg0.getPosition().latitude);
						intent.putExtra("longitude",
								arg0.getPosition().longitude);
						intent.putExtra("address",
								arg0.getName());
						startActivity(intent);
					}
				})
		.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

					}
				}).create().show();
		return false;
	}

}
