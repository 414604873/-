package com.xhydh.fragment.message;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.xhydh.fragment.WodeFragment;
import com.xhydh.map.Login;
import com.xhydh.map.R;
import com.xhydh.map.R.layout;
import com.xhydh.utils.CollectDBHelper;
import com.xhydh.utils.MessageInfo;
import com.xhydh.utils.UserDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class ShowCollect extends Activity {

	private ListView message;
	List<MessageInfo> data = new ArrayList<MessageInfo>();
	MyAdapter myAdapter;
	MessageInfo messageInfo;
	private Logger log = Logger.getLogger(getClass());
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_list);
		message = (ListView) findViewById(R.id.message);
		data = getData(WodeFragment.phoneString);
		myAdapter = new MyAdapter(getApplicationContext(), data);// �˴���������Ϊ
		message.setAdapter(myAdapter);
		myAdapter.notifyDataSetChanged();
		// ע��˵�������
		registerForContextMenu(message);

		// �����ʾ
		message.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), ShowMessage.class);
				intent.putExtra("title", data.get(arg2).getTitle());
				intent.putExtra("content", data.get(arg2).getContent());
				startActivity(intent);
			}
		});

		// ��ȡ������ֵ
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
	}

	// ���ɲ˵�
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.collect_menu, menu);

	}

	// �˵�ѡ���¼�
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.cacel_collect:
			if (WodeFragment.flag == true) {

				if (deleteCollect(WodeFragment.phoneString, messageInfo)) {
					log.debug(WodeFragment.phoneString+"ȡ���ղ�"+messageInfo.getTitle());
					Toast.makeText(getApplicationContext(), "ȡ���ղسɹ�",
							Toast.LENGTH_SHORT).show();
					// ����
					Intent intent = new Intent(getApplicationContext(),
							ShowCollect.class);
					startActivity(intent);
					this.finish();
				}

			} 
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	// ��ѡ����Ϣ���ղ���ɾ��
	public Boolean deleteCollect(String phoneString, MessageInfo messageInfo) {
		// TODO Auto-generated method stub
		CollectDBHelper collectDBHelper = new CollectDBHelper(
				getApplicationContext(), "collectmess", null, 1);
		SQLiteDatabase db = null;
		try {
			db = collectDBHelper.getWritableDatabase();
			String sql = "delete from collectmess where title=? and phone=? ";
			Object obj[] = { messageInfo.getTitle(), phoneString };
			db.execSQL(sql, obj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "����ɾ������",
					Toast.LENGTH_SHORT).show();
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return true;
	}

	public List<MessageInfo> getData(String phonesString) {
		List<MessageInfo> list = new ArrayList<MessageInfo>();
		CollectDBHelper collectDBHelper = new CollectDBHelper(
				getApplicationContext(), "collectmess", null, 1);
		SQLiteDatabase db = null;
		try {
			db = collectDBHelper.getWritableDatabase();
			String sql = "select distinct title,content from collectmess where phone=?";
			Cursor cursor = db.rawQuery(sql, new String[] { phonesString });
			int count = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				MessageInfo messageInfo = new MessageInfo();
				messageInfo.setTitle(cursor.getString((cursor
						.getColumnIndex("title"))));
				messageInfo.setContent(cursor.getString((cursor
						.getColumnIndex("content"))));

				list.add(messageInfo);

			}
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), "���ݲ�ѯ����",
					Toast.LENGTH_SHORT).show();
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return list;

	}
}
