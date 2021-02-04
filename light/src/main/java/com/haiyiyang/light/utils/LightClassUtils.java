package com.haiyiyang.light.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

public class LightClassUtils {

	private static final Logger LR = LoggerFactory.getLogger(LightClassUtils.class);

	// TODO change to Guava cache.
	private static final Map<String, Class<?>> userServiceClassMap = new HashMap<>(32);

	public static Class<?> forName(String name) {
		if (userServiceClassMap.containsKey(name)) {
			return userServiceClassMap.get(name);
		}
		try {
			userServiceClassMap.put(name, ClassUtils.forName(name, null));
			return userServiceClassMap.get(name);
		} catch (ClassNotFoundException | LinkageError e) {
			LR.error(e.getMessage());
			return null;
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, LinkageError {
		Class<?> clazz = ClassUtils.forName(int[].class.getName(), null);
		System.out.println(int[].class.getName());
		System.out.println(int[].class.toString());
		System.out.println(clazz.getName());
		Class<?> clazz1 = ClassUtils.forName(Integer[].class.getName(), null);
		System.out.println(Integer[].class.getName());
		System.out.println(Integer[].class.toString());
		System.out.println(clazz1.getName());
	}

}
