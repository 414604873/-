package com.xhydh.utils;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.xhydh.map.R;

public class OverlayUtils {
	private SchoolInfo schoolInfo,schoolInfo1,schoolInfo2,schoolInfo3,schoolInfo4,schoolInfo5,schoolInfo6,schoolInfo7,schoolInfo8,schoolInfo9,schoolInfo10,schoolInfo11,schoolInfo12,schoolInfo13,schoolInfo14,schoolInfo15,schoolInfo16,schoolInfo17,schoolInfo18,schoolInfo19,schoolInfo20,schoolInfo21;
	private LatLng latLng,latLng1,latLng2,latLng3,latLng4,latLng5,latLng6,latLng7,latLng8,latLng9,latLng10,latLng11,latLng12,latLng13,latLng14,latLng15,latLng16,latLng17,latLng18,latLng19,latLng20,latLng21;
	private List<SchoolInfo> list = new ArrayList<SchoolInfo>();
	private List<Marker> listMarkers = new ArrayList<Marker>();
	public OverlayUtils() {
		//一食堂
		latLng = new LatLng(30.782691, 103.958455);//103.958455,30.782691
		schoolInfo = new SchoolInfo("一食堂", "一食堂",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng);
		latLng1 = new LatLng(30.78121, 103.961644);//103.961644,30.78121
		schoolInfo1 = new SchoolInfo("二食堂", "二食堂",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng1);
		latLng2 = new LatLng(30.781264, 103.954808);//103.954808,30.781264
		schoolInfo2 = new SchoolInfo("三食堂", "三食堂",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng2);
		latLng3 = new LatLng(30.788291, 103.962452);//103.962452,30.788291
		schoolInfo3 = new SchoolInfo("四食堂", "四食堂",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng3);
		latLng4 = new LatLng(30.780357, 103.956389);//103.956389,30.780357
		schoolInfo4 = new SchoolInfo("锦地苑操场", "锦地苑操场",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng4);
		latLng5 = new LatLng(30.780419, 103.95497);//103.95497,30.780419
		schoolInfo5 = new SchoolInfo("锦地苑", "锦地苑",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng5);
		latLng6 = new LatLng(30.780101, 103.962848);//103.962848,30.780101
		schoolInfo6 = new SchoolInfo("临江苑", "临江苑",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng6);
		latLng7 = new LatLng(30.781939, 103.96406);//103.96406,30.781939
		schoolInfo7 = new SchoolInfo("临江苑操场", "临江苑操场",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng7);
		latLng8 = new LatLng(30.787524, 103.959515);//103.959515,30.787524
		schoolInfo8 = new SchoolInfo("德馨苑", "德馨苑",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng8);
		latLng9 = new LatLng(30.787058, 103.960521);//103.960521,30.787058
		schoolInfo9 = new SchoolInfo("德馨苑操场", "德馨苑操场",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng9);
		latLng10 = new LatLng(30.779822, 103.95859);//103.95859,30.779822
		schoolInfo10 = new SchoolInfo("1教", "1教",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng10);
		latLng11 = new LatLng(30.780093, 103.957521);//103.957521,30.780093
		schoolInfo11 = new SchoolInfo("2教", "2教",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng11);
		latLng12 = new LatLng(30.779271, 103.958159);//103.958159,30.779271
		schoolInfo12 = new SchoolInfo("3教", "3教",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng12);
		latLng13 = new LatLng(30.78128, 103.957889);//103.957889,30.78128
		schoolInfo13 = new SchoolInfo("4教", "4教",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng13);
		/*schoolInfo13 = new SchoolInfo("4教B区", "4教B区",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng13);
		schoolInfo13 = new SchoolInfo("4教C区", "4教C区",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng13);
		schoolInfo13 = new SchoolInfo("4教D区", "4教D区",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng13);*/
		latLng14 = new LatLng(30.784266, 103.960216);//103.960216,30.784266
		schoolInfo14 = new SchoolInfo("5教A区", "5教A区",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng14);
		latLng15 = new LatLng(30.785173, 103.960737);//103.960737,30.785173
		schoolInfo15 = new SchoolInfo("5教B区", "5教B区",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng15);
		latLng16 = new LatLng(30.785321, 103.961976);//103.961976,30.785321
		schoolInfo16 = new SchoolInfo("5教C区", "5教C区",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng16);
		latLng17 = new LatLng(30.782358, 103.961563);//103.961563,30.782358
		schoolInfo17 = new SchoolInfo("6教A区", "6教A区",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng17);
		latLng18 = new LatLng(30.783265, 103.9623);//103.9623,30.783265
		schoolInfo18 = new SchoolInfo("6教B区", "6教B区",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng18);
		latLng19 = new LatLng(30.783258, 103.961671);//103.961671,30.783258
		schoolInfo19 = new SchoolInfo("6教C区", "6教C区",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng19);
		latLng20 = new LatLng(30.783436, 103.963135);//103.963135,30.783436
		schoolInfo20 = new SchoolInfo("6教D区", "6教D区",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng20);
		latLng21 = new LatLng(30.784747, 103.967636);//103.967636,30.784747
		schoolInfo21 = new SchoolInfo("7教", "7教",
				BitmapDescriptorFactory.fromResource(R.drawable.icon_marka),
				latLng21);
		
		list.add(schoolInfo);
		list.add(schoolInfo1);
		list.add(schoolInfo2);
		list.add(schoolInfo3);
		list.add(schoolInfo4);
		list.add(schoolInfo5);
		list.add(schoolInfo6);
		list.add(schoolInfo7);
		list.add(schoolInfo8);
		list.add(schoolInfo9);
		list.add(schoolInfo10);
		list.add(schoolInfo11);
		list.add(schoolInfo12);
		list.add(schoolInfo13);
		list.add(schoolInfo14);
		list.add(schoolInfo15);
		list.add(schoolInfo16);
		list.add(schoolInfo17);
		list.add(schoolInfo18);
		list.add(schoolInfo19);
		list.add(schoolInfo20);
		list.add(schoolInfo21);
	}

	public void initOverlay(BaiduMap mBaiduMap) {
		for (int i = 0; i < list.size(); i++) {
			SchoolInfo schoolInfo1 = list.get(i);
			// 构建MarkerOption，用于在地图上添加Marker
			MarkerOptions option1 = new MarkerOptions()
					.position(schoolInfo1.getPoint())
					.icon(schoolInfo1.getBitmap()).zIndex(9);
			// 在地图上添加Marker，并显示
			listMarkers.add((Marker) mBaiduMap.addOverlay(option1));
		}
	}

	public void recycleBitmap() {
		for(SchoolInfo schoolInfo:list){
			schoolInfo.getBitmap().recycle();
		}
	}

	public List<SchoolInfo> getList() {
		return list;
	}

	public void setList(List<SchoolInfo> list) {
		this.list = list;
	}

	public List<Marker> getListMarkers() {
		return listMarkers;
	}

	public void setListMarkers(List<Marker> listMarkers) {
		this.listMarkers = listMarkers;
	}

}
