package com.xhydh.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class SdCardUtils {
	private Context context;

	public SdCardUtils() {
		// TODO Auto-generated constructor stub
	}

	public SdCardUtils(Context context) {
		super();
		this.context = context;
	}

	public String getData(String filename) {
		FileInputStream fileInputStream = null;
		String result = "";
		File file = new File(Environment.getExternalStorageDirectory(),
				filename);
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			try {
				fileInputStream = new FileInputStream(file);
				byte[] b = new byte[fileInputStream.available()];
				fileInputStream.read(b);
				result = new String(b);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return result;// new String(outputStream.toByteArray());

	}

	public boolean save(String filename, String content) {
		boolean flag = false;
		FileOutputStream fileOutputStream = null;
		// 获得sd卡所在路径
		File file = new File(Environment.getExternalStorageDirectory(),
				filename);
		// 判断sd卡是否可用
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			try {
				fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(content.getBytes());
				flag = true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return flag;
	}
}
