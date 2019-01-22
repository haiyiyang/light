package com.haiyiyang.light.app.props;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.constant.LightConstants;

import jodd.props.Props;

public class SettingsProps {

	private static final Logger LOGGER = LoggerFactory.getLogger(SettingsProps.class);

	private Props props = new Props();
	private static volatile SettingsProps SETTINGS_PROPS;

	private static final String APP_NAME = "appName";
	private static final String SCAN_PACKAGES = "scanPackages";
	private static final String ANNOTATED_CLASSES = "annotatedClasses";
	private static final String FILE_SETTINGS_PROPS = "settings.props";

	private SettingsProps() throws IOException {
		initializeSettingsProps();
	}

	public static SettingsProps SINGLETON() throws IOException {
		if (SETTINGS_PROPS != null) {
			return SETTINGS_PROPS;
		}
		synchronized (SettingsProps.class) {
			if (SETTINGS_PROPS == null) {
				SETTINGS_PROPS = new SettingsProps();
			}
		}
		return SETTINGS_PROPS;
	}

	private void initializeSettingsProps() throws IOException {
		Enumeration<URL> ps = null;
		try {
			ps = Thread.currentThread().getContextClassLoader().getResources(FILE_SETTINGS_PROPS);
		} catch (IOException e) {
			LOGGER.error("The file [settings.props] does not exists.");
			throw e;
		}
		if (ps != null && ps.hasMoreElements()) {
			InputStream in = null;
			try {
				in = ps.nextElement().openStream();
				props.load(in);
				LOGGER.info("Loaded the file [settings.props] successfully.");
			} catch (IOException e) {
				LOGGER.error("Loaded the file [settings.props] unsuccessfully.");
				throw e;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						LOGGER.error("Failed to close the file [settings.props].");
						throw e;
					}
				}
			}
		}
		LOGGER.info("Initialized configuration[SettingsProps].");
	}

	public String getAppName() {
		return props.getValue(APP_NAME);
	}

	public String getScanPackages() {
		return props.getValue(SCAN_PACKAGES);
	}

	public String getAnnotatedClasses() {
		return props.getValue(ANNOTATED_CLASSES);
	}

	public Class<?>[] getConfigurableClasses() throws ClassNotFoundException {
		String annotatedClasses = getAnnotatedClasses();
		if (annotatedClasses == null || annotatedClasses.isEmpty()) {
			return null;
		}
		String[] classesNames = annotatedClasses.split(LightConstants.COMMA);
		Class<?>[] classes = new Class<?>[classesNames.length];
		for (int i = 0; i < classesNames.length; i++) {
			classes[i] = Class.forName(classesNames[i]);
		}
		return classes;
	}

}
