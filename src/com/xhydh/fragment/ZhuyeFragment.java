package com.xhydh.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.xhydh.map.Daohang;
import com.xhydh.map.Lixian;
import com.xhydh.map.R;
import com.xhydh.map.Dingwei;

public class ZhuyeFragment extends Fragment implements OnClickListener {
	Button dingwei, lixian, daohang;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.zhuye, container, false);
		dingwei = (Button) view.findViewById(R.id.dingwei);
		lixian = (Button) view.findViewById(R.id.lixian);
		daohang = (Button) view.findViewById(R.id.daohang);
		dingwei.setOnClickListener(this);
		lixian.setOnClickListener(this);
		daohang.setOnClickListener(this);
		Intent intent = getActivity().getIntent();
		WodeFragment.phoneString = intent.getStringExtra("phone");
		return view;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.dingwei:
			intent = new Intent(getActivity(), Dingwei.class);
			startActivity(intent);
			break;
		case R.id.lixian:
			intent = new Intent(getActivity(), Lixian.class);
			startActivity(intent);
			Toast.makeText(getActivity(), "¿Îœﬂ", 1).show();
			break;
		case R.id.daohang:
			intent = new Intent(getActivity(), Daohang.class);
			startActivity(intent);
			Toast.makeText(getActivity(), "µº∫Ω", 1).show();
			break;
		default:
			break;
		}
	}
}
