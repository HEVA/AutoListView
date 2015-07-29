package com.example.autolistview.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.autolistview.MyData;
import com.example.autolistview.R;
import com.example.autolistview.widget.AutoListView.PinnedSectionedHeaderAdapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author
 * @date 2014-2-2
 * @version 1.0
 * @desc 适配器
 * 
 */
public class ListViewAdapter extends SectionedBaseAdapter {

	private ArrayList<MyData> list;
	private Context context;
	int dataPos = 0;

	public ListViewAdapter(Context context, ArrayList<MyData> list) {
		this.list = list;
	}

	@Override
	public Object getItem(int section, int position) {
		return null;
	}

	@Override
	public long getItemId(int section, int position) {
		return 0;
	}

	// 多少组
	@Override
	public int getSectionCount() {
		return MyData.getLetter(list);
	}

	// 每组的个数
	@Override
	public int getCountForSection(int section) {
		return MyData.getDataCount(list, section);
	}

	public int getSumCountForSection(int section) {
		int sum = 0;
		for (int i = 0; i < section; i++) {
			sum += getCountForSection(i);
		}
		return sum;
	}

	@Override
	public View getItemView(int section, int position, View convertView, ViewGroup parent) {

		LinearLayout layout = null;

		if (section > 0) {
			dataPos = getSumCountForSection(section) + position;
		} else {
			dataPos = position;
		}
		if(dataPos >= list.size())
			dataPos = list.size()-1;
		System.out.println("+++++++++++++++++" + dataPos + "/" + list.size());
		MyData data = list.get(dataPos);
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (LinearLayout) inflator.inflate(R.layout.list_item, null);
		} else {
			layout = (LinearLayout) convertView;
		}

		((TextView) layout.findViewById(R.id.textItem)).setText(data.data);
		return layout;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
		LinearLayout layout = null;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (LinearLayout) inflator.inflate(R.layout.header_item, null);
		} else {
			layout = (LinearLayout) convertView;
		}
		((TextView) layout.findViewById(R.id.textItem)).setText(MyData.getHeaderText(list, section));
		return layout;
	}
	private void sortLetter(ArrayList<MyData> datas) {
		Collections.sort(datas, new Comparator<MyData>() {
			@Override
			public int compare(MyData lhs, MyData rhs) {
				return lhs.firstLetter.compareTo(rhs.firstLetter);
			}
		});
	}
}
