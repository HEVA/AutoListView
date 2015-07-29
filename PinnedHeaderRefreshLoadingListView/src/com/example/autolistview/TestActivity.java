package com.example.autolistview;

import java.util.ArrayList;
import java.util.List;

import com.example.autolistview.adapter.ListViewAdapter;
import com.example.autolistview.adapter.MyData;
import com.example.autolistview.widget.AutoListView;
import com.example.autolistview.widget.AutoListView.OnLoadListener;
import com.example.autolistview.widget.AutoListView.OnRefreshListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * @author SunnyCoffee
 * @date 2014-1-28
 * @version 1.0
 * @desc listview下拉刷新，上拉自动加载更多。 http：//blog.csdn.com/limb99
 */

public class TestActivity extends Activity implements OnRefreshListener,
		OnLoadListener {

	private AutoListView lstv;
	private ListViewAdapter adapter;
	private ArrayList<MyData> list = new ArrayList<MyData>();
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ArrayList<MyData> result = (ArrayList<MyData>) msg.obj;
			switch (msg.what) {
			case AutoListView.REFRESH:
				lstv.onRefreshComplete();
				list.clear();
				list.addAll(result);
				lstv.setResultSize(result.size());
				break;
			case AutoListView.LOAD:
				lstv.onLoadComplete();
				list.addAll(result);
				lstv.setResultSize(list.size());
				break;
			}
		
			adapter.notifyDataSetChanged();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		lstv = (AutoListView) findViewById(R.id.lstv);
		list = MyData.getData();
//		adapter = new ListViewAdapter(this,lstv, list);
//		lstv.setAdapter(adapter);
		lstv.setOnRefreshListener(this);
		lstv.setOnLoadListener(this);
		initData();
	}

	private void initData() {
//		loadData(AutoListView.REFRESH);
	}

	private void loadData(final int what) {
		// 这里模拟从服务器获取数据
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(700);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Message msg = handler.obtainMessage();
				msg.what = what;
				msg.obj = getData();
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void onRefresh() {
//		loadData(AutoListView.REFRESH);
	}

	@Override
	public void onLoad() {
//		loadData(AutoListView.LOAD);
	}

	// 测试数据
	public List<MyData> getData() {
		
		return MyData.getData();
	}
}
