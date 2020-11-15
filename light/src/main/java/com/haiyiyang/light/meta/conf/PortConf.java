package com.haiyiyang.light.meta.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.meta.LightAppMeta;
import com.haiyiyang.light.resource.subscription.ResourceSubscriber;
import com.haiyiyang.light.resource.subscription.ResourceSubscription;
import com.haiyiyang.light.utils.LightUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PortConf implements ResourceSubscriber {

	private static final Logger LR = LoggerFactory.getLogger(PortConf.class);

	private static final int DEFAULT_APP_PORT = 8080;
	private static final String APP_PORT_CONF_URL = "/light/port.conf";
	private static final String APP_PORT_CONF_LOCAL_URL = LightUtil.getLocalPath(APP_PORT_CONF_URL);

	private static volatile PortConf PORT_CONF;
	private static LightAppMeta LIGHT_APP_META;
	private static Config PORT_CONFIG;

	private PortConf(LightAppMeta lightAppMeta) {
		PortConf.LIGHT_APP_META = lightAppMeta;
		initialize();
	}

	public static PortConf singleton(LightAppMeta lightAppMeta) {
		if (PORT_CONF != null) {
			return PORT_CONF;
		}
		synchronized (PortConf.class) {
			if (PORT_CONF == null) {
				PORT_CONF = new PortConf(lightAppMeta);
			}
		}
		return PORT_CONF;
	}

	private void initialize() {
		if (LightUtil.useLocalConf()) {
			PORT_CONFIG = ConfigFactory.load(APP_PORT_CONF_LOCAL_URL);
		} else {
			doSubscribePortConf();
		}
	}

	private void doSubscribePortConf() {
		byte[] data = ResourceSubscription.getSubscription(this).getData(APP_PORT_CONF_URL);
		if (data == null || data.length == 0) {
			LR.error("The file [{}] does not exists, or is empty.", APP_PORT_CONF_URL);
			throw new LightException(LightException.FILE_NOT_FOUND_OR_EMPTY);
		}
		PORT_CONFIG = ConfigFactory.parseString(new String(data, LightConstants.CHARSET_UTF8));
	}

	public int getAppPort() {
		if (PORT_CONFIG.hasPath(LIGHT_APP_META.getAppName())) {
			return PORT_CONFIG.getInt(LIGHT_APP_META.getAppName());
		}
		return DEFAULT_APP_PORT;
	}

	@Override
	public String getRegistry() {
		return LIGHT_APP_META.getConfigRegistry();
	}

	@Override
	public String getPath() {
		return APP_PORT_CONF_URL;
	}

	@Override
	public void subscribe() {
		doSubscribePortConf();
		LR.info("Reloaded file [{}].", getPath());
	}

}
