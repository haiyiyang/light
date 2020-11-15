package com.haiyiyang.light.app.conf;

import java.io.IOException;

import com.haiyiyang.light.constant.LightConstants;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SettingsConf {

	private static final String APP_NAME = "appName";
	private static final String SCAN_PACKAGES = "scanPackages";
	private static final String ANNOTATED_CLASSES = "annotatedClasses";
	private static final String FILE_SETTINGS_CONF = "settings.conf";

	private static volatile SettingsConf SETTINGS_CONF;

	private String appName;
	private String scanPackages;
	private String annotatedClasses;

	private SettingsConf() {
		this.initialize();
	}

	public static SettingsConf singleton() throws IOException {
		if (SETTINGS_CONF != null) {
			return SETTINGS_CONF;
		}
		synchronized (SettingsConf.class) {
			if (SETTINGS_CONF == null) {
				SETTINGS_CONF = new SettingsConf();
			}
		}
		return SETTINGS_CONF;
	}

	private void initialize() {
		Config config = ConfigFactory.load(FILE_SETTINGS_CONF);
		this.appName = config.getString(APP_NAME);
		this.scanPackages = config.getString(SCAN_PACKAGES);
		this.annotatedClasses = config.getString(ANNOTATED_CLASSES);
	}

	public String getAppName() {
		return this.appName;
	}

	public String getScanPackages() {
		return this.scanPackages;
	}

	public String getAnnotatedClasses() {
		return this.annotatedClasses;
	}

	public Class<?>[] getConfigurableClasses() throws ClassNotFoundException {
		if (this.annotatedClasses == null || this.annotatedClasses.isEmpty()) {
			return null;
		}
		String[] classesNames = this.annotatedClasses.split(LightConstants.COMMA);
		Class<?>[] classes = new Class<?>[classesNames.length];
		for (int i = 0; i < classesNames.length; i++) {
			classes[i] = Class.forName(classesNames[i]);
		}
		return classes;
	}

}
