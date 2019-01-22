package com.haiyiyang.light.meta.props;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.meta.LightAppMeta;
import com.haiyiyang.light.service.subscription.LightSubscriber;
import com.haiyiyang.light.service.subscription.LightSubscription;
import com.haiyiyang.light.utils.LightUtils;

import jodd.props.Props;
import jodd.props.PropsEntry;

public class AppProps implements LightSubscriber {

	private static final Logger LOGGER = LoggerFactory.getLogger(AppProps.class);

	private static final String SECTION_RESOURCE = "resource";
	private static final String APP_PROPS_PATH = "/light/app/";
	private static final String APP_PROPS_LOCAL_PATH = LightUtils.getLocalPath(APP_PROPS_PATH);

	private Props props = new Props();
	private String appPropsPath;
	private String appPropsLocalPath;
	private LightAppMeta lightAppMeta;

	private static volatile AppProps APP_PROPS;

	private AppProps(LightAppMeta lightAppMeta) {
		this.lightAppMeta = lightAppMeta;
		this.appPropsPath = new StringBuilder(APP_PROPS_PATH).append(lightAppMeta.getAppName())
				.append(LightConstants.DOT_PROPS).toString();
		this.appPropsLocalPath = new StringBuilder(APP_PROPS_LOCAL_PATH).append(lightAppMeta.getAppName())
				.append(LightConstants.DOT_PROPS).toString();
		initializeAppProps();
	}

	public static AppProps SINGLETON(LightAppMeta lightAppMeta) {
		if (APP_PROPS != null) {
			return APP_PROPS;
		}
		synchronized (AppProps.class) {
			if (APP_PROPS == null) {
				APP_PROPS = new AppProps(lightAppMeta);
			}
		}
		return APP_PROPS;
	}

	private void initializeAppProps() {
		if (LightUtils.useLocalProps()) {
			File file = new File(appPropsLocalPath);
			if (!file.isFile()) {
				LOGGER.error("The file [{}] does not exists.", appPropsLocalPath);
				throw new LightException(LightException.FILE_NOT_FOUND);
			}
			try {
				props.load(file);
			} catch (Exception ex) {
				LOGGER.error("Loading file [{}] failed.", appPropsLocalPath);
				throw new LightException(LightException.LOADING_FILE_FAILED);
			}
		} else {
			doSubscribeAppProps();
		}
	}

	private void doSubscribeAppProps() {
		byte[] data = LightSubscription.getSubscription(this).getData(appPropsPath);
		if (data == null || data.length == 0) {
			LOGGER.error("The file [{}] does not exists, or is empty.", appPropsPath);
			throw new LightException(LightException.FILE_NOT_FOUND_OR_EMPTY);
		}
		try {
			props.load(new ByteArrayInputStream(data));
		} catch (IOException e) {
			LOGGER.error("Loading file [{}] failed.", appPropsPath);
			throw new LightException(LightException.LOADING_FILE_FAILED);
		}
	}

	public Map<String, String> getResources() {
		Map<String, String> map = new HashMap<>();
		Iterator<PropsEntry> it = props.entries().section(SECTION_RESOURCE).iterator();
		PropsEntry pe;
		while (it.hasNext()) {
			pe = it.next();
			map.put(pe.getKey(), pe.getValue());
		}
		return map;
	}

	public String getPropsValue(String key) {
		return props.getValue(key);
	}

	public String getPropsValue(final String key, String profiles) {
		return props.getValue(key, profiles);
	}

	@Override
	public String getRegistry() {
		return lightAppMeta.getConfigRegistry();
	}

	@Override
	public String getPath() {
		return this.appPropsPath;
	}

	@Override
	public void subscribe() {
		doSubscribeAppProps();
		LOGGER.info("Reloaded file [{}].", getPath());
	}

}
