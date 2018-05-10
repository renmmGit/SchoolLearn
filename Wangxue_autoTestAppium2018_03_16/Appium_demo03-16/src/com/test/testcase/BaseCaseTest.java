
package com.test.testcase;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
//import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import io.appium.java_client.AppiumDriver;

import com.dan.common.BaseTools;
import com.dan.common.SuiteListener;
import com.dan.common.Config;
import com.dan.common.Driver;
import com.dan.common.SpringConfig;
import com.dan.common.TestListener;
import com.dan.common.Element;
import com.dan.demo.BaseCase;

/**
 * BaseCaseTest作为测试用例类的父类，提供用例执行前后的处理工作
 * 每一个测试类需要继承此类
 * @author chang.lu
 *
 */

@Listeners({ TestListener.class,SuiteListener.class })
@ContextConfiguration(classes = SpringConfig.class)
public class BaseCaseTest extends AbstractTestNGSpringContextTests{

//private static final Logger LOG = LogManager.getLogger(BaseCaseTest.class);
private static Logger LOG =  Logger.getLogger(BaseCaseTest.class);
BaseTools bt = new BaseTools();
	/**
	 * 超时时间，单位：秒
	 */
	public int timeOut;

	AppiumDriver driver;
	BaseCase bcase;
	@AfterMethod(groups = {"AppDimming","SceneShare","Switch","Delay","Set","login"})
	public void afterMethod(){
		//该方法用于每个用例执行完成后，返回到主界面
		bcase.goToMain();
		LOG.info("回到主页");
	}
	
	@BeforeClass(groups = {"AppDimming","SceneShare","Switch","Delay","Set","login"})
	public void beforeClass() {	
		LOG.info("测试begin");
		Driver.autoDriver = "Appium";
		//获取toast，使用uiautomator2
		//Driver.autoDriver = "uiautomator2";
		driver = Driver.getDriver();
		bcase = new BaseCase(driver);
		timeOut = Integer.valueOf(Config.get("timeOut","2"));
		
		//Element ele = new Element();
		//LOG.info("cpu memory detected start");
		//ele.saveCpu(1000);
		//ele.saveMemory(1000, "old");
		//进入操作界面
		bcase.GointoOpePage();
		
	}

	@AfterClass(groups = {"AppDimming","SceneShare","Switch","Delay","Set","login"})
	public void afterClass() {
		driver.quit();
		//quit执行后，退出appium服务，失去session，需要重建连接
		Driver.reset();
		LOG.info("测试end");
	}
	
}
