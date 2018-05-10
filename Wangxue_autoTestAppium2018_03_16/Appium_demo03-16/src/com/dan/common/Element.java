package com.dan.common;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;

//import org.slf4j.LoggerFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;

/**
 * 读取配置文件获取元素查找方式及元素定位属性
 * 
 * @author 
 *
 */
public class Element {

//	private static final Logger LOG = LogManager.getLogger(Element.class);
	private static Logger LOG =  Logger.getLogger(Element.class);
	AppiumDriver driver= Driver.getDriver();
	private TouchAction action = new TouchAction(driver);


	
	/** 定位路径	 */
	String address;

	/** 定位方式	：xpath, linkText, id, name, className, desc */
	ByType type;
	
	public enum ByType {
		xpath, linkText, id, name, className, desc
	};
	

	public Element() {
		driver = Driver.getDriver();
		action = new TouchAction(driver);
	}

	/**
	 * 根据配置文件获取元素查找方式及元素定位属性
	 * 若获取不到配置，则通过txt直接查找
	 * @param ename
	 * @return
	 */
	public MobileElement get(String ename) {
		LOG.info("操作元素："+ename);
		MobileElement me = null;
		String list = ElementConfig.get(ename);
		if (list != null) {
			String[] lists = list.split("\\|\\|");
			if (lists.length == 2) {
				type = getByType(lists[0]);
				address = lists[1];
				me = find(whichBy());
			} else {
				LOG.error("[key=" + ename + "]的配置不正确，请检查");
			}
		} else {
			LOG.error("根据元素配置表未获取到[key=" + ename + "]的元素,根据名称查找元素");
			me = findByName(ename);
		}
		return me;
	}
	
	/**
	 * 通过xpath方式获取包含name的控件
	 * @param name
	 * @return 
	 */
	private MobileElement findByName(String name) {
		MobileElement e = find(By.xpath("//android.widget.TextView[contains(@text,'"+name+"')]"));
		return e;
	}
	
	/**
	 * 根据配置获取元素查找方式及元素定位属性
	 * 
	 * @param ename
	 * @return
	 */
	public List<MobileElement> getList(String ename) {
		List<MobileElement> elist = null;
		String list = ElementConfig.get(ename);
		if (list != null) {
			String[] lists = list.split("\\|\\|");
			if (lists.length == 2) {
				type = getByType(lists[0]);
				address = lists[1];
				elist = findList(whichBy());
			} else {
				LOG.error("[key=" + ename + "]的配置不正确，请检查");
			}
		} else {
			LOG.error("根据元素配置表未获取到[key=" + ename + "]的元素,根据名称查找元素");
		}

		return elist;
	}

	/**
	 * 查找元素列表，并返回
	 * 
	 * @return List<MobileElement>
	 */
	private List<MobileElement> findList(By whichBy) {
		List<? super MobileElement> list = driver.findElements(whichBy);
		return (List<MobileElement>) list;
	}

	/**
	 * 查找元素，并返回
	 * 
	 * @return MoblieElement
	 */
	private MobileElement find(By by) {
		MobileElement e = (MobileElement) driver.findElement(by);
		return e;
	}

	private By whichBy() {
		By by;
		switch (type) {
		case xpath:
			by = By.xpath(address);
			break;
		case id:
			by = By.id(address);
			break;
		case name:
			by = By.name(address);
			break;
		case className:
			by = By.className(address);
			break;
		case linkText:
			by = By.linkText(address);
			break;
		default:
			by = By.id(address);
		}
		return by;
	}


	/**
	 * 在配置时间内判断元素是否存在
	 * 
	 * @param timeOut  单位：秒
	 * @param ename  element.properties文件中配置名称
	 * @return
	 */
	public boolean isExist(int timeOut,String ename) {
		LOG.debug("判断界面是否存在元素："+ename);
		boolean flag = false;
		By by = null;
		String list = ElementConfig.get(ename);
		if (list != null) {
			String[] lists = list.split("\\|\\|");
			if (lists.length == 2) {
				type = getByType(lists[0]);
				address = lists[1];
				by = whichBy();
				flag = waitForLoad(timeOut, by, ename);
			} else {
				LOG.error("[key=" + ename + "]的配置不正确，请检查");
			}
		} else {
			LOG.error("根据元素配置表未获取到[key=" + ename + "]的元素,根据名称查找元素");
			flag = waitForLoad(timeOut, By.xpath("//*[contains(@text,'"+ename+"')]"), ename);
		}
		if (flag) {
			LOG.info("Successful:界面存在元素["+ename+"]");
		}
		return flag;
	}

	private boolean waitForLoad(int timeOut, final By by, String name) {
		boolean flag = false;
		try {
			flag = (new WebDriverWait(driver, timeOut)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					MobileElement element = (MobileElement) driver.findElement(by);
					return true;
				}
			});
		} catch (TimeoutException e) {
			LOG.error("!!超时!! " + timeOut + " 秒之后还没找到元素 [" + name + "]");
		}
		return flag;
	}

	public boolean isSelected(String name){
		LOG.info("判断界面是否选中图标："+name);
		MobileElement me = get(name);		
	    return me.isSelected();
	}
	/**
	 * 判断当前页面中是否存在查找的字符
	 * 
	 * @param 需要查找的字符
	 * @return true/false
	 */
	
	public boolean isContainTxt(String txt) {
		LOG.info("判断界面是否存在字符："+txt);
		MobileElement text = null;
		MobileElement button = null;
		try {
			text = (MobileElement) driver.findElement(By.xpath("//android.widget.TextView[contains(@text,'"+txt+"')]"));
		} catch (Exception e) {
			LOG.error("没有找到存在"+txt+"的TextView");
			try {
				button = (MobileElement) driver.findElement(By.xpath("//android.widget.Button[contains(@text,'"+txt+"')]"));
			} catch (Exception e1) {
				LOG.error("没有找到存在"+txt+"的button");
			}
		}
		
		if (text!=null||button!=null) {
			LOG.info("Successful:界面存在字符["+txt+"]");
			return true;
		}
		return false;
	}
	
	/**
	 * 弹出窗口处理，直接根据名称定位获取元素，并点击
	 */
	public void alert(String txt) {
		try {
			driver.findElement(By.name(txt)).click();
		} catch (Exception e2) {
			try {
				driver.findElement(By.xpath("//android.widget.Button[contains(@text,'"+txt+"')]")).click();
			} catch (Exception e) {
				LOG.error("没有找到存在"+txt+"的button");
				try {
					driver.findElement(By.xpath("//android.widget.TextView[contains(@text,'"+txt+"')]")).click();
				} catch (Exception e1) {
					LOG.error("没有找到存在"+txt+"的TextView");
				}
			}
		}
	}

	/**
	 * 向左滑动n次
	 */
	public void swipeToLeft(int n) {
		int x = Driver.width;
		int y = Driver.height;
		for (int i = 0; i < n; i++) {
			try {
				driver.swipe((x / 8 * 7), (y / 2 * 1), (x / 8 * 1), (y / 2 * 1), 1000);
			} catch (Exception e) {
				LOG.error("第"+(i+1)+"次滑动失败",e);
			}
		}
	}

	/**
	 * 向右滑动n次
	 */
	public void swipeToRight(int n) {
		int x = Driver.width;
		int y = Driver.height;
		for (int i = 0; i < n; i++) {
			try {
				driver.swipe((x / 8 * 1), (y / 2 * 1), (x / 8 * 7), (y / 2 * 1), 1000);
			} catch (Exception e) {
				LOG.error("第"+(i+1)+"次滑动失败",e);
			}
		}
	}

	/**
	 * 向上滑动n次
	 */
	public void swipeToUp(int n) {
		int x = Driver.width;
		int y = Driver.height;
		for (int i = 0; i < n; i++) {
			try {
				driver.swipe((x / 2 * 1), (y / 4 * 3), (x / 2 * 1), (y / 4 * 1), 1000);
			} catch (Exception e) {
				LOG.error("第"+(i+1)+"次滑动失败",e);
			}
		}
	}

	/**
	 * 向下滑动n次
	 */
	public void swipeToDown(int n) {
		int x = Driver.width;
		int y = Driver.height;
		for (int i = 0; i < n; i++) {
			try {
				driver.swipe((x / 2 * 1), (y / 8 * 1), (x / 2 * 1), (y / 8 * 7), 1000);
			} catch (Exception e) {
				LOG.error("第"+(i+1)+"次滑动失败",e);
			}
		}
	}

	/**
	 * 通过坐标点击
	 */
	public void tapByXY(int x, int y,int times) {
//		MobileElement me = get(name);
		try {
			driver.tap(times, x, y, 50);//点[x,y]坐标，200*5ms后释放，点一次
//			action.tap(x, y).perform();		
		} catch (Exception e) {
			LOG.error("坐标【"+x+","+y+"】点击失败");
		}
//		action.waitAction(1000);//unit: ms
	}

	/**
	 * 通过坐标长按N
	 * @param x
	 * @param y
	 * @param time 毫秒
	 * 
	 */
	public void longclickByXY(int x, int y, int time) {
		try {
			action.longPress(x, y).waitAction(time).perform();
		} catch (Exception e) {
			LOG.error("长按选择位置【"+x+","+y+"】失败");
		}
	}
	
	/**
	 * 通过已定位元素长按
	 * 
	 * @param me
	 * @param time 毫秒
	 * 
	 */
	public void longclickByElement(String name, int time) {
		MobileElement me = get(name);
		try {
			action.longPress(me).waitAction(time).perform();
		} catch (Exception e) {
			LOG.error("长按选择位置"+me+"失败");
		}
	}
	
	/**
	 * 定位元素坐标
	 * 
	 * @param me
	 * 
	 */
	public Point getLocation(String name, int index){
		List<MobileElement> me = getList(name);
		return me.get(index).getCenter();
	}
	/**
	 * 定位元素大小
	 * 
	 * @param me
	 * 
	 */
	public Dimension getSize(String name,int index){
		List<MobileElement> me=getList(name);
		return me.get(index).getSize();
	}
	
	public String getText(String name){
		MobileElement me = get(name);
		return me.getText();
	}
	
	public List<String> getTexts(String name){
		List<MobileElement> mes = getList(name);
		List<String> texts = new ArrayList<>();
		for(int i = 0; i < mes.size(); i++){
			texts.add(mes.get(i).getText());
		}
		return texts;
	}
	/***
	 * 向名为name的元素中输入str文本
	 * @param name
	 * @param str
	 */
	public void inputText(String name, String str){
		clearText(name);
		MobileElement me=get(name);
		try {
			me.sendKeys(str);
		} catch (Exception e) {
			LOG.error("文本输入失败");
		}
//		action.waitAction(1000);//unit: ms
	}
	
	/***
	 * clear Text
	 * @param text
	 */
	public void clearText(String name) {
		MobileElement me = get(name);
		String text = me.getText();
		String cmdstr="adb shell input keyevent 123";//光标移动到末尾
		try {
			Runtime.getRuntime().exec(cmdstr).waitFor();
		} catch (InterruptedException e1) {
			LOG.error("清除文本时失败");
		} catch (IOException e1) {
			LOG.error("清除文本时失败");
		}
	    for (int i = 0; i < text.length(); i++) {
	    	String cmdstr1="adb shell input keyevent 67";//退格键
	    	try {
				Runtime.getRuntime().exec(cmdstr1).waitFor();
			} catch (InterruptedException e) {
				LOG.error("清除文本时失败");
			} catch (IOException e) {
				LOG.error("清除文本时失败");
			}
	    }

	}
	/***
	 * 
	 * @param startx
	 * @param starty
	 * @param endx
	 * @param endy
	 * @param dura
	 */
	public void drag(int startx,int starty,int endx, int endy,int dura){
		try {
			driver.swipe(startx, starty, endx, endy, dura);
		} catch (Exception e) {
			LOG.error("滑动失败");
		}
		
	}
	/***
	 * 点击某个element
	 * @param me
	 */
	public void click(String name){
		MobileElement me = get(name);
		try {
			me.click();
			LOG.info("元素点击成功");
		} catch (Exception e) {
			LOG.fatal("元素点击失败");
		}
//		action.waitAction(2000);
		
	}
	

	/**
	 * 拖动元素
	 * 
	 */
	public void dragElement(String e1, String e2) {
		try {
			action.press(get(e1)).moveTo(get(e2)).release().perform();
		} catch (Exception e) {
			LOG.error("拖动失败");
		}
	}

	
	/**
	 * 以（x,y）为基准，计算得出（x,y-100）,(x,y+100)两个点，然后2个手指按住这两个点同时滑到（x,y）
	 */
	public void zoom() {
		int x = Driver.width;
		int y = Driver.height;
		int centerx = x/2;
		int centery = y/2;
		try {
			driver.zoom(centerx, centery);
		} catch (Exception e) {
			LOG.error("zoom失败");
		}
	}

	/**
	 * 两个手指从（x,y）点开始向（x,y-100）和（x,y+100）滑动
	 */
	public void pinch() {
		int x = Driver.width;
		int y = Driver.height;
		int centerx = x/2;
		int centery = y/2;
		try {
			driver.pinch(centerx, centery);
		} catch (Exception e) {
			LOG.error("pinch失败");
		}
	}

	
	/**
	 * 切换Webview页面查找元素
	 */
	public void switchtoWebview() {
		try {
			Set<String> contextNames = driver.getContextHandles();
			for (String contextName : contextNames) {
				LOG.info(contextName);
				if (contextName.toLowerCase().contains("webview")) {
					driver.context(contextName);
					LOG.info("跳转到web页 开始操作web页面");
					break;
				}
			}
		} catch (Exception e) {
			LOG.error("切换web页面失败");
			e.printStackTrace();
		}
	}

	public static ByType getByType(String type) {
		ByType byType = ByType.xpath;
		if (type == null || type.equalsIgnoreCase("xpath")) {
			byType = ByType.xpath;
		} else if (type.equalsIgnoreCase("id")) {
			byType = ByType.id;
		} else if (type.equalsIgnoreCase("name")) {
			byType = ByType.name;
		} else if (type.equalsIgnoreCase("className")) {
			byType = ByType.className;
		}
		return byType;
	}
	
	public void saveCpu (final long ms){
		new Thread(){
			public void run(){
				for(int i = 1 ; i > 0; i++){
					saveData("cpu.txt",""+getProcessCpuRate());
					try {
						Thread.sleep(ms);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	
	public void saveMemory(final long ms, final String type){
		new Thread(){
			public void run(){
				for(int i = 1; i > 0; i++){
					saveData("memory.txt", getPss(Config.get("auto.appPackage"),type));
					try {
						Thread.sleep(ms);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.start();
		
	}
	public String getPss(String packageName, String type){
		String str = shellReturn("adb shell dumpsys meminfo " + packageName,false);
		if(str != null && type == "new"){
			str = str.split("TOTAL")[2].replace(":", "").trim();
			return String.valueOf(Float.parseFloat(str)/1024);
		}
		else if(type == "old"){	
			str = str.split("TOTAL")[1].split("    ")[1].trim();			
			return String.valueOf(Float.parseFloat(str)/1024);
		}
		return "";
	}
	public String shellReturn (String command,Boolean b){
		String text = "";
		Process p = null;
		BufferedReader bf = null;
		BufferedInputStream in = null;
		try {
			p = Runtime.getRuntime().exec(command);
			int status = p.waitFor();
		    if(status != 0 ){
				throw new RuntimeException(String.format("Run shell commmad: %s, status: %s", command, status));
			}
			in = new BufferedInputStream(p.getInputStream());
			bf = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line;
			while((line = bf.readLine()) != null){
				text += line + "\n";
				if(b)
					break;
			}
			bf.close();
			in.close();
		} catch (Exception e) {
		}finally{
			if(bf != null){
				try {
					bf.close();
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
			if(p != null){
				p.destroy();
			}
		}
		return text.trim();
		
	}
	public float getProcessCpuRate(){
		float totalCpuTime1 = getTotalCpuTime();
		float processCpuTime1 = getAppCpuTime();
		try {
			Thread.sleep(360);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float totalCpuTime2 = getTotalCpuTime();
		float processCpuTime2 = getAppCpuTime();
		float cpuRate = 100 * (processCpuTime2 - processCpuTime1)
				/(totalCpuTime2 - totalCpuTime1);
		LOG.info("下面是cpu利用率");
		LOG.info(String.valueOf(cpuRate));
		return cpuRate;
		
	}
	public long getTotalCpuTime(){
		String[] cpuInfos = null;	
		String command = "adb shell cat /proc/stat";
		String load = shellReturn(command,true);
		cpuInfos = load.split(" ");
		long totalCpu = Long.parseLong(cpuInfos[2])
				+Long.parseLong(cpuInfos[3])+Long.parseLong(cpuInfos[4])
				+Long.parseLong(cpuInfos[5])+Long.parseLong(cpuInfos[6])
				+Long.parseLong(cpuInfos[7])+Long.parseLong(cpuInfos[8]);
		return totalCpu;
		
	}
	public long getAppCpuTime(){
		String[] cpuInfos = null;
		String pids = shellReturn("adb shell top -n 1| grep "+ Config.get("auto.appPackage") + "$",true);
		String pid = pids.split("  ")[0];	
		String command = "adb shell cat /proc/"+ pid + "/stat";
		String load = shellReturn(command,true);
		cpuInfos = load.split(" ");	
		long appCpuTime = Long.parseLong(cpuInfos[13])
				+Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
				+Long.parseLong(cpuInfos[26]);
		return appCpuTime;
		
	}
	public void saveData(String fileName, String str){
		FileOutputStream fos  = null;
		//创建File对象
		File file = new File("c:/logs/"+ fileName);
		try {
			if(!file.exists()){
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			fos = new FileOutputStream(file,true);
			fos.write((","+str).getBytes());
			if(fos != null){
				fos.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
