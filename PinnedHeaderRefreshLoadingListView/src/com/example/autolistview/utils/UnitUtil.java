package com.example.autolistview.utils;
import java.text.DecimalFormat;

import android.content.Context;

public class UnitUtil {  

	/**
	 * 保留2位小数
	 * @param a
	 * @return
	 */
	public static double get2Double(double a){
		DecimalFormat df=new DecimalFormat("0.00");
		return new Double(df.format(a).toString());
	}
	/**
	 * 米转换为千米
	 * @param distance
	 * @return
	 */
	public static String getDistance(double distance){
		String temp = "0.00米";
		double dis;
		if(distance/1000 >=1){
			int tem = (int) distance;
			dis = Double.parseDouble(tem/1000+"."+tem%1000);
			temp = get2Double(dis)+"千米";
		}else{
			temp = distance+"米";
		}
		return temp;
	}
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}  