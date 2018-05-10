package com.dan.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;



@ComponentScan
public class SpringConfig {

	@Autowired
	Environment evn ;
	
	@Bean
	public Config config() {
		return new Config(evn);
	}
	
	@Bean
	public ElementConfig elementConfig() {
		return new ElementConfig(evn);
	}
}
