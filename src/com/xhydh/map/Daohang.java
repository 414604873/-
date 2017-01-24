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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.xhydh.fragment.WodeFragment;
import com.xhydh.utils.LogUtils;
import com.xhydh.utils.OverlayUtils;

/**
 * 此demo用来展示如何进行驾车、步行、公交路线搜索并在地图使用RouteOverlay、TransitOverlay绘制
 * 同时展示如何进行节点浏览并弹出泡泡
 */
public class Daohang extends Activity implements BaiduMap.OnMapClickListener,
		OnGetRoutePlanResultListener, TextWatcher,
		OnGetSuggestionResultListener,OnClickListener {
	// 浏览路线节点相关
	Button mBtnPre = null; // 上一个节点
	Button mBtnNext = null; // 下一个节点
	Button bt_plan = null;// 路线详情
	int nodeIndex = -1; // 节点索引,供浏览节点时使用
	// 路线数据结构的基类,表示一条路线，路线可能包括：路线规划中的换乘/驾车/步行路线
	@SuppressWarnings("rawtypes")
	RouteLine route = null;
	OverlayManager routeOverlay = null;
	boolean useDefaultIcon = false;
	private TextView popupText = null; // 泡泡view
	// 建议搜索地理位置,适配器
	SuggestionSearch suggestionSearch;
	private ArrayAdapter<String> sugAdapter = null;
	private String city;// 当前城市名
	// 起点，终点
	private AutoCompleteTextView start, end;
	// 定位相关
	private boolean isFirstLoc = true;
	private LocationClient mLocClient;
	private MyLocationListenner myListenner;
	// 定位图层显示方式
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	// 当前经纬度信息
	private LatLng latLng;
	// 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MapView mMapView = null; // 地图View
	BaiduMap mBaiduMap = null;
	// 路径规划搜索
	RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	// 存储路线详情信息,距离
	public static List<String> route_line = new ArrayList<String>();
	public static int distance;
	// 地图类型切换
	private Button daohang_wx;
	private Button daohang_zc;
	private Logger log = Logger.getLogger(getClass());
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daohang);
		CharSequence titleLable = "路线规划功能";
		setTitle(titleLable);
		// 初始化地图
		initMap();
		// 初始化控件
		initView();
		//配置log4j
		LogUtils.configLog();
	}

	private void initMap() {
		// TODO Auto-generated method stub
		// 初始化地图
		mMapView = (MapView) findViewById(R.id.map);
		mBaiduMap = mMapView.getMap();
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(80.0f);
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setMyLocationEnabled(true);
		// 设置当前位置显示
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
		// 定位当前位置
		mLocClient = new LocationClient(this);
		myListenner = new MyLocationListenner();
		mLocClient.registerLocationListener(myListenner);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		// 不设置 无法获取当前地址信息
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		// mLocClient.start();
	}

	private void initView() {
		// TODO Auto-generated method stub
		// 初始化组件
		start = (AutoCompleteTextView) findViewById(R.id.start);
		end = (AutoCompleteTextView) findViewById(R.id.end);
		daohang_wx = (Button) findViewById(R.id.daohang_wx);
		daohang_zc = (Button) findViewById(R.id.daohang_zc);
		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);
		bt_plan = (Button) findViewById(R.id.bt_plan);
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		bt_plan.setVisibility(View.INVISIBLE);
		daohang_wx.setOnClickListener(this);
		daohang_zc.setOnClickListener(this);
		// 注册地图点击事件
		mBaiduMap.setOnMapClickListener(this);
		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);
		// 注册EditText变化事件
		start.addTextChangedListener(this);
		end.addTextChangedListener(this);
		// 创建建议搜索实例，注册监听，添加适配器
		suggestionSearch = SuggestionSearch.newInstance();
		suggestionSearch.setOnGetSuggestionResultListener(this);
		sugAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
		end.setAdapter(sugAdapter);
		start.setAdapter(sugAdapter);
	}

	
	
	//发起路线规划搜索
	 
	public void searchRoute(View v) {
		// 重置浏览节点的路线数据
		try {
			route = null;
			mBtnPre.setVisibility(View.INVISIBLE);
			mBtnNext.setVisibility(View.INVISIBLE);
			mBaiduMap.clear();
			// 设置起终点信息，对于tranist search 来说，城市名无意义
			PlanNode stNode = null;
			PlanNode enNode = null;

			if (start.getText().toString().equals("我的位置")) {
				stNode = PlanNode.withLocation(latLng);
			} else {
				stNode = PlanNode.withCityNameAndPlaceName(city, start.getText()
						.toString().trim());
			}
			//根据城市名和地名确定结点信息
			enNode = PlanNode.withCityNameAndPlaceName(city, end.getText()
					.toString().trim());
			
			// 从起点到终点发起路径规划查询
			if (v.getId() == R.id.drive) {
				if( WodeFragment.flag == true ) {
					log.debug(WodeFragment.phoneString+"进行了从"+start.getText()
							.toString().trim()+"到"+end.getText()
							.toString().trim()+"的驾车搜索");
				}
				mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode)
						.to(enNode));
			} else if (v.getId() == R.id.transit) {
				if( WodeFragment.flag == true ) {
					log.debug(WodeFragment.phoneString+"进行了从"+start.getText()
							.toString().trim()+"到"+end.getText()
							.toString().trim()+"的公交搜索");
				}
				mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode)
						.city(city).to(enNode));
			} else if (v.getId() == R.id.walk) {
				if( WodeFragment.flag == true ) {
					log.debug(WodeFragment.phoneString+"进行了从"+start.getText()
							.toString().trim()+"到"+end.getText()
							.toString().trim()+"的步行搜索");
				}
				mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode)
						.to(enNode));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(Daohang.this, "请检查您的网络！", Toast.LENGTH_SHORT)
			.show();
		}
	}

	//点击节点浏览
	
	public void nodeClick(View v) {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		if (nodeIndex == -1 && v.getId() == R.id.pre) {
			return;
		}
		// 设置节点索引
		if (v.getId() == R.id.next) {
			if (nodeIndex < route.getAllStep().size() - 1) {
				nodeIndex++;
			} else {
				return;
			}
		} else if (v.getId() == R.id.pre) {
			if (nodeIndex > 0) {
				nodeIndex--;
			} else {
				return;
			}
		}
		// 获取节点结果经纬度信息
		LatLng nodeLocation = null;
		String nodeTitle = null;
		// 得到点击节点的信息，list存储
		Object step = route.getAllStep().get(nodeIndex);
		if (step instanceof DrivingRouteLine.DrivingStep) {
			// 上转型 得到当前节点入口路段信息的地理位置信息
			nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
		} else if (step instanceof WalkingRouteLine.WalkingStep) {
			nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
		} else if (step instanceof TransitRouteLine.TransitStep) {
			nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace()
					.getLocation();
			// 路段总体指示信息
			nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
		}
		if (nodeLocation == null || nodeTitle == null) {
			return;
		}
		// 移动节点至中心
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
		// show popup
		popupText = new TextView(Daohang.this);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFF000000);
		popupText.setText(nodeTitle);
		mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

	}

	

	// 得到导航路线详情
	public void routeinfo(View view) {
		route_line.removeAll(route_line);// 清空之前路线信息
		for (int i = 0; i < route.getAllStep().size(); i++) {
			String nodeTitle = null;
			Object step = route.getAllStep().get(i);
			distance = route.getDistance();
			if (step instanceof DrivingRouteLine.DrivingStep) {
				nodeTitle = ((DrivingRouteLine.DrivingStep) step)
						.getInstructions();// 节点行驶路线信息
			} else if (step instanceof WalkingRouteLine.WalkingStep) {
				nodeTitle = ((WalkingRouteLine.WalkingStep) step)
						.getInstructions();
			} else if (step instanceof TransitRouteLine.TransitStep) {
				nodeTitle = ((TransitRouteLine.TransitStep) step)
						.getInstructions();
			}
			route_line.add(nodeTitle);
		}
		Intent intent = new Intent(Daohang.this, RouteInfo.class);
		startActivity(intent);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	// 得到步行路线结果
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(Daohang.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			result.getSuggestAddrInfo();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			bt_plan.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			routeOverlay = overlay;
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(Daohang.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			result.getSuggestAddrInfo();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			bt_plan.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			routeOverlay = overlay;
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(Daohang.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			result.getSuggestAddrInfo();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			bt_plan.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
			routeOverlay = overlay;
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	// 定制RouteOverly
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

		public MyWalkingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	private class MyTransitRouteOverlay extends TransitRouteOverlay {

		public MyTransitRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}

	/**
	 * 定位SDK监听函数
	 */
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
				// 真正获取当前位置
				latLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				/*
				 * MapStatus.Builder builder = new MapStatus.Builder();
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
	public void onMapClick(LatLng point) {
		mBaiduMap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(final MapPoi poi) {
		new AlertDialog.Builder(Daohang.this)
		.setTitle("是否导航？")
		.setMessage("目标：" + poi.getName())
		.setPositiveButton("确定",
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Daohang.this,
								Poi_Daohang.class);
						intent.putExtra("latitude",
								poi.getPosition().latitude);
						intent.putExtra("longitude",
								poi.getPosition().longitude);
						intent.putExtra("address",
								poi.getName());
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
		mSearch.destroy();
		mMapView.onDestroy();
		suggestionSearch.destroy();
		super.onDestroy();
	}

	// 更新提示
	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		// TODO Auto-generated method stub
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();

		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null) {
				sugAdapter.add(info.key);

			}
		}
		sugAdapter.notifyDataSetChanged();

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
			Toast.makeText(Daohang.this, "请检查您的网络！", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.daohang_zc:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			mBaiduMap.setBaiduHeatMapEnabled(false);
			mBaiduMap.setTrafficEnabled(false);
			break;
		case R.id.daohang_wx:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			mBaiduMap.setBaiduHeatMapEnabled(false);
			mBaiduMap.setTrafficEnabled(false);
			break;
		}
	}
}
