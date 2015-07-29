package com.example.autolistview.widget;

import com.example.autolistview.R;
import com.example.autolistview.adapter.ListViewAdapter;
import com.example.autolistview.adapter.MyData;
import com.example.autolistview.utils.UnitUtil;
import com.example.autolistview.utils.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author Lenny
 * @create 2015-10-24
 * @version 1.0
 * @desc 自定义Listview 下拉刷新,上拉加载更多 PinnedHeader
 */

public class AutoListView extends ListView implements OnScrollListener {

	// 区分当前操作是刷新还是加载
	public static final int REFRESH = 0;
	public static final int LOAD = 1;

	// 区分PULL和RELEASE的距离的大小
	private static final int SPACE = 20;

	// 定义header的四种状态和当前状态
	private static final int NONE = 0;
	private static final int PULL = 1;
	private static final int RELEASE = 2;
	private static final int REFRESHING = 3;
	private int state;

	private LayoutInflater inflater;
	private View header;
	private View footer;
	private TextView tip;
	private TextView lastUpdate;
	private ImageView arrow;
	private ProgressBar refreshing;

	private TextView noData;
	private TextView loadFull;
	private TextView more;
	private ProgressBar loading;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;
	boolean isFirstDing = true;
	private int firstVisibleItem;
	private int scrollState;
	private int headerContentInitialHeight;
	private int headerContentHeight;

	// 只有在listview第一个item显示的时候（listview滑到了顶部）才进行下拉刷新， 否则此时的下拉只是滑动listview
	private boolean isRecorded;
	private boolean isLoading;// 判断是否正在加载
	private boolean loadEnable = true;// 开启或者关闭加载更多功能
	private boolean isLoadFull;
	private int pageSize = 10;

	private OnRefreshListener onRefreshListener;
	private OnLoadListener onLoadListener;

	private View recyclePinnedView;
	View pinnedView;
	// 下拉的距离
	private int mTranslateY;
	// 固定头是否显示
	private boolean isPinnedHeaderShown;

	private Context context;
	private ListViewAdapter adapter;

	public AutoListView(Context context) {
		super(context);
		this.context = context;
		initView(context);
	}

	public AutoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView(context);
	}

	public AutoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * 显示顶部悬浮框
	 */
	private void createPinnedHeader(int position) {

		pinnedView = (TextView) adapter.getPinnedSectionView(position);
		// Color.rgb(0, 138, 138)
		pinnedView.setBackgroundColor(Color.rgb(0, 138, 138));
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) pinnedView.getLayoutParams();
		if (layoutParams == null) {
			layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}

		int heightMode = MeasureSpec.getMode(layoutParams.height);
		int heightSize = MeasureSpec.getSize(layoutParams.height);

		if (heightMode == MeasureSpec.UNSPECIFIED)
			heightMode = MeasureSpec.EXACTLY;

		int maxHeight = getHeight() - getListPaddingTop() - getListPaddingBottom();
		if (heightSize > maxHeight)
			heightSize = maxHeight;

		int ws = MeasureSpec.makeMeasureSpec(getWidth() - getListPaddingLeft() - getListPaddingRight(),
				MeasureSpec.EXACTLY);
		int hs = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
		pinnedView.measure(ws, hs);
		pinnedView.layout(0, 0, pinnedView.getMeasuredWidth(), pinnedView.getMeasuredHeight());

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		if (adapter != null) {
			
			MyData myData;
			MyData nextData;

			if (firstVisibleItem != 0) {
				// 屏幕中可以看到的顶部第一条
				myData = (MyData) adapter.getItem(firstVisibleItem - 1);
				// 屏幕中可以看到的顶部第二条
				nextData = (MyData) adapter.getItem(firstVisibleItem);
			} else {
				myData = (MyData) adapter.getItem(firstVisibleItem);
				nextData = (MyData) adapter.getItem(firstVisibleItem + 1);
			}
			// 对比第一二条数据
			if (!myData.firstLetter.equals(nextData.firstLetter)) {
				// 不同时即出现两个悬浮框互相顶替效果,
				// 则需要动态获取y轴偏移量,让顶部悬浮框在y轴上根据偏移量显示
				// 从而最终造成一种被顶出去顶回来(顶来顶去可欢乐了)滴效果
				isFirstDing = false;
				View childView = view.getChildAt(0);
				if (childView != null) {
					mTranslateY = childView.getTop();
					// 68是通过代码测试得出的
					if (mTranslateY <= -68) {
						createPinnedHeader(firstVisibleItem); // 创建当前显示的悬浮框
						recyclePinnedView = null; // 回收上一个显示的悬浮框
					}
					System.out.println("ding ... " + mTranslateY);
				}
			} else {
				if (isFirstDing) {
					createPinnedHeader(firstVisibleItem);
					recyclePinnedView = pinnedView; // 创建完以后保存在回收悬浮框中,便于重复利用
					System.out.println("first create");
					return;
				}
				// 相同时字母悬浮栏不变,直接使用回收的悬浮框
				if (recyclePinnedView != null) {
					pinnedView = recyclePinnedView;
					mTranslateY = 0;
					System.out.println("recycle");
					return;
				} else {
					System.out.println("create");
					createPinnedHeader(firstVisibleItem);
					recyclePinnedView = pinnedView;
					return;
				}
			}
		}
	}

	// 下拉刷新监听
	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		this.onRefreshListener = onRefreshListener;
	}

	// 加载更多监听
	public void setOnLoadListener(OnLoadListener onLoadListener) {
		this.loadEnable = true;
		this.onLoadListener = onLoadListener;
	}

	public boolean isLoadEnable() {
		return loadEnable;
	}

	// 这里的开启或者关闭加载更多，并不支持动态调整
	public void setLoadEnable(boolean loadEnable) {
		this.loadEnable = loadEnable;
		this.removeFooterView(footer);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	// 初始化组件
	private void initView(Context context) {
		// 设置箭头特效
		animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(100);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(100);
		reverseAnimation.setFillAfter(true);

		inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.listview_footer, null);
		loadFull = (TextView) footer.findViewById(R.id.loadFull);
		noData = (TextView) footer.findViewById(R.id.noData);
		more = (TextView) footer.findViewById(R.id.more);
		loading = (ProgressBar) footer.findViewById(R.id.loading);

		header = inflater.inflate(R.layout.pull_to_refresh_header, null);
		arrow = (ImageView) header.findViewById(R.id.arrow);
		tip = (TextView) header.findViewById(R.id.tip);
		lastUpdate = (TextView) header.findViewById(R.id.lastUpdate);
		refreshing = (ProgressBar) header.findViewById(R.id.refreshing);

		// 为listview添加头部和尾部，并进行初始化
		headerContentInitialHeight = header.getPaddingTop();
		measureView(header);
		headerContentHeight = header.getMeasuredHeight();
		topPadding(-headerContentHeight);
		this.addHeaderView(header);
		this.addFooterView(footer);
		this.setOnScrollListener(this);
		// ====
		isPinnedHeaderShown = false;
		adapter = new ListViewAdapter(context, AutoListView.this, MyData.getData());
		this.setAdapter(adapter);
	}

	public void onRefresh() {
		if (onRefreshListener != null) {
			pinnedView.setVisibility(View.GONE);
			onRefreshListener.onRefresh();
		}
	}

	public void onLoad() {
		if (onLoadListener != null) {
			onLoadListener.onLoad();
		}
	}

	public void onRefreshComplete(String updateTime) {
		lastUpdate.setText(this.getContext().getString(R.string.lastUpdateTime, Utils.getCurrentTime()));
		state = NONE;
		refreshHeaderViewByState();
	}

	// 用于下拉刷新结束后的回调
	public void onRefreshComplete() {
		String currentTime = Utils.getCurrentTime();
		onRefreshComplete(currentTime);
	}

	// 用于加载更多结束后的回调
	public void onLoadComplete() {
		isLoading = false;
	}

	/**
	 * 核心方法 绘制顶部悬浮框
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if (pinnedView != null) {

			int pLeft = getListPaddingLeft();
			int pTop = getListPaddingTop();
			if (firstVisibleItem != 0) {
				canvas.save();
				canvas.clipRect(pLeft, pTop, pLeft + pinnedView.getWidth(), pinnedView.getHeight());
				canvas.translate(pLeft, pTop + mTranslateY);
				drawChild(canvas, pinnedView, getDrawingTime());
				canvas.restore();
			}

		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		ifNeedLoad(view, scrollState);
	}

	// 根据listview滑动的状态判断是否需要加载更多
	private void ifNeedLoad(AbsListView view, int scrollState) {
		if (!loadEnable) {
			return;
		}
		try {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && !isLoading
					&& view.getLastVisiblePosition() == view.getPositionForView(footer) && !isLoadFull) {
				onLoad();
				isLoading = true;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 监听触摸事件，解读手势
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (firstVisibleItem == 0) {
				isRecorded = true;
				startY = (int) ev.getY();

			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (state == PULL) {
				state = NONE;
				refreshHeaderViewByState();
			} else if (state == RELEASE) {
				state = REFRESHING;
				refreshHeaderViewByState();
				onRefresh();
			}
			isRecorded = false;
			break;
		case MotionEvent.ACTION_MOVE:
			whenMove(ev);
			break;
		}
		return super.onTouchEvent(ev);
	}

	// 解读手势，刷新header状态
	private void whenMove(MotionEvent ev) {
		if (!isRecorded) {
			return;
		}
		int tmpY = (int) ev.getY();
		int space = tmpY - startY;
		int topPadding = space - headerContentHeight;
		switch (state) {
		case NONE:
			if (space > 0) {
				state = PULL;
				refreshHeaderViewByState();
			}
			break;
		case PULL:
			topPadding(topPadding);
			if (scrollState == SCROLL_STATE_TOUCH_SCROLL && space > headerContentHeight + SPACE) {
				state = RELEASE;
				refreshHeaderViewByState();
			}
			break;
		case RELEASE:
			topPadding(topPadding);
			if (space > 0 && space < headerContentHeight + SPACE) {
				state = PULL;
				refreshHeaderViewByState();
			} else if (space <= 0) {
				state = NONE;
				refreshHeaderViewByState();
			}
			break;
		}

	}

	// 调整header的大小。其实调整的只是距离顶部的高度。
	private void topPadding(int topPadding) {
		header.setPadding(header.getPaddingLeft(), topPadding, header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	/**
	 * 这个方法是根据结果的大小来决定footer显示的。
	 * <p>
	 * 这里假定每次请求的条数为10。如果请求到了10条。则认为还有数据。如过结果不足10条，则认为数据已经全部加载，这时footer显示已经全部加载
	 * </p>
	 * 
	 * @param resultSize
	 */
	public void setResultSize(int resultSize) {
		if (resultSize == 0) {
			isLoadFull = true;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.VISIBLE);
		} else if (resultSize > 0 && resultSize < pageSize) {
			isLoadFull = true;
			loadFull.setVisibility(View.VISIBLE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.GONE);
		} else if (resultSize == pageSize) {
			isLoadFull = false;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
			more.setVisibility(View.VISIBLE);
			noData.setVisibility(View.GONE);
		}

	}

	// 根据当前状态，调整header
	private void refreshHeaderViewByState() {
		switch (state) {
		case NONE:
			topPadding(-headerContentHeight);
			tip.setText(R.string.pull_to_refresh);
			refreshing.setVisibility(View.GONE);
			arrow.clearAnimation();
			arrow.setImageResource(R.drawable.pull_to_refresh_arrow);
			break;
		case PULL:
			arrow.setVisibility(View.VISIBLE);
			tip.setVisibility(View.VISIBLE);
			lastUpdate.setVisibility(View.VISIBLE);
			refreshing.setVisibility(View.GONE);
			tip.setText(R.string.pull_to_refresh);
			arrow.clearAnimation();
			arrow.setAnimation(reverseAnimation);
			break;
		case RELEASE:
			arrow.setVisibility(View.VISIBLE);
			tip.setVisibility(View.VISIBLE);
			lastUpdate.setVisibility(View.VISIBLE);
			refreshing.setVisibility(View.GONE);
			tip.setText(R.string.pull_to_refresh);
			tip.setText(R.string.release_to_refresh);
			arrow.clearAnimation();
			arrow.setAnimation(animation);
			break;
		case REFRESHING:
			topPadding(headerContentInitialHeight);
			refreshing.setVisibility(View.VISIBLE);
			arrow.clearAnimation();
			arrow.setVisibility(View.GONE);
			tip.setVisibility(View.GONE);
			lastUpdate.setVisibility(View.GONE);
			break;
		}
	}

	// 用来计算header大小的。比较隐晦。因为header的初始高度就是0,貌似可以不用。
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/*
	 * 定义下拉刷新接口
	 */
	public interface OnRefreshListener {
		public void onRefresh();
	}

	/*
	 * 定义加载更多接口
	 */
	public interface OnLoadListener {
		public void onLoad();
	}

}
