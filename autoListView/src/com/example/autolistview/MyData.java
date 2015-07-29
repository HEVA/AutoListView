package com.example.autolistview;

import java.util.ArrayList;

import android.util.SparseArray;

/**
 * 模拟数据
 */
public class MyData {
	public String firstLetter; // 数据对应首字母
	public String data; // 具体数据

	private SparseArray<Integer> itemNums;
	static ArrayList<Integer> nums = new ArrayList<Integer>();
	static int a = 32;

	@Override
	public String toString() {
		return "MyData [firstLetter=" + firstLetter + ", data=" + data + "]";
	}

	/**
	 * 获得组数
	 * 
	 * @param datas
	 * @return
	 */
	public static int getLetter(ArrayList<MyData> datas) {
		int num = 1;
		for (int i = 0; i < datas.size() - 1; i++) {
			if (!datas.get(i).firstLetter.equals(datas.get(i + 1).firstLetter)) {
				num++;
			}
		}
		return num;
	}

	/**
	 * 获得前几组内的Item的总和
	 */
	public static int getSumCountForSection(ArrayList<MyData> datas, int section) {
		int sum = 0;
		for (int i = 0; i < section; i++) {
			sum += getDataCount(datas, i);
		}
		return sum;
	}

	/**
	 * 获得每组Item的个数
	 * 
	 * @param datas
	 * @return
	 */
	public static int getDataCount(ArrayList<MyData> datas, int setion) {

		SparseArray<String> firstLetters = SectionData(datas);
		int num = 0;
		for (int i = 0; i < getLetter(datas); i++) {
			if (setion == i) {
				String letter = firstLetters.get(i);

				for (int j = 0; j < datas.size(); j++) {
					if (datas.get(j).firstLetter.equals(letter)) {
						num++;
					}
				}
			}
		}
		return num;
	}

	/**
	 * 获得Tag值
	 * @param datas  数据源
	 * @param section   第几组
	 * @return
	 */
	public static String getHeaderText(ArrayList<MyData> datas, int section) {
		return datas.get(getSumCountForSection(datas, section)).firstLetter;
	}

	/**
	 * 抽取出每一组数组的标记值 类似TAG
	 * 
	 * @param datas
	 * @return
	 */

	public static SparseArray<String> SectionData(ArrayList<MyData> datas) {
		SparseArray<String> firstLetters = new SparseArray<String>();
		int num = 0;
		for (int i = 0; i < datas.size() - 1; i++) {
			if (!datas.get(i).firstLetter.equals(datas.get(i + 1).firstLetter)) {
				firstLetters.put(num, datas.get(i).firstLetter);
				num++;
			}
		}
		firstLetters.put(num, datas.get(datas.size() - 1).firstLetter);
		return firstLetters;
	}

	/**
	 * 模拟数据
	 * 
	 * @return
	 */
	public static ArrayList<MyData> getData() {

		ArrayList<MyData> datas = new ArrayList<MyData>();
		for (int j = 0; j < 2; j++) {
			a++;
			for (int i = 0; i < 15; i++) {
				MyData data = new MyData();
				data.firstLetter = a + "a";
				data.data = String.valueOf(a) + i;
				datas.add(data);
			}
		}
		return datas;
	}

}
