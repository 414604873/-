package com.xhydh.map;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Poi_RouteInfo extends Activity {
	private ListView listView;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_info);
		listView = (ListView) findViewById(R.id.route_list);
		textView=(TextView) findViewById(R.id.route_info);
		
		if (Poi_Daohang.distance<1000) {
			textView.setText("总计："+Poi_Daohang.distance+"m");
		}else {
			textView.setText("总计："+(((float)(Poi_Daohang.distance))/1000)+"km");
		}
		//listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, R.id.route_info,Daohang.route_line));
				listView.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int position, View view, ViewGroup arg2) {
				if (view == null) {
					view = LayoutInflater.from(getApplicationContext())
							.inflate(R.layout.showroute, null);
				}
				TextView textView = (TextView) view.findViewById(R.id.show_route);
				String string = Poi_Daohang.route_line.get(position);
				textView.setText(string);
				return view;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return Poi_Daohang.route_line.get(position);
			}

			@Override
			public int getCount() {
				return Poi_Daohang.route_line.size();
			}
		});
		}

}
