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
 * 此类用来展示如何结合定位SDK实现定位，并使用MyLocation Overlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 */
public class Dingwei extends Activity implements OnClickListener,
		OnGetPoiSearchResultListener, OnGetSuggestionResultListener,
		TextWatcher,OnMapClickListener {

	// 定位相关
	LocationClient mLocClient;
	// 定位SDK监听函数
	public MyLocationListenner myListener;
	// 定位图层显示方式
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	// 地图显示相关
	MapView mMapView;
	BaiduMap mBaiduMap;
	// 按钮点击事件
	OnCheckedChangeListener radioButtonListener;
	// 定位图标按钮
	Button requestLocButton;
	// 切换地图类型
	Button zhengchang;
	Button weixing;
	Button jiaotong;
	Button reli;
	// poi搜索
	Button poi_search;
	// 是否首次定位
	boolean isFirstLoc = true;
	// 弹窗
	private PopupWindow popupWindow;
	// 存储切换地图的类型名字的id
	List<String> mapName = new ArrayList<String>();
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch suggestionSearch;
	// 搜索关键字输入窗口
	private AutoCompleteTextView keyWords = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index = 0;
	private String city;// 当前城市名
	public static String new_city;// poi所在城市
	private LatLng latLng;// 当前经纬度
	private TextView popupText = null; // 泡泡view
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
		// 初始化overlay
		initOverlay();
		LogUtils.configLog();
	}

	// 初始化控件
	private void InitView() {
		// TODO Auto-generated method stub
		// 控件初始化
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
		// 定位图层显示方式，默认为normal
		mCurrentMode = LocationMode.NORMAL;
		requestLocButton.setText("普通");
		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
				case NORMAL:
					requestLocButton.setText("跟随");
					// 跟随态，保持定位图标在地图中心
					mCurrentMode = LocationMode.FOLLOWING;
					// 设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case COMPASS:
					requestLocButton.setText("普通");
					// 普通态： 更新定位数据时不对地图做任何操作
					mCurrentMode = LocationMode.NORMAL;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case FOLLOWING:
					requestLocButton.setText("罗盘");
					// 罗盘态，显示定位方向圈，保持定位图标在地图中心
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
		// POI实例化，注册监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		// 初始化搜索模块，注册搜索事件监听
		keyWords = (AutoCompleteTextView) findViewById(R.id.poi_search);
		keyWords.addTextChangedListener(this);
		suggestionSearch = SuggestionSearch.newInstance();
		suggestionSearch.setOnGetSuggestionResultListener(this);
		sugAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
		keyWords.setAdapter(sugAdapter);
	}

	// 初始化地图
	private void initMap() {
		// TODO Auto-generated method stub
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.lmapView);
		// 开启定位图层
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapClickListener(this);
		// 设置地图放大比例
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(30.0f);
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		myListener = new MyLocationListenner();
		mLocClient.registerLocationListener(myListener);
		//定位相关属性设置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
	}

	// 初始化自定义图绘
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

	// 定位SDK监听函数

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.e("Dingwei", "---->>>>>>>>>>>." + location.getLocType());
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			// 得到城市
			city = location.getCity();
			// accuracy 定位精度
			// direction GPS定位时方向角度
			// latitude 百度纬度坐标
			// longitude百度经度坐标
			// satellitesNum GPS定位时卫星数目
			// speedGPS定位时速度
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
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
		// 开启图层定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocClient.isStarted()) {
			mLocClient.start();
		}
		// 开启方向传感器
		// myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// 关闭图层定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocClient.stop();

		// 关闭方向传感器
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
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
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

	// 切换地图显示
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

	// 搜索按钮点击事件

	public void poiSearch(View v) {
		if (keyWords.getText().toString().isEmpty()) {
			Toast.makeText(Dingwei.this, "搜索内容不能为空！", Toast.LENGTH_LONG).show();
		} else {
			mPoiSearch.searchNearby((new PoiNearbySearchOption())
					.location(latLng).keyword(keyWords.getText().toString())
					.radius(10 * 1000).pageNum(load_Index));
			if( WodeFragment.flag == true) {
				log.debug(WodeFragment.phoneString+"搜索了"+keyWords.getText().toString());
			}
		}
	}

	// 将poi搜索得到的结果标注
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(Dingwei.this, "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			// 创建PoiOverlay
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			// 设置overlay可以处理标注点击事件
			mBaiduMap.setOnMarkerClickListener(overlay);
			// 设置PoiOverlay数据
			overlay.setData(result);
			// 添加PoiOverlay到地图中
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(Dingwei.this, strInfo, Toast.LENGTH_LONG).show();
		}
	}

	// 再点击得到poi搜索结果详细信息
	public void onGetPoiDetailResult(final PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(Dingwei.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(Dingwei.this,
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
			// 移动节点至中心
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
				log.debug(WodeFragment.phoneString+"点击了"+result.getName());
			}
		}
	}

	// 气泡显示poi_reult参数
	public TextView popText(final PoiDetailResult result) {
		popupText = new TextView(Dingwei.this);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFF000000);
		popupText.setText(result.getName() + ": " + result.getAddress());
		mBaiduMap.showInfoWindow(new InfoWindow(popupText,
				result.getLocation(), 0));
		return popupText;
	}

	// 气泡显示SchoolInfo参数
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

	// 弹出框传poi_reult参数
	public void alert(final PoiDetailResult result) {
		new AlertDialog.Builder(Dingwei.this)
				.setTitle("是否导航？")
				.setMessage("目标地址：" + result.getAddress())
				.setPositiveButton("确定",
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
				.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).create().show();
	}

	// 弹出框传SchoolInfo参数
	public void alert(final SchoolInfo schoolInfo) {
		new AlertDialog.Builder(Dingwei.this)
				.setTitle("是否导航？")
				.setMessage("目标地址：" + schoolInfo.getAddress())
				.setPositiveButton("确定",
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
				.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).create().show();
	}

	// 得到联想提示
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

	// 自定义PoiOverlay
	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);

			// 得到兴趣点的信息
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			// 得到兴趣点城市
			new_city = poi.city;
			return true;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	// 编辑框文字改变操作
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

		try {
			if (s.length() <= 0) {
				return;
			}
			String cityname = city;
			/**
			 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
			 */
			suggestionSearch.requestSuggestion((new SuggestionSearchOption())
					.keyword(s.toString()).city(cityname));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(Dingwei.this, "请检查您的网络！", Toast.LENGTH_SHORT).show();
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
		.setTitle("是否导航？")
		.setMessage("目标：未知")
		.setPositiveButton("确定",
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
								"未知");
						startActivity(intent);
					}
				})
		.setNegativeButton("取消",
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
		.setTitle("是否导航？")
		.setMessage("目标：" + arg0.getName())
		.setPositiveButton("确定",
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
		.setNegativeButton("取消",
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
