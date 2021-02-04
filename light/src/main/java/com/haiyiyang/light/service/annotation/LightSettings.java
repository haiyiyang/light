package com.haiyiyang.light.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LightSettings {
	public String appName();

	public String configURL() default "configURL";

	public String scanPackages() default "";

	public String annotatedClasses() default "";
}
