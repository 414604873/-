package com.xhydh.map;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.xhydh.fragment.WodeFragment;
import com.xhydh.login_register.Register;
import com.xhydh.main.MainActivity;
import com.xhydh.map.R;
import com.xhydh.utils.LogUtils;
import com.xhydh.utils.UserDBHelper;

import de.mindpipe.android.logging.log4j.LogConfigurator;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {
	TextView visit, register_link;
	EditText phone, pswd;
	Button loginButton;
	private Logger log = Logger.getLogger(getClass());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		getActionBar().setTitle("��¼");
		// ��������
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY,
				"F9IKoxo8zzixZLQnl9RfZE1pdGz06MBV");
		visit = (TextView) findViewById(R.id.visit);
		register_link = (TextView) findViewById(R.id.register_link);
		phone = (EditText) findViewById(R.id.username_edit);
		pswd = (EditText) findViewById(R.id.password_edit);
		loginButton = (Button) findViewById(R.id.signin_button);
		phone.setInputType(InputType.TYPE_CLASS_PHONE);// �绰

		loginButton.setOnClickListener(new UserLogin());
		visit.setOnClickListener(this);
		register_link.setOnClickListener(this);
		LogUtils.configLog();// ����log4j
	}

	// ��¼�¼�
	class UserLogin implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (checkLogin(phone.getText().toString().trim(), pswd.getText()
					.toString().trim())) {
				Toast.makeText(getApplicationContext(), "��¼�ɹ���",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(Login.this, MainActivity.class);
				WodeFragment.flag = true;
				String userMess = phone.getText().toString().trim();
				log.debug(userMess + "��¼");
				intent.putExtra("phone", phone.getText().toString().trim());
				startActivity(intent);
				Login.this.finish();
			} else if (phone.getText().toString().trim().isEmpty()) {
				Toast.makeText(getApplicationContext(), "�ֻ��Ų���Ϊ��",
						Toast.LENGTH_SHORT).show();
			} else if (pswd.getText().toString().trim().isEmpty()) {
				Toast.makeText(getApplicationContext(), "���벻��Ϊ��",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "��¼ʧ�ܣ��˺������벻ƥ�䣬���صǣ�",
						Toast.LENGTH_SHORT).show();
				phone.setText("");
				pswd.setText("");
			}
		}

	}

	// ����¼��Ϣ�����ݿ����ݱȶ�
	public boolean checkLogin(String phone, String pswd) {
		UserDBHelper userDBHelper = new UserDBHelper(Login.this, "user", null,
				1);
		SQLiteDatabase db = null;
		try {
			db = userDBHelper.getWritableDatabase();
			String sql = "select * from user where phone=? and pswd=?";
			Cursor cursor = db.rawQuery(sql, new String[] { phone, pswd });
			if (cursor.moveToFirst() == true) {
				cursor.close();
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return false;
	}

	// ����¼�
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.visit:
			intent = new Intent(Login.this, MainActivity.class);
			WodeFragment.flag = false;
			startActivity(intent);
			break;
		case R.id.register_link:
			intent = new Intent(Login.this, Register.class);
			startActivity(intent);
			break;
		}

	}
}
