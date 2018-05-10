package com.test.tools;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import com.dan.common.SpringConfig;
import com.dan.common.Config;


@ContextConfiguration(classes = SpringConfig.class)
public class ConfigTest extends AbstractTestNGSpringContextTests{

	@Autowired
	Config config;
	
	
	@Test
	public void Configure() {
		System.out.println(config.get("auto.platform"));
	}
}
