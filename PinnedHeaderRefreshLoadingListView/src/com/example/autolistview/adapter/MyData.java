package com.example.autolistview.adapter;

import java.util.ArrayList;
/**
 * 模拟数据
 */
public class MyData {
	public String firstLetter; // 数据对应首字母
	public String data; // 具体数据

	@Override
	public String toString() {
		return "MyData [firstLetter=" + firstLetter + ", data=" + data + "]";
	}

	public static ArrayList<MyData> getData() {
		ArrayList<MyData> datas = new ArrayList<MyData>();
		for (int i = 0; i <15; i++) {
			MyData data = new MyData();
			data.firstLetter = "a";
			data.data = "a" + i;
			datas.add(data);
		}
		for (int i = 0; i < 5; i++) {
			MyData data = new MyData();
			data.firstLetter = "e";
			data.data = "e" + i;
			datas.add(data);
		}
		for (int i = 0; i < 7; i++) {
			MyData data = new MyData();
			data.firstLetter = "b";
			data.data = "b" + i;
			datas.add(data);
		}
		for (int i = 0; i < 10; i++) {
			MyData data = new MyData();
			data.firstLetter = "w";
			data.data = "w" + i;
			datas.add(data);
		}
		return datas;
	}
}
