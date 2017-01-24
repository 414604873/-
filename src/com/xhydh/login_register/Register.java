package com.xhydh.login_register;

import android.R.drawable;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xhydh.map.Login;
import com.xhydh.map.R;
import com.xhydh.utils.UserDBHelper;
import com.xhydh.utils.UserInfo;

public class Register extends Activity {
	Button registerButton;
	EditText phone, pswd, confirmPswd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		getActionBar().setDisplayHomeAsUpEnabled(true);// 设置actionbar的返回按钮
		// getActionBar().setCustomView(R.layout.action_bar);//设置自定义的actionbar显示。
		// getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);//实现显示
		phone = (EditText) findViewById(R.id.register_username_edit);
		pswd = (EditText) findViewById(R.id.register_password_edit);
		confirmPswd = (EditText) findViewById(R.id.register_confirm_password_edit);
		registerButton = (Button) findViewById(R.id.register_button);
		phone.setInputType(InputType.TYPE_CLASS_PHONE);// 电话
		registerButton.setOnClickListener(new UserRegister());
	}

	//
	class UserRegister implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (phone.getText().toString().trim().isEmpty()) {
				Toast.makeText(getApplicationContext(), "手机号不能为空",
						Toast.LENGTH_SHORT).show();
			} else if (pswd.getText().toString().trim().isEmpty()) {
				Toast.makeText(getApplicationContext(), "密码不能为空",
						Toast.LENGTH_SHORT).show();
			} else if (confirmPswd.getText().toString().trim().isEmpty()) {
				Toast.makeText(getApplicationContext(), "确认密码号不能为空",
						Toast.LENGTH_SHORT).show();
			} else if (!pswd.getText().toString().trim()
					.equals(confirmPswd.getText().toString().trim())) {
				Toast.makeText(getApplicationContext(), "前后密码不相符，请重输",
						Toast.LENGTH_SHORT).show();
				pswd.setText("");
				confirmPswd.setText("");
			} else if (phone.getText().toString().trim().length() < 11) {
				Toast.makeText(getApplicationContext(), "手机号不能少于11位",
						Toast.LENGTH_SHORT).show();
			} else {
				UserInfo userInfo = new UserInfo(phone.getText().toString()
						.trim(), pswd.getText().toString().trim());
				if (checkRegister(userInfo)) {
					Toast.makeText(getApplicationContext(), "注册成功！",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(Register.this, Login.class);
					startActivity(intent);
					Register.this.finish();
				} else {
					Toast.makeText(getApplicationContext(), "注册失败，请重试！",
							Toast.LENGTH_SHORT).show();
					phone.setText("");
					pswd.setText("");
					confirmPswd.setText("");
				}
			}
		}

	}

	// 将注册信息存入数据库
	public boolean checkRegister(UserInfo userInfo) {
		UserDBHelper userDBHelper = new UserDBHelper(Register.this, "user",
				null, 1);
		SQLiteDatabase db = null;
		try {
			if (checkExist(userInfo.getPhone())) {
				db = userDBHelper.getWritableDatabase();
				String sql = "insert into user(phone,pswd) values(?,?)";
				Object obj[] = { userInfo.getPhone(), userInfo.getPswd() };
				db.execSQL(sql, obj);
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(), "数据库错误",
					Toast.LENGTH_SHORT).show();
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return false;
	}

	public boolean checkExist(String phoneString) {
		UserDBHelper userDBHelper = new UserDBHelper(Register.this, "user",
				null, 1);
		SQLiteDatabase db = null;
		try {
			db = userDBHelper.getWritableDatabase();
			String sql = "select * from user where phone=?";
			Cursor cursor = db.rawQuery(sql, new String[] { phoneString });
			if (cursor.moveToFirst() == true) {
				cursor.close();
				Toast.makeText(getApplicationContext(), "用户名已存在！",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return true;
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return true;
	}

	// 实现返回按钮的功能
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}