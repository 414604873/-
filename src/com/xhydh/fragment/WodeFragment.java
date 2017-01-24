package com.xhydh.fragment;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.xhydh.fragment.message.ShowCollect;
import com.xhydh.map.Login;
import com.xhydh.map.R;
import com.xhydh.utils.HttpUtils;
import com.xhydh.utils.LogUtils;
import com.xhydh.utils.SdCardUtils;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WodeFragment extends Fragment {
	public TextView phone, collect;
	public static boolean flag;
	public static String phoneString;
	private Button login_out;
	private Logger log = Logger.getLogger(getClass());
	HttpUtils httpUtils;
	SdCardUtils sdCardUtils;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.wode, container, false);
		LogUtils.configLog();
		phone = (TextView) view.findViewById(R.id.phone);
		collect = (TextView) view.findViewById(R.id.collect);
		login_out = (Button) view.findViewById(R.id.login_out);
		collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (flag == false) {
					Toast.makeText(getActivity(), "使用收藏功能请先登录！",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(getActivity(),ShowCollect.class);
					startActivity(intent);
				}

			}
		});
		//退出登录
		login_out.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			Intent intent = new Intent(getActivity(),Login.class);
			startActivity(intent);
			if( flag == true) {
				log.debug(phone.getText().toString()+"退出登录");
			}
			getActivity().finish();
			//上传日志
			httpUtils = new HttpUtils();
			sdCardUtils = new SdCardUtils(getActivity());
			String logContent = sdCardUtils.getData("xhydh_log.log");
			Log.e("logContent--->>>", logContent);
			httpUtils.uploadLogs("http://172.20.10.6:8080/xhydh/servlet/UploadLogs", logContent);
			}
		});
		checkIfLogin();
		return view;
	}

	// 判断是否登录
	private void checkIfLogin() {
		// TODO Auto-generated method stub
		Intent intent = getActivity().getIntent();
		phoneString = intent.getStringExtra("phone");
		if (phoneString != null && !phoneString.isEmpty()) {
			phone.setText(phoneString);
		} else {
			phone.setText("游客");
		}
	}

}
