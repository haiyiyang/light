package com.haiyang.light.console.basic;

import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import com.haiyang.light.console.Constants;

@Configurable
public class Configration {

	@Bean(name = "beetlConfig", initMethod = "init")
	BeetlGroupUtilConfiguration getBeetlGroupUtilConfiguration() {
		return new BeetlGroupUtilConfiguration();
	}

	@Bean(name = "viewResolver")
	BeetlSpringViewResolver getBeetlSpringViewResolver() {
		BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
		beetlSpringViewResolver.setContentType(Constants.DEFAULT_CONTENT_TYPE);
		return beetlSpringViewResolver;
	}
}
