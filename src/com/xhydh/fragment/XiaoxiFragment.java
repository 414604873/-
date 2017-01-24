package com.xhydh.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.xhydh.fragment.message.MyAdapter;
import com.xhydh.fragment.message.ShowMessage;
import com.xhydh.map.R;
import com.xhydh.utils.CollectDBHelper;
import com.xhydh.utils.HttpUtils;
import com.xhydh.utils.JsonTools;
import com.xhydh.utils.LogUtils;
import com.xhydh.utils.MessageInfo;
import com.xhydh.utils.qqshare.BaseUIListener;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class XiaoxiFragment extends Fragment {
	private ListView message;
	List<MessageInfo> data = new ArrayList<MessageInfo>();
	MessageInfo messageInfo;
	MyAdapter myAdapter;
	private static final String APP_ID = "1105362292";
	private Tencent mTencent;
	private Logger log = Logger.getLogger(getClass());
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.message_list, container, false);
		
		
		// 解决俄android4.0以上网络通信得放子线程问题
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		message = (ListView) view.findViewById(R.id.message);
		data = getData();
		myAdapter = new MyAdapter(getActivity(), data);// 此处的上下文为
		message.setAdapter(myAdapter);
		// 注册菜单上下文
		registerForContextMenu(message);
		// 点击显示
		message.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ShowMessage.class);
				intent.putExtra("title", data.get(arg2).getTitle());
				intent.putExtra("content", data.get(arg2).getContent());
				startActivity(intent);
				if (WodeFragment.flag == true) {
					log.debug(WodeFragment.phoneString+"点开了"+data.get(arg2).getTitle());
				}
			}
		});

		// 获取长按的值
		message.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				messageInfo = new MessageInfo(data.get(arg2).getTitle(), data
						.get(arg2).getContent());
				return false;
			}
		});
		LogUtils.configLog();
		// Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
		// 其中APP_ID是分配给第三方应用的appid，类型为String。
		mTencent = Tencent.createInstance(APP_ID, getActivity());
		return view;
	}

	public static List<MessageInfo> getData() {
		String path = "http://172.20.10.6:8080/xhydh/servlet/GetMessage?action_flag=messageTitle";
		String jsonString1 = HttpUtils.getJsonString(path);
		List<MessageInfo> list = JsonTools.getMessageList("messageTitle",
				jsonString1);
		Log.e("message1--->>>", jsonString1);
		Log.e("message2--->>>", list.toString());
		return list;
	}

	// 生成菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.main, menu);

	}

	// 菜单选择事件
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.collect:
			if (WodeFragment.flag == true) {

				if (insertToCollect(WodeFragment.phoneString, messageInfo)) {
					log.debug(WodeFragment.phoneString+"收藏了"+messageInfo.getTitle());
					Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT)
							.show();
				}

			} else {
				Toast.makeText(getActivity(), "使用收藏功能请先登录！", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.forward:
				if (WodeFragment.flag == true) {
					log.debug(WodeFragment.phoneString+"分享了"+messageInfo.getTitle());
				}
				Toast.makeText(getActivity(), "转发", Toast.LENGTH_SHORT).show();
				onClickShare();
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	// 将选定信息收藏到本地
	public Boolean insertToCollect(String phone, MessageInfo messageInfo) {
		// TODO Auto-generated method stub
		CollectDBHelper collectDBHelper = new CollectDBHelper(getActivity(),
				"collectmess", null, 1);
		SQLiteDatabase db = null;
		try {
			db = collectDBHelper.getWritableDatabase();
			String sql = "insert into collectmess values(?,?,?)";
			Object obj[] = { messageInfo.getTitle(), messageInfo.getContent(),
					phone };
			db.execSQL(sql, obj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getActivity(), "数据插入出错", Toast.LENGTH_SHORT).show();
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return true;
	}

	private void onClickShare() { 
	    final Bundle params = new Bundle();
	    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
	    params.putString(QQShare.SHARE_TO_QQ_TITLE, messageInfo.getTitle());
	    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://www.qq.com");
	    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
	    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "测试应用222222");	
	    mTencent.shareToQQ(getActivity(), params, new BaseUIListener(getActivity()));
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null != mTencent)
			mTencent.onActivityResult(requestCode, resultCode, data);
	}
}
