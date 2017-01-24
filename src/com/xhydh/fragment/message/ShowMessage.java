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
	//�����key
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
		// Tencent����SDK����Ҫʵ���࣬�����߿�ͨ��Tencent�������Ѷ���ŵ�OpenAPI��
		// ����APP_ID�Ƿ����������Ӧ�õ�appid������ΪString��
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
					log.debug(WodeFragment.phoneString+"�ղ���"+messageInfo.getTitle());
					Toast.makeText(getApplicationContext(), "�ղسɹ�",
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(getApplicationContext(), "ʹ���ղع������ȵ�¼��",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.forward:
			onClickShare();
			if (WodeFragment.flag == true) {
				log.debug(WodeFragment.phoneString+"������"+messageInfo.getTitle());
			}
			Toast.makeText(getApplicationContext(), "ת��", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// ��ѡ����Ϣ�ղص�����
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
			Toast.makeText(getApplicationContext(), "���ݲ������",
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
	    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "����Ӧ��222222");	
	    mTencent.shareToQQ(this, params, new BaseUIListener(getApplicationContext()));
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null != mTencent)
			mTencent.onActivityResult(requestCode, resultCode, data);
	}
}
