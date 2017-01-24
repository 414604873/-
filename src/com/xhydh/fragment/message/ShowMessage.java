package com.xhydh.fragment.message;

import org.apache.log4j.Logger;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;
import com.xhydh.fragment.WodeFragment;
import com.xhydh.map.R;
import com.xhydh.map.R.id;
import com.xhydh.map.R.layout;
import com.xhydh.map.R.menu;
import com.xhydh.utils.CollectDBHelper;
import com.xhydh.utils.LogUtils;
import com.xhydh.utils.MessageInfo;
import com.xhydh.utils.qqshare.BaseUIListener;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class ShowMessage extends Activity {
	TextView showTitle, showContent;
	MessageInfo messageInfo;
	//分享的key
	private static final String APP_ID = "1105362292";
	private Tencent mTencent;
	private Logger log = Logger.getLogger(getClass());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_message);

		showTitle = (TextView) findViewById(R.id.showTitle);
		showContent = (TextView) findViewById(R.id.showContent);
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		String content = intent.getStringExtra("content");
		showTitle.setText(title);
		showContent.setText(content);
		messageInfo = new MessageInfo(title, content);
		LogUtils.configLog();
		// Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
		// 其中APP_ID是分配给第三方应用的appid，类型为String。
		mTencent = Tencent.createInstance(APP_ID, getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.collect:
			if (WodeFragment.flag == true) {

				if (insertToCollect(WodeFragment.phoneString, messageInfo)) {
					log.debug(WodeFragment.phoneString+"收藏了"+messageInfo.getTitle());
					Toast.makeText(getApplicationContext(), "收藏成功",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(getApplicationContext(), "使用收藏功能请先登录！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.forward:
			onClickShare();
			if (WodeFragment.flag == true) {
				log.debug(WodeFragment.phoneString+"分享了"+messageInfo.getTitle());
			}
			Toast.makeText(getApplicationContext(), "转发", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 将选定信息收藏到本地
	public Boolean insertToCollect(String phone, MessageInfo messageInfo) {
		// TODO Auto-generated method stub
		CollectDBHelper collectDBHelper = new CollectDBHelper(
				getApplicationContext(), "collectmess", null, 1);
		SQLiteDatabase db = null;
		try {
			db = collectDBHelper.getWritableDatabase();
			String sql = "insert into collectmess values(?,?,?)";
			Object obj[] = { messageInfo.getTitle(), messageInfo.getContent(),
					phone };
			db.execSQL(sql, obj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "数据插入出错",
					Toast.LENGTH_SHORT).show();
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
	    mTencent.shareToQQ(this, params, new BaseUIListener(getApplicationContext()));
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null != mTencent)
			mTencent.onActivityResult(requestCode, resultCode, data);
	}
}
