package com.haiyiyang.light.conf;

import java.util.List;

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

public class AppConf implements ResourceSubscriber {

	private static final Logger LR = LoggerFactory.getLogger(AppConf.class);

	private static final String SHARED = "shared";
	private static final String APP_CONF_PATH = "/light/app/";
	private static final String APP_CONF_LOCAL_PATH = LightUtil.getLocalPath(APP_CONF_PATH);

	private Config config;
	private String appConfPath;
	private String appConfLocalPath;
	private LightAppMeta lightAppMeta;

	private static volatile AppConf APP_CONF;

	private AppConf(LightAppMeta lightAppMeta) {
		this.lightAppMeta = lightAppMeta;
		this.setAppConfPath();
		this.setAppConfLocalPath();
		initialize();
	}

	public void setAppConfPath() {
		this.appConfPath = new StringBuilder(APP_CONF_PATH).append(lightAppMeta.getAppName())
				.append(LightConstants.DOT_CONF).toString();
	}

	public void setAppConfLocalPath() {
		this.appConfLocalPath = new StringBuilder(APP_CONF_LOCAL_PATH).append(lightAppMeta.getAppName())
				.append(LightConstants.DOT_CONF).toString();
	}

	public static AppConf singleton(LightAppMeta lightAppMeta) {
		if (APP_CONF != null) {
			return APP_CONF;
		}
		synchronized (AppConf.class) {
			if (APP_CONF == null) {
				APP_CONF = new AppConf(lightAppMeta);
			}
		}
		return APP_CONF;
	}

	private void initialize() {
		if (LightUtil.useLocalConf()) {
			config = ConfigFactory.load(appConfLocalPath);
		} else {
			doSubscribeAppConf();
		}
	}

	private void doSubscribeAppConf() {
		byte[] data = ResourceSubscription.getSubscription(this).getData(appConfPath);
		if (data == null || data.length == 0) {
			LR.error("The file [{}] does not exists, or is empty.", appConfPath);
			throw new LightException(LightException.FILE_NOT_FOUND_OR_EMPTY);
		}
		config = ConfigFactory.parseString(new String(data, LightConstants.CHARSET_UTF8));
	}

	public List<String> getShared() {
		if (config.hasPath(SHARED)) {
			return config.getStringList(SHARED);
		}
		return null;
	}

	@Override
	public String getRegistry() {
		return lightAppMeta.getConfigRegistry();
	}

	@Override
	public String getPath() {
		return this.appConfPath;
	}

	@Override
	public void subscribe() {
		doSubscribeAppConf();
		LR.info("Reloaded file [{}].", getPath());
	}

}
