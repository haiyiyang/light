package com.haiyiyang.light.utils;

import com.haiyiyang.light.constant.LightConstants;

public class LightUtil {

	public static boolean useLocalConf() {
		return LightConstants.STR1.equals(System.getProperty("useLocalProps"));
	}

	public static String getLocalPath(String path) {
		return LightConstants.USER_HOME + path.replace('/', LightConstants.FS_CHAR);
	}
}
