package com.dan.common;

import java.net.MalformedURLException;
import java.net.URL;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;



//import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities; 


//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class Driver {

//	private static final Logger LOG = LogManager.getLogger(Driver.class);
	private static Logger LOG =  Logger.getLogger(Driver.class);
	static IOSDriver iosDriver;
	static AndroidDriver androidDriver;
	public static int width ;
	public static int height ;
	public static String autoDriver = "Appium";
	
	public static void reset() {
		androidDriver = null;
		iosDriver = null;
	}

	public static AppiumDriver getDriver() {
		if (Config.ANDROID.equals(Config.get("auto.platform","Android"))) {
			if (androidDriver==null) {
				LOG.info("创建Android Driver");
				createAndroidDriver(autoDriver,Config.get("auto.udid"),Config.get("auto.port"),Config.get("auto.appPackage"),Config.get("auto.appActivity"));
			}
			return androidDriver;
		} else if (Config.IOS.equals(Config.get("auto.platform"))) {
			if (iosDriver==null) {
				LOG.info("创建IOS Driver");
				createIOSDriver();
			}
			return iosDriver;
		} else {
			if (androidDriver==null) {
				LOG.info("创建Android Driver");
				createAndroidDriver(autoDriver,Config.get("auto.udid"),Config.get("auto.port"),Config.get("auto.appPackage"),Config.get("auto.appActivity"));
			}
			return androidDriver;
		}
	}
	
	/**
	 * 未完成，不可使用
	 * @return
	 */
	private static IOSDriver createIOSDriver() {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("automationName", "XCUITest");
		capabilities.setCapability("platformName", "iOS");
		capabilities.setCapability("platformVersion", "11.2");
		capabilities.setCapability("deviceName", "iPhone 5s");		
		capabilities.setCapability("bundleId","com.xiaomi.mihome");
		capabilities.setCapability("xcodeOrgId","QFMT6985U7");
		capabilities.setCapability("xcodeSigningId","iPhone Developer");
		capabilities.setCapability("udid", "f6a6d37859c472d9067788eb394c135fe228f38f");
		
		/*capabilities.setCapability("deviceName", "iPhone 5s");  
        capabilities.setCapability("platformVersion", "11.2");  
        capabilities.setCapability("platformName", "iOS");  
        capabilities.setCapability("browserName", "safari"); 
        capabilities.setCapability("automationName", "XCUITest"); */
  
		try {
				iosDriver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"),capabilities);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

		//width = iosDriver.manage().window().getSize().width;
		//height = iosDriver.manage().window().getSize().height;
		LOG.info("IOS Driver 创建成功");
		
		return iosDriver;
	}

	private static AndroidDriver createAndroidDriver(String autoDriver, String udid, String port, String appPackage, String appActivity) {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		// 使用的自动化测试引擎：Appium(默认)或uiautomator2
		capabilities.setCapability("automationName", autoDriver);
		// 测试的手机操作系统：iOS,Android,FirefoxOS
		capabilities.setCapability("platformName", "Android");
		// driver的session超时时间，默认是60秒
		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, "600");
		// 手机操作系统版本
		capabilities.setCapability("platformVersion", "8.0.0");
		// 使用的手机类型或模拟器类型:iPhone Simulator,iPad Simulator,iPhone Retina 4-inch,android
		// Emulator,Galaxy S4等
		// 在 iOS上，这个关键字的值必须是使用 `instruments -s devices` 得到的可使用的设备名称之一。
		// 在 Android上，这个关键字目前不起作用
		capabilities.setCapability("deviceName", "android Emulator");
		// 连接的物理设备的唯一设备标识
		// 通过adb devices命令查看模拟器的udid
		//capabilities.setCapability(MobileCapabilityType.UDID,udid);
		capabilities.setCapability("udid",udid);
		// 使用的是WINDOWS平台
		capabilities.setCapability(CapabilityType.PLATFORM, "WINDOWS");
		// 测试的web浏览器，如果是测app则忽略
		//capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
		// 要启动的app包
		capabilities.setCapability("appPackage", appPackage);
		// 要启动的Activity
		capabilities.setCapability("appActivity", appActivity);
		// 支持中文输入
		capabilities.setCapability("unicodeKeyboard", "True");
		// 在设定了 unicodeKeyboard 关键字的 Unicode 测试结束后，重置输入法到原有状态。如果单独使用，将会被忽略。默认值 false
		//capabilities.setCapability("resetKeyboard", "True"); 
		// 安装时不对apk进行重签名，设置很有必要，否则有的apk在重签名后无法正常使用
		capabilities.setCapability("noSign", "True");
		//不要在会话前重置应用状态。默认值false。
		capabilities.setCapability("noReset", "True");
		try {
			androidDriver = new AndroidDriver(new URL("http://127.0.0.1:" + port + "/wd/hub"), capabilities);
			width = androidDriver.manage().window().getSize().width;
			height = androidDriver.manage().window().getSize().height;
		} catch (MalformedURLException e) {
			LOG.error("连接Appium Server出错...",e);
		}
		LOG.info("Android Driver 创建成功");
		return androidDriver;
	}
	
	public static AndroidDriver createAndroidDriver(DesiredCapabilities caps) {
		try {
			androidDriver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
			width = androidDriver.manage().window().getSize().width;
			height = androidDriver.manage().window().getSize().height;
		} catch (MalformedURLException e) {
			LOG.error("连接Appium Server出错...",e);
		}
		return androidDriver;
	}
	
}

