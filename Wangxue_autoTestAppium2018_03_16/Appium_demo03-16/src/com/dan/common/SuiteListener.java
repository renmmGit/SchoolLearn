package com.dan.common;

//import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.dan.appium.ServerManager;
import com.dan.common.BaseTools;
import com.dan.common.Element;

/**
 * TestNG 监听类扩展
 * 用于测试前启动appium
 * 
 * @author chang.lu
 *
 */
public class SuiteListener implements ISuiteListener{

//	private static final Logger LOG = LogManager.getLogger(SuiteListener.class);
	private static Logger LOG =  Logger.getLogger(SuiteListener.class);
	ServerManager server;

	public void onFinish(ISuite arg0) {
		LOG.info("测试Suite结束，关闭appium server");
		if (ServerManager.run) {
			server.stop();
		}
	}

	public void onStart(ISuite arg0) {
		LOG.info("测试Suite开始，开启appium server");
		server = new ServerManager();
		BaseTools.wait(2000);
		if (ServerManager.run) {
			server.start();
			BaseTools.wait(2000);
		}

	}
}
