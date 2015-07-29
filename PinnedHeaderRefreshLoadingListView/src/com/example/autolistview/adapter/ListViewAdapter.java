package com.example.autolistview.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.example.autolistview.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author SunnyCoffee
 * @date 2014-2-2
 * @version 1.0
 * @desc 适配器
 * 
 */
public class ListViewAdapter extends BaseAdapter {

	private ViewHolder holder;
	private ArrayList<MyData> datas;
	private Context context;
	private ViewGroup group;
	public Map<String, Integer> maps;
	
	public ListViewAdapter(Context context,ViewGroup group) {
		this(context,group,null);
		this.group =  group;
		this.context = context;
		sortLetter(datas);
	
	}
	
	public ListViewAdapter(Context context,ViewGroup group, ArrayList<MyData> datas) {
		this.datas = datas;
		this.group =  group;
		this.context = context;
		sortLetter(datas);
	}

	public ListViewAdapter(Context context) {
		this(context,null);
		this.context = context;
		this.datas = MyData.getData();
		sortLetter(datas);
		
	}


	/**
	 * 获取需要顶部悬浮显示的view
	 */
	public View getPinnedSectionView(int position) {
		ViewGroup view = (ViewGroup) getView(position, null,
				group);
		View vAlpha = view.getChildAt(0);
		return vAlpha;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public MyData getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = View.inflate(context, R.layout.alpha_item, null);
		final TextView tvAlpha = (TextView) view
				.findViewById(R.id.alphaitem_tv_alpha);
		TextView tvContent = (TextView) view
				.findViewById(R.id.alphaitem_tv_content);

		MyData myData = (MyData) getItem(position);

		tvAlpha.setText(myData.firstLetter);
		if (maps.get(myData.firstLetter) == position) {
			tvAlpha.setVisibility(View.VISIBLE);
		} else {
			tvAlpha.setVisibility(View.GONE);
		}
		tvAlpha.setTag(position);
		tvContent.setText(myData.data);

		return view;
	}

	private static class ViewHolder {
		TextView text;
	}
	
	private void sortLetter(ArrayList<MyData> datas) {
		Collections.sort(datas, new Comparator<MyData>() {
			@Override
			public int compare(MyData lhs, MyData rhs) {
				return lhs.firstLetter.compareTo(rhs.firstLetter);
			}
		});

		maps = new HashMap<String, Integer>();
		for (int i = 0; i < datas.size(); i++) {
			if (!maps.containsKey(datas.get(i).firstLetter)) {
				maps.put(datas.get(i).firstLetter, i);
			}
		}
	}
}
