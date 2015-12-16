package com.duang.easyecard.GlobalData;

import android.app.Application;

public class UrlConstant extends Application{

	// 主页
	public final static String INDEX = "http://card.ouc.edu.cn/";
	// 登录页面, POST登录信息
	public final static String MINI_CHECK_IN = INDEX + "Account/MiniCheckIn";
	// 验证码地址
	public final static String GET_CHECKCODE_IMG = INDEX + "Account/GetCheckCodeImg/Flag=0";
}
