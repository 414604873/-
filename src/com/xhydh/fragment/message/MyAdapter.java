package com.xhydh.fragment.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xhydh.map.R;
import com.xhydh.utils.MessageInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private LayoutInflater mInflater = null;
	//private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	List<MessageInfo> data = new ArrayList<MessageInfo>();
	public MyAdapter() {

	}

	public MyAdapter(Context context, List<MessageInfo> data) {
		// ����context�����ļ��ز���
		this.mInflater = LayoutInflater.from(context);
		this.data = data;
	}

	// �ڴ�������������������ݼ��е���Ŀ��
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}
	//��ȡ���ݼ�����ָ��������Ӧ��������
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}
	 //��ȡ���б�����ָ��������Ӧ����id
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// ����
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.message_list_item, null);
		}

		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		TextView title = (TextView) convertView.findViewById(R.id.title);

		image.setImageResource(R.drawable.message);
		title.setText(data.get(position).getTitle());
		return convertView;
	}

}
