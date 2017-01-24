package com.xhydh.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.xhydh.map.R;

//完成对数据的解析
public class JsonTools {

	public JsonTools() {
		// TODO Auto-generated constructor stub
	}

	public static MessageInfo getMessageInfo(String key, String jsonString) {
		MessageInfo messageInfo = new MessageInfo();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONObject messObject = jsonObject.getJSONObject(key);
			System.err.println(messObject.toString());
			messageInfo.setTitle(messObject.getString("Title"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return messageInfo;

	}

	public static List<MessageInfo> getMessageList(String key, String jsonString) {
		List<MessageInfo> list = new ArrayList<MessageInfo>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			//json数组
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			System.out.println(jsonArray.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject2 = jsonArray.getJSONObject(i);
				MessageInfo messageInfo = new MessageInfo();
				messageInfo.setTitle(jsonObject2.getString("title"));
				messageInfo.setContent(jsonObject2.getString("content"));
				list.add(messageInfo);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;

	}
	
}
