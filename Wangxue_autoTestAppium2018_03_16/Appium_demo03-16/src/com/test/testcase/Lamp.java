package com.test.testcase;

import org.testng.annotations.Test;

import com.dan.common.BaseTools;
import com.dan.common.Driver;

public class Lamp extends BaseCaseTest{
	@Test
	public void errorShowup(){
			
		
//		while(true){
			while(bcase.isExist(1, "已关闭")){
				bcase.click("开关");
			}
				
			while(bcase.isSelected("护眼模式"))
			{
				bcase.click("护眼模式");
			}
			
		    for(int i=0; i <=30; i++){	
		    	int b;
		    	bcase.tapByXY((int)(Math.random()*(Driver.width)), (b=(int)(Math.random()*(Driver.height)))<300||b>1400?900:b,0);
		    	BaseTools.wait(1000);
		    	bcase.tapByXY((int)(Math.random()*(Driver.width)), (b=(int)(Math.random()*(Driver.height)))<300||b>1400?900:b,0);
		    	BaseTools.wait(1000);
		    	bcase.tapByXY((int)(Math.random()*(Driver.width)), (b=(int)(Math.random()*(Driver.height)))<300||b>1400?900:b,0);		    	
		    	BaseTools.wait(1000);
		    	bcase.drag(Driver.width/2, Driver.height/3, Driver.width/2, Driver.height/3*2, 1000);
				bcase.drag(Driver.width/2, Driver.height/3*2, Driver.width/2, Driver.height/3, 1000);
		    }
			
			
			bcase.click("护眼模式");
			bcase.click("儿童看书");
			bcase.click("成人看书");
			bcase.click("手机电脑");
			
			bcase.click("延时关灯");
			bcase.click("1分钟后关闭");
			bcase.click("延时关灯");
			bcase.click("取消关闭");
		}
		
				
//	}

}
