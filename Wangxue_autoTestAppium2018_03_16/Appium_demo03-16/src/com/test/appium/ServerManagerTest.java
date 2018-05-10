package com.test.appium;


import org.springframework.test.context.ContextConfiguration;


import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.dan.appium.ServerManager;
import com.dan.common.BaseTools;
import com.dan.common.Driver;
import com.dan.common.SpringConfig;

import io.appium.java_client.AppiumDriver;

@ContextConfiguration(classes = SpringConfig.class)
public class ServerManagerTest extends AbstractTestNGSpringContextTests{
	
	@Test
	public void start() {
		ServerManager winServer = new ServerManager();
		winServer.start();
		AppiumDriver driver = Driver.getDriver();
		try {
			BaseTools.wait(500);
			throw new Exception();
		} catch (Exception e) {
			driver.quit();
			winServer.stop();
			e.printStackTrace();
		}
	}

	@Test(expectedExceptions=RuntimeException.class, expectedExceptionsMessageRegExp="Test not implemented")
	public void stop() {
		throw new RuntimeException("Test not implemented");
	}

}
