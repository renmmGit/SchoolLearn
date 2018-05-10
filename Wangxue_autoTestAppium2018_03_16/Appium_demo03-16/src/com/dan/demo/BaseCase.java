package com.dan.demo;


//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;


import com.dan.common.BaseTools;
import com.dan.common.Config;
import com.dan.common.Driver;
import com.dan.common.Element;
import io.appium.java_client.AppiumDriver;
 

public class BaseCase extends Element{
	AppiumDriver driver ;

//	private static final Logger LOG = LogManager.getLogger(BaseCase.class);
	private static Logger LOG =  Logger.getLogger(BaseCase.class);
	/**
	 * 超时时间，单位：秒
	 */
	public int timeOut = Integer.valueOf(Config.get("timeOut","2"));
	
	public BaseCase() {
		this.driver = Driver.getDriver();
	}
	
	public BaseCase(AppiumDriver d) {
		this.driver = d;
	}
	
	/**
	 * 进入操作页面
	 */
	public void GointoOpePage() {
		if (isExist(timeOut, "主界面")) {
			click("主界面");
//			tapByXY(getLocation(get("主界面")).x,getLocation(get("主界面")).y+getSize(get("主界面")).height);
		}
	}
	
	/**
	 * 滑动欢迎页面
	 */
	public void swipeHelloPage() {
		if (isExist(timeOut, "延时关灯")) {
			swipeToLeft(2);			
		}
		if (isExist(timeOut, "立即体验")) {
//			get("立即体验").click();
			click("立即体验");
		}
		if (isExist(timeOut, "登录")) {
//			get("登录").click();
			click("登录");
		}
		
		
	}
	
	/**
	/**
	 * 返回主界面操作
	 */
	public void goToMain() {
		if(!isExist(1, "主页")) {
//			get("返回").click();
			click("返回");
		}
	}
}
