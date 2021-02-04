package com.haiyiyang.light.conf;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.context.LightContext;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.resource.subscription.ResourceSubscriber;
import com.haiyiyang.light.resource.subscription.ResourceSubscription;
import com.haiyiyang.light.utils.LightUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SharedConf implements ResourceSubscriber {

	private static final Logger LR = LoggerFactory.getLogger(SharedConf.class);

	public static final String SHARED_PATH = "/light/shared/";
	public static final String SHARED_LOCAL_PATH = LightUtil.getLocalPath(SHARED_PATH);
	private static Map<String, SharedConf> SHARED_CONF_MAP = new ConcurrentHashMap<>();

	private String path;
	private Config config;

	private SharedConf(String path) {
		this.path = path;
	}

	private void initialize() {
		if (LightUtil.useLocalConf()) {
			config = ConfigFactory.load(path);
		} else {
			doSubscribeSharedConf();
		}
	}

	public static void subscribeShared(List<String> sharedList) {
		if (sharedList != null && !sharedList.isEmpty()) {
			StringBuilder fullPath = new StringBuilder(64);
			int length = fullPath.length();
			fullPath.append(LightUtil.useLocalConf() ? SHARED_LOCAL_PATH : SHARED_PATH);
			for (String shared : sharedList) {
				SharedConf sharedConf = new SharedConf(
						fullPath.delete(length, fullPath.length()).append(shared).toString());
				if (!SHARED_CONF_MAP.containsKey(shared)) {
					SHARED_CONF_MAP.put(sharedConf.path, sharedConf);
				}
				sharedConf.initialize();
			}
		}
	}

	private void doSubscribeSharedConf() {
		byte[] data = ResourceSubscription.getSubscription(this).getData(this.path);
		if (data == null || data.length == 0) {
			LR.error("The file [{}] does not exists, or is empty.", this.path);
			throw new LightException(LightException.FILE_NOT_FOUND_OR_EMPTY);
		}
		config = ConfigFactory.parseString(new String(data, LightConstants.CHARSET_UTF8));
	}

	public Config getValue(String shared) {
		SharedConf sharedConf = SHARED_CONF_MAP.get(shared);
		if (sharedConf != null) {
			return sharedConf.config;
		}
		return null;
	}

	@Override
	public String getRegistry() {
		return LightContext.getLightAppMeta().getConfigRegistry();
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public void subscribe() {
		doSubscribeSharedConf();
		LR.info("Reloaded file [{}].", getPath());
	}
}
