package com.dan.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import org.springframework.stereotype.Component;
@Component
@Configuration
@PropertySource(value = {"element_mijia_bedlight.properties"}, encoding="utf-8")
public class ElementConfig {

	static Environment evn;

	public ElementConfig(Environment evn1) {
		evn = evn1;
	}

	/**
	 * 根据配置项名称获取配置参数，如果获取不到，则返回null
	 * 
	 * @param name
	 * @return
	 */
	public static String get(String name) {
		return evn.getProperty(name);
	}
	
}

