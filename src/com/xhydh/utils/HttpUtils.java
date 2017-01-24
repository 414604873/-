package com.xhydh.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.widget.Toast;


public class HttpUtils {

	public HttpUtils() {
		// TODO Auto-generated constructor stub
	}

	public static String getJsonString(String path) {
		try {
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setConnectTimeout(3000);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			int code = httpURLConnection.getResponseCode();
			System.out.println(code);
			if (code == 200) {
				return changeInp(httpURLConnection.getInputStream());
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return path;

	}

	private static String changeInp(InputStream inputStream) {
		String jsonString = "";
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			byte[] data = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(data)) != -1) {
				outputStream.write(data, 0, len);
			}
			jsonString = new String(outputStream.toByteArray());
			System.out.println(jsonString);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return jsonString;
	}
	
	public void uploadLogs(String path,String logContent)
	{
		 try {
			   URL url = new URL(path);
			   HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			   conn.setDoOutput(true);
			   conn.setDoInput(true);
			   conn.setChunkedStreamingMode(1024*1024);  
			   conn.setRequestMethod("POST");
			   conn.setRequestProperty("connection", "Keep-Alive");
			   conn.setRequestProperty("Charsert", "UTF-8");
			   String fname = "xhydh_log.log";
			   //File file = new File(fname);
			   
			   conn.setRequestProperty("Content-Type","multipart/form-data;file="+fname);
			   conn.setRequestProperty("logname",fname);
			   OutputStream out = new DataOutputStream(conn.getOutputStream());
			   ByteArrayInputStream in = new ByteArrayInputStream(logContent.getBytes());
			   int len = 0;
			   byte[] bufferOut = new byte[1024];
			   while ((len = in.read(bufferOut)) != -1) {
			    out.write(bufferOut, 0, len);
			   }
			   in.close();
			   out.flush();
			   out.close();
			   BufferedReader reader = new BufferedReader(new InputStreamReader(
			     conn.getInputStream()));
			   String line = null;
			   while ((line = reader.readLine()) != null) {
			    System.out.println(line);
			   }
			  } catch (Exception e) {
			   System.out.println("发送POST请求出现异常！" + e);
			   e.printStackTrace();
			  }
	}
}
