package com.haiyiyang.light.constant;

import java.io.File;
import java.nio.charset.Charset;

public class LightConstants {

	public static final String DOT = ".";
	public static final String SLASH = "/";
	public static final String COLON = ":";
	public static final String COMMA = ",";
	public static final String SEMICOLON = ";";
	public static final byte PROTOCOL_MAGIC_NUMBER = 'L';

	public static final int INT0 = 0, INT1 = 1, INT2 = 2, INT3 = 3;
	public static final byte BYTE0 = 0, BYTE1 = 1, BYTE2 = 2, BYTE3 = 3;
	public static final String STR0 = "0", STR1 = "1", STR2 = "2", STR3 = "3";

	public static final String UTF8 = "UTF-8";
	public static final String DOT_CONF = ".conf";
	public static final String DEFAULT = "default";
	public static final String LOCAL_IP = "127.0.0.1";
	public static final char FS_CHAR = File.separatorChar;
	public static final String LOCAL_UNDERLINE_IP = "127_0_0_1";
	public static final String ZK_DEFAULT_ADDRESS = "127.0.0.1:2181";
	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	public static final String USER_HOME = System.getProperty("user.home");

}