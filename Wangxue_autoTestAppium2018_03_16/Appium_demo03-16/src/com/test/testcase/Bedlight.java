package com.test.testcase;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.dan.common.BaseTools;

public class Bedlight extends BaseCaseTest{

	private static Logger LOG = Logger.getLogger(Bedlight.class);
	
	@Test(groups = "login")
	public void a(){
		//BaseTools.wait(1000*5);
		//bcase.tapByXY(100, 100, 1);
		while(!bcase.isExist(5, "下拉控制开关灯")) {
			bcase.click("床头灯1");
		}
		while(bcase.isExist(5, "下拉控制开关灯")) {
			bcase.tapByXY(500, 400, 1);
		}
		//初始登录界面校验
		if(!bcase.isExist(3, "开关")){
			LOG.fatal("初始登录界面不为开关/调色板界面");
		}else if(!bcase.isExist(3, "调色板")){
			LOG.fatal("初始登录界面不为开关/调色板界面");
		}else if(!bcase.isExist(3, "colorIcon")){
			LOG.fatal("初始登录界面不为开关/调色板界面");
		}
		//下拉开关灯功能校验
		bcase.swipeToDown(1);
		if(!bcase.isExist(3, "已关闭")){
			LOG.fatal("下拉关灯功能不可用");
		}
		bcase.swipeToDown(1);
		if(!bcase.isExist(3, "亮度")){
			LOG.fatal("下拉开灯功能不可用");
		}
		//为点击上下左右做准备
		int centerx = bcase.getLocation("colorIcon", 0).x;
		int centery = bcase.getLocation("colorIcon", 0).y;		
		int width = bcase.getSize("colorIcon", 0).width;
		int height = bcase.getSize("colorIcon", 0).height;
		
		bcase.tapByXY(centerx, centery - height/2+20, 1);//上
		bcase.tapByXY(centerx, centery + height/2-20, 1);//下
		bcase.tapByXY(centerx-width/2+20, centery, 1);//左		
		bcase.tapByXY(centerx+width/2-20, centery, 1);//右
		
		bcase.click("亮度");
		String[] brights={"亮度1%","亮度50%","亮度100%"};
		int centerx2 = bcase.getLocation("temIcon", 0).x;
		int centery2 = bcase.getLocation("temIcon", 0).y;		
		int width2 = bcase.getSize("temIcon", 0).width;
		int height2 = bcase.getSize("temIcon", 0).height;
		int delt_width2=(int)(width2-width/1.4);
		
		bcase.tapByXY(centerx2-width2/2+delt_width2-30, centery2 + height2/2-delt_width2+60, 1);//0%
		BaseTools.wait(6000);
		if(!bcase.getText("获取亮度色温").contains(brights[0])){
			LOG.fatal("亮度不能调为1%");
		}
		bcase.tapByXY(centerx2, centery2 - height2/2+20, 1);//50%
		BaseTools.wait(6000);
		if(!bcase.getText("获取亮度色温").contains(brights[0])){
			LOG.fatal("亮度不能调为50%");
		}
		bcase.tapByXY(centerx2+width2/2-delt_width2+30, centery2 + height2/2-delt_width2+60, 1);//100%
		BaseTools.wait(6000);
		if(!bcase.getText("获取亮度色温").contains(brights[0])){
			LOG.fatal("亮度不能调为100%");
		}
		bcase.click("色温");
		int centerx3 = bcase.getLocation("temIcon", 0).x;
		int centery3 = bcase.getLocation("temIcon", 0).y;		
		int width3 = bcase.getSize("temIcon", 0).width;
		int height3 = bcase.getSize("temIcon", 0).height;
		int delt_width3=(int)(width2-width/1.4);
		
		bcase.tapByXY(centerx3-width3/2+delt_width3-30, centery3 + height3/2-delt_width3+60, 1);//0%	
		bcase.tapByXY(centerx3, centery3 - height3/2+20, 1);//50%
		bcase.tapByXY(centerx3+width3/2-delt_width3+30, centery3 + height3/2-delt_width3+60, 1);//100%	
	}
	@Test(groups = "login")
	public void b(){
		while(!bcase.isExist(3, "下拉控制开关灯")) {
			bcase.click("床头灯1");
		}
		while(bcase.isExist(3, "下拉控制开关灯")) {
			bcase.tapByXY(500, 400, 1);
		}
		bcase.click("收藏");
		for(int i = 0; i < 6; i++){
		bcase.inputText("请输入", "#"+i);
		bcase.click("收藏");
		}		
		bcase.click("场景");
	}
	
	@Test(groups = "login1")
	public void c(){
		while(!bcase.isExist(3, "下拉控制开关灯")) {
			bcase.click("床头灯1");
		}
		while(bcase.isExist(3, "下拉控制开关灯")) {
			bcase.tapByXY(500, 400, 1);
		}
		bcase.click("助眠");
		bcase.click("设置助眠倒计时");
		int centerx = bcase.getLocation("获取时间按钮", 1).x;
		int centery = bcase.getLocation("获取时间按钮", 1).y;
		int height = bcase.getSize("获取时间按钮", 1).height;
		int width = bcase.getSize("获取时间按钮", 1).width;
		//设置助眠倒计时
		bcase.tapByXY((int)(centerx-width/4), centery + height, 1);
		bcase.click("确定");
		bcase.swipeToDown(1);//开启助眠倒计时
		if(!bcase.isExist(3, "灯光将在20分钟后结束")){
			LOG.fatal("助眠倒计时功能不能同步显示在助眠界面");
		}
		bcase.click("设置助眠倒计时");
		bcase.tapByXY((int)(centerx-width/4), centery + height, 1);
		bcase.click("确定");
		if(bcase.isExist(3, "灯光将在25分钟后结束")){
			LOG.fatal("不关闭助眠功能，仍可以改变助眠定时");
		}
		
		bcase.swipeToDown(1);//关闭助眠倒计时
		bcase.click("设置助眠倒计时");
		bcase.tapByXY((int)(centerx-width/4), centery + height, 1);
		bcase.click("确定");
		bcase.swipeToDown(1);//开启助眠倒计时
		if(!bcase.isExist(3, "灯光将在30分钟后结束")){
			LOG.fatal("助眠倒计时设置不能改变");
		}
		
		
	}
}
