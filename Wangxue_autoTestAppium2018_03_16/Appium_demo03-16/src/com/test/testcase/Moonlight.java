package com.test.testcase;


import static org.testng.Assert.assertTrue;

//import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;
import org.seleniumhq.jetty7.util.log.Log;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.dan.common.Driver;
import com.dan.common.SpringConfig;


public class Moonlight extends BaseCaseTest{
	private static Logger LOG =  Logger.getLogger(Moonlight.class);
	@Test
	public void test(){
		bcase.swipeToDown(3);
		bcase.swipeToUp(2);
//		assertTrue(bcase.isContainTxt("Hello"),"判断是否存在Hello的字符");
	}
	@Test
	public void swithch(){
		bcase.click("开关");
		bcase.swipeToDown(1);
	}
	@Test
	public void universalSet(){
		bcase.click("通用设置");
		bcase.click("重命名");
		bcase.click("名称");
		bcase.inputText("名称", "智睿床头灯");
		bcase.click("确定");
		bcase.click("通用设置返回");
	}
	
	
}
