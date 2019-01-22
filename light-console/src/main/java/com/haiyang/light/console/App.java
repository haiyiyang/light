package com.haiyang.light.console;

import org.beetl.core.GroupTemplate;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import com.haiyang.light.console.basic.Configration;

@ServletComponentScan()
@SpringBootApplication()
@Import(Configration.class)
public class App {
	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(App.class, args);
		BeetlGroupUtilConfiguration bean = (BeetlGroupUtilConfiguration) applicationContext.getBean("beetlConfig");
		System.out.println(bean);
		GroupTemplate group = bean.getGroupTemplate();
		System.out.println(group);
	}
}
