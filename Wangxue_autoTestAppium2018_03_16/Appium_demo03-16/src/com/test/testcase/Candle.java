package com.test.testcase;


import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

//import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;
import org.seleniumhq.jetty7.util.log.Log;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.dan.common.BaseTools;
import com.dan.common.Driver;
import com.dan.common.SpringConfig;


public class Candle extends BaseCaseTest{
	private static Logger LOG =  Logger.getLogger(Candle.class);
	//APP开关灯
	@Test(groups = "try")
	public void a1(){
		bcase.swipeToUp(1);
	    
	}
	@Test(groups = "AppDimming",alwaysRun = true)
	public void dimming1(){
		LOG.info("the fisrt case start");
		bcase.click("智睿筒灯ß");
		if(!bcase.isExist(10, "已关闭")){
			bcase.click("dimmingIcon");//ensure the light is closed
		}
		if(bcase.isExist(10, "收藏")){
			LOG.fatal("关灯情况下，收藏按钮仍存在！！！");
		}
		bcase.click("延时关");
		int startx = bcase.getLocation("设置延时时间",0).x;
		int starty = bcase.getLocation("设置延时时间",0).y-(int)bcase.getSize("设置延时时间",0).height/2;
		
		bcase.drag(startx, starty, startx+20, starty+20, 1000);//dur,每一步用5ms
		if(!bcase.isExist(10, "滑动上部按钮调节时间")){
			LOG.fatal("关灯情况下，延时功能仍然可用！！！");
		}
		LOG.info("the first case end");
	}
	@Test(groups = {"AppDimming"},alwaysRun = true)
	public void dimming2(){
		
		bcase.click("智睿筒灯");
		String[] para={"色温100%","色温1%","亮度100%","亮度1%","明亮"};		
		int tempx = bcase.getLocation("dimmingIcon",0).x;
		int tempy = bcase.getLocation("dimmingIcon",0).y;
		int randy = (int)(Driver.height);
		int randx = (int)(Driver.width);
		bcase.drag(tempx, tempy, tempx, 100, 1000);	
		LOG.info(bcase.getText("获取亮度色温"));
		if(!(bcase.getText("获取亮度色温").contains(para[2]) || bcase.getText("获取亮度色温").contains(para[4]))){
			LOG.fatal("亮度不能调到最大值！！！");
		}
		
		bcase.drag(tempx, tempy, tempx, randy-100, 1000);	
		LOG.info(bcase.getText("获取亮度色温"));
		if(!(bcase.getText("获取亮度色温").contains(para[3]) || bcase.getText("获取亮度色温").contains(para[4]))){
			LOG.fatal("亮度不能调到最小值！！！");		
		}
		bcase.drag(tempx, tempy, randx-100, tempy, 1000);		
		LOG.info(bcase.getText("获取亮度色温"));
		if(!(bcase.getText("获取亮度色温").contains(para[0]) || bcase.getText("获取亮度色温").contains(para[4]))){
			LOG.fatal("色温不能调到最大值！！！");
		}
		bcase.drag(tempx, tempy, 100, tempy, 1000);	
		LOG.info(bcase.getText("获取亮度色温"));
		if(!(bcase.getText("获取亮度色温").contains(para[1]) || bcase.getText("获取亮度色温").contains(para[4]))){
			LOG.fatal("色温不能调到最小值！！！");		
		}		
	}
	@Test(groups = {"AppDimming"},alwaysRun = true)
	public void dimming3() {				
		bcase.click("智睿筒灯");
		//色温
		int tempx = bcase.getLocation("dimmingIcon",0).x;
		int tempy = bcase.getLocation("dimmingIcon",0).y;
		//同时调节
		for (int i = 0; i < 10; i++){
			int randx = (int)(Math.random() * Driver.width); 
			int randy = (int)(Math.random() * Driver.height); 
			bcase.drag(tempx, tempy, randx, randy, 1000);
			if (tempx <= randx) tempx = randx - 15;
			else tempx = randx + 15;
			if (tempy <= randy) tempy = randy - 15;
			else tempy = randy + 15;
			
		}		
	}
	
	@Test(groups = {"AppDimming"},alwaysRun = true)
	public void dimming4(){
		String[] scene = {"明亮", "电视","温馨", "起夜"};
		bcase.click("智睿筒灯");
		bcase.click("情景");
		for(int i= 0; i < 10 ; i++){
			bcase.click(scene[(i%4)]);
			LOG.info(scene[(i%4)]);
			bcase.click("调光");
			if(!bcase.getText("获取亮度色温").contains(scene[(i%4)])){
				LOG.fatal("调光界面与情景界面不同步！！！");
			}
			bcase.click("情景");
		}				
	}
	
	@Test(groups = "SceneShare",alwaysRun = true)
	public void share1(){
		bcase.click("智睿筒灯");	
		int count = 0;
		int tempx = bcase.getLocation("dimmingIcon",0).x;
		int tempy = bcase.getLocation("dimmingIcon",0).y;
		for(int i = 0; i < 10; i++){
			bcase.click("收藏");
			while(bcase.isExist(10, "请输入名称") == false && count < 6){
				int randx = (int)(Math.random() * Driver.width);
				int randy = (int)(Math.random() * Driver.height); 
				bcase.drag(tempx, tempy, randx, randy, 1000);			
				bcase.click("收藏");
			}
			if(i <= 4){
				bcase.inputText("请输入", "#"+i);
				count++;
				bcase.click("确定");
			}
			if(i == 5){
				bcase.inputText("请输入", "*********************************************************");
				count++;
				bcase.click("确定");		
			}			
		}
	}
	
	@Test(groups="SceneShare",alwaysRun = true)
	public void share2(){
		List<String> texts = null;
		bcase.click("智睿筒灯");
		bcase.click("情景");
		bcase.swipeToUp(1);	//保证六个收藏场景都在手机视野里
		texts = bcase.getTexts("获取情景界面亮度色温值");
		LOG.info(texts);
		for(int i = 0; i < 5; i++){
			bcase.click("#"+i);
			bcase.click("调光");
			if(!bcase.isContainTxt(texts.get(i))){
				LOG.fatal("新添情景亮度色温值没有同步到调光界面！！！");
			}
			bcase.click("情景");
		}
		
	}
	@Test(groups="SceneShare",alwaysRun = true)
	public void share3(){
		bcase.click("智睿筒灯");
		bcase.click("情景");
		bcase.swipeToUp(1);	//保证六个收藏场景都在手机视野里
		for(int i = 0; i < 5; i++){
			bcase.longclickByElement("#"+i, 2);
			if(bcase.isExist(10, "删除")){
				bcase.click("删除");
			}
			else
				LOG.fatal("不存在删除按钮，无法删除收藏情景！！！");
			
		}
		bcase.longclickByElement("************************", 2);//因为手机界面最多只能显示24个字符
		bcase.click("删除");
		
	}
	
	@Test(groups = "Switch",alwaysRun = true)
	public void switch1(){
		bcase.click("智睿筒灯");
		if(!bcase.isExist(10, "已关闭")){
			bcase.click("dimmingIcon");//ensure the light is closed
			LOG.info("等灯关闭");
		}
		bcase.click("更多");
		bcase.click("定时开关");
		
		if(bcase.isExist(10, "添加定时"))
			bcase.click("添加定时");
		else if(bcase.isExist(10, "立即添加")){
			bcase.click("立即添加");}
		    else			
			LOG.info("没有找到“添加定时”按钮！！！");
		
		
		int tempx = bcase.getLocation("获取时间按钮",1).x;
		int tempy = bcase.getLocation("获取时间按钮",1).y;
		int randy = bcase.getSize("获取时间按钮",1).height;
		bcase.click("开启时间");		
		bcase.tapByXY(tempx,tempy+randy,1);
		bcase.click("关闭时间");
		bcase.tapByXY(tempx,tempy+randy,1);
		bcase.tapByXY(tempx,tempy+randy,1);
		bcase.click("确定");
		while(bcase.isExist(3, "更新中...")){
			if(bcase.isExist(0, "取消"))
			bcase.click("取消");
			BaseTools.wait(1000*2);
			if(bcase.isExist(0, "确定"))
				bcase.click("确定");
		}
		bcase.click("次返回");
        BaseTools.wait(1000*120);//延时两分钟
        bcase.click("更多");
        bcase.click("定时开关");
        if(bcase.isSelected("check按钮")){
        	LOG.fatal("定时记录未同步！！！");
        }
        bcase.click("次返回");
	}
	
	@Test(groups = "Switch",alwaysRun = true)
	public void switch2(){
		bcase.click("智睿筒灯");
		if(!bcase.isExist(10, "已关闭")){
			bcase.click("dimmingIcon");//ensure the light is closed
			LOG.info("等灯关闭");
		}
		bcase.click("更多");
		bcase.click("定时开关");
		
		if(bcase.isExist(10, "添加定时"))
			bcase.click("添加定时");
		else if(bcase.isExist(10, "立即添加")){
			bcase.click("立即添加");}
		    else			
			LOG.info("没有找到“添加定时”按钮！！！");
		
		int tempx = bcase.getLocation("获取时间按钮",1).x;
		int tempy = bcase.getLocation("获取时间按钮",1).y;
		int randy = bcase.getSize("获取时间按钮",1).height;
		bcase.tapByXY(tempx,tempy+randy,1);
		bcase.click("确定");
		while(bcase.isExist(3, "更新中...")){
			if(bcase.isExist(0, "取消"))
			bcase.click("取消");
			BaseTools.wait(1000*2);
			if(bcase.isExist(0, "确定"))
				bcase.click("确定");
		}
		bcase.click("次返回");
        BaseTools.wait(1000*60);//延时一分钟
        if(bcase.isExist(10, "已关闭")){
			LOG.fatal("定时功能没起作用！！！");
		}
	}
	@Test(groups = "Switch",alwaysRun = true)
	public void switch3(){
		bcase.click("智睿筒灯");
		if(!bcase.isExist(10, "已关闭")){
			bcase.click("dimmingIcon");//ensure the light is closed
			LOG.info("等灯关闭");
		}
		bcase.click("更多");
		bcase.click("定时开关");
		
		if(bcase.isExist(10, "添加定时"))
			bcase.click("添加定时");
		else if(bcase.isExist(10, "立即添加")){
			bcase.click("立即添加");}
		    else			
			LOG.info("没有找到“添加定时”按钮！！！");
		
		int tempx = bcase.getLocation("获取时间按钮",1).x;
		int tempy = bcase.getLocation("获取时间按钮",1).y;
		int randy = bcase.getSize("获取时间按钮",1).height;
		bcase.tapByXY(tempx,tempy+randy,1);

		bcase.click("重复");
		bcase.click("每天");
		bcase.click("确定");
		while(bcase.isExist(3, "更新中...")){
			if(bcase.isExist(0, "取消"))
			bcase.click("取消");
			BaseTools.wait(1000*2);
			if(bcase.isExist(0, "确定"))
				bcase.click("确定");
		}
		if(bcase.getList("定时记录数").size()!=3)
			LOG.fatal("不能同步新增定时记录！！！");
		bcase.click("次返回");
	}
	
	@Test(groups = "Switch",alwaysRun = true)
	public void switch4(){
		bcase.click("智睿筒灯");
		bcase.click("更多");
		bcase.click("定时开关");
		int times = bcase.getList("定时记录数").size();
		LOG.info(String.valueOf(times));
		while(times > 0){
			bcase.longclickByElement("定时记录数", 2);
			if(bcase.isExist(5,"删除")){
				bcase.click("删除");
				while(bcase.isExist(3, "更新中...")){
					BaseTools.wait(1000*2);}
				times = bcase.getList("定时记录数").size();
				LOG.info(String.valueOf(times));
			}		
			else
				LOG.fatal("删除按钮不存在，无法删除定时记录！！！");	
		}
		
		
		bcase.click("次返回");
	}
	
	@Test(groups = "Delay",alwaysRun = true)
	public void delay1(){
		bcase.click("智睿筒灯");
		if(bcase.isExist(10, "已关闭")){
			bcase.click("dimmingIcon");//ensure the light is open
		}
		BaseTools.wait(1000*3);
		bcase.click("延时关");
		
		int tempx = bcase.getLocation("设置延时时间",0).x;
		int tempy = bcase.getLocation("设置延时时间",0).y;
		//int randx = (int)(bcase.getSize("设置延时时间", 0).width);
		int randy = (int)(bcase.getSize("设置延时时间", 0).height);
		int starty=(int)(tempy-(randy/2));
		
		bcase.drag(tempx,starty+50,tempx+40,starty+50,1000);//+50是为了更好的定位拖动按钮,延时1min
		
		BaseTools.wait(1000*70);//延时70s
		
		bcase.click("调光");

		if(!bcase.isExist(10, "已关闭")){
			LOG.fatal("延时关功能不起作用！！！");
		}
	}
	
	@Test(groups = "Delay",alwaysRun = true)
	public void delay2(){
		bcase.click("智睿筒灯");
		if(bcase.isExist(10, "已关闭")){
			bcase.click("dimmingIcon");//ensure the light is open
		}
		BaseTools.wait(1000*3);
		bcase.click("延时关");
		
		int tempx = bcase.getLocation("设置延时时间",0).x;
		int tempy = bcase.getLocation("设置延时时间",0).y;
		//int randx = (int)(bcase.getSize("设置延时时间", 0).width);
		int randy = (int)(bcase.getSize("设置延时时间", 0).height);
		int starty=(int)(tempy-(randy/2));
		
		bcase.drag(tempx,starty+50,tempx+40,starty+50,1000);
		bcase.click("调光");
		bcase.click("dimmingIcon");
		bcase.click("延时关");
		if(!bcase.isExist(10, "滑动上部按钮调节时间")){
			LOG.fatal("延时关功能不能同步其他操作！！！");
		}
		
	}
	
	@Test(groups = "Delay",alwaysRun = true)
	public void delay3(){
		bcase.click("智睿筒灯");
		if(bcase.isExist(10, "已关闭")){
			bcase.click("dimmingIcon");//ensure the light is open
		}
		BaseTools.wait(1000*3);
		bcase.click("延时关");
		
		int tempx = bcase.getLocation("设置延时时间",0).x;
		int tempy = bcase.getLocation("设置延时时间",0).y;
		//int randx = (int)(bcase.getSize("设置延时时间", 0).width);
		int randy = (int)(bcase.getSize("设置延时时间", 0).height);
		int starty=(int)(tempy-(randy/2));
		
		bcase.drag(tempx,starty+50,tempx+100,starty+70,1000);//延时两分半钟
		BaseTools.wait(1000*20);//等待20s
		bcase.drag(tempx+90, starty+70, tempx+30, starty+50, 1000);//变为延时30s
		BaseTools.wait(1000*60);
		bcase.click("调光");
		if(!bcase.isExist(10, "已关闭")){
			LOG.fatal("延时关功不能更改上一次设定的延时时间！！！");
		}	
	}
}
