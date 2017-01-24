package com.xhydh.map;

import android.app.Application;
import cn.jpush.android.api.JPushInterface;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.mapapi.SDKInitializer;

public class MapApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
		
	}

}