package com.test.testcase;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class UniversalSet extends BaseCaseTest {

	private static Logger LOG = Logger.getLogger(UniversalSet.class);
	@Test(groups = "Set")
	public void Set1(){
		bcase.click("蜡烛灯水晶版");
		bcase.click("更多");
		bcase.click("通用设置");
		bcase.click("重命名");
		bcase.click("输入名称");
		bcase.inputText("输入名称", "新年好呀");
		bcase.click("确定");
		bcase.click("次返回");
		bcase.click("返回");
		if(!bcase.isExist(10, "新年好呀")){
			LOG.fatal("设备重命名没有同步更新到主页！！！");
		}
		
		bcase.click("新年好呀");
		bcase.click("更多");
		bcase.click("通用设置");
		bcase.click("重命名");
		bcase.click("输入名称");
		bcase.inputText("输入名称", "蜡烛灯水晶版");
		bcase.click("取消");
		bcase.click("次返回");
		bcase.click("返回");
		if(bcase.isExist(10, "蜡烛灯水晶版")){
			LOG.fatal("设备重命名没有同步更新到主页！！！");
		}
		bcase.click("新年好呀");
		bcase.click("更多");
		bcase.click("通用设置");
		bcase.click("重命名");
		bcase.click("输入名称");
		bcase.inputText("输入名称", "蜡烛灯水晶版");
		bcase.click("确定");
		bcase.click("次返回");
	}
	
	@Test(groups = "Set")
	public void Set2(){
		bcase.click("蜡烛灯水晶版");
		bcase.click("更多");
		bcase.click("通用设置");
		bcase.click("删除设备");
		bcase.click("取消");
		bcase.click("次返回");
		bcase.click("返回");
		if(!bcase.isExist(10, "蜡烛灯水晶版")){
			LOG.fatal("删除设备功能存在问题！！！");
		}
		bcase.click("蜡烛灯水晶版");
		bcase.click("更多");
		bcase.click("通用设置");
		bcase.click("删除设备");
		bcase.click("确定");
		if(bcase.isExist(10, "蜡烛灯水晶版")){
			LOG.fatal("删除设备功能存在问题！！！");
		}
	}
	
}
