package com.xhydh.utils;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.model.LatLng;

public class SchoolInfo {
	//����
	private String point_name;
	//��ַ
	private String address;
	//��ͼ��עͼ��
	private BitmapDescriptor bitmap;
	//�õ�ľ�γ��
	private LatLng point;
	//��Ϣ�б�
	private List<SchoolInfo> list = new ArrayList<SchoolInfo>();
	
	
	public SchoolInfo() {
		super();
	}
	
	public SchoolInfo(String point_name, String address,
			BitmapDescriptor bitmap, LatLng point) {
		super();
		this.point_name = point_name;
		this.address = address;
		this.bitmap = bitmap;
		this.point = point;
	}

	public String getPoint_name() {
		return point_name;
	}
	public void setPoint_name(String point_name) {
		this.point_name = point_name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public BitmapDescriptor getBitmap() {
		return bitmap;
	}
	public void setBitmap(BitmapDescriptor bitmap) {
		this.bitmap = bitmap;
	}
	public LatLng getPoint() {
		return point;
	}
	public void setPoint(LatLng point) {
		this.point = point;
	}
	public List<SchoolInfo> getList() {
		return list;
	}
	public void setList(List<SchoolInfo> list) {
		this.list = list;
	}
	
}
