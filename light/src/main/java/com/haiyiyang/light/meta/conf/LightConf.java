package com.haiyiyang.light.meta.conf;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.meta.LightAppMeta;
import com.haiyiyang.light.resource.subscription.ResourceSubscriber;
import com.haiyiyang.light.resource.subscription.ResourceSubscription;
import com.haiyiyang.light.rpc.server.IpPort;
import com.haiyiyang.light.utils.LightUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class LightConf implements ResourceSubscriber {

	private static final Logger LOGGER = LoggerFactory.getLogger(LightConf.class);

	private static final String OPEN_GROUP = "openGroup";
	private static final String DOMAIN_PACKAGES = "domainPackages";
	private static final String DISABLE_PUBLISH = "disablePublish";
	private static final String SERVER_THREAD_QUANTITY = "serverThreadQuantity";

	private static final String TIMEOUT = "timeout";
	private static final long DEFAULT_TIMEOUT = 10000;

	private static final String MIN_THREAD = "minThread";
	private static final int DEFAULT_MIN_THREAD = 10;
	private static final String MAX_THREAD = "maxThread";
	private static final int DEFAULT_MAX_THREAD = 100;

	private static final String PROXY_TYPE = "proxyType";
	private static final String DEFAULT_PROXY_TYPE = "JDK";

	private static final String SERIALIZER = "serializer";
	private static final String DEFAULT_SERIALIZER = "JSON";

	private static final String REGISTRY = "registry";
	private static final String DEFAULT_REGISTRY = "127.0.0.1:2181";

	private static final String IP_SEGMENT_PREFIX = "ipSegmentPrefix";

	private static final byte DEFAULT_SERVER_LOAD_WEIGHT = 3;
	private static final String SERVER_LOAD_WEIGHT = "serverLoadWeight";

	private static final String LIGHT_CONF_URL = "/light/light.conf";
	private static final String LIGHT_CONF_LOCAL_URL = LightUtil.getLocalPath(LIGHT_CONF_URL);

	private static volatile LightConf LIGHT_CONF;

	private LightAppMeta lightAppMeta;
	private List<String> domainPackageList;

	private Config config;

	private LightConf(LightAppMeta lightAppMeta) {
		this.lightAppMeta = lightAppMeta;
		initialize();
	}

	public static LightConf singleton(LightAppMeta lightAppMeta) {
		if (LIGHT_CONF != null) {
			return LIGHT_CONF;
		}
		synchronized (LightConf.class) {
			if (LIGHT_CONF == null) {
				LIGHT_CONF = new LightConf(lightAppMeta);
			}
		}
		return LIGHT_CONF;
	}

	private void initialize() {
		if (LightUtil.useLocalConf()) {
			config = ConfigFactory.load(LIGHT_CONF_LOCAL_URL);
		} else {
			doSubscribeLightConf();
		}
	}

	private void doSubscribeLightConf() {
		byte[] data = ResourceSubscription.getSubscription(this).getData(LIGHT_CONF_URL);
		if (data == null || data.length == 0) {
			LOGGER.error("The file [{}] does not exists, or is empty.", LIGHT_CONF_URL);
			throw new LightException(LightException.FILE_NOT_FOUND_OR_EMPTY);
		}
		config = ConfigFactory.parseString(new String(data, LightConstants.CHARSET_UTF8));
	}

	public List<String> getDomainPackages() {
		if (domainPackageList != null) {
			return domainPackageList;
		}
		String domainPackages = config.getString(DOMAIN_PACKAGES);
		if (domainPackages == null) {
			domainPackageList = Collections.emptyList();
		} else {
			domainPackageList = Lists.newArrayList(domainPackages.split(LightConstants.COMMA));
			domainPackageList.sort((a, b) -> (a.length() > b.length()) ? -1 : 1);
		}
		return domainPackageList;
	}

	public boolean isOpenGroup() {
		return LightConstants.STR1.equals(config.getString(OPEN_GROUP));
	}

	public String getIpSegmentPrefix() {
		if (config.hasPath(IP_SEGMENT_PREFIX)) {
			return config.getString(IP_SEGMENT_PREFIX);
		}
		return lightAppMeta.getAppNameDotUnderlineIP();
	}

	public boolean isDisablePublish() {
//		return LightConstants.STR1.equals(config.getString(DISABLE_PUBLISH, lightAppMeta.getAppNameDotUnderlineIP()));
		return true;
	}

	public int getServerThreadQuantity() {
//		Integer quantity = config.getIntegerValue(SERVER_THREAD_QUANTITY, lightAppMeta.getAppNameDotUnderlineIP());
//		if (quantity != null) {
//			return quantity.intValue();
//		}
		return LightConstants.INT1;
	}

	public int getMinThread() {
//		Integer minThread = config.getIntegerValue(MIN_THREAD, lightAppMeta.getAppNameDotUnderlineIP());
//		if (minThread != null) {
//			return minThread.intValue();
//		}
		return DEFAULT_MIN_THREAD;
	}

	public int getMaxThread() {
//		Integer maxThread = config.getIntegerValue(MAX_THREAD, lightAppMeta.getAppNameDotUnderlineIP());
//		if (maxThread != null) {
//			return maxThread.intValue();
//		}
		return DEFAULT_MAX_THREAD;
	}

	public String getPublishRegistry(String appName) {
//		String publishRegistry = config.getString(REGISTRY, appName);
//		if (publishRegistry != null) {
//			return publishRegistry;
//		}
		return DEFAULT_REGISTRY;
	}

	public String getPublishRegistry() {
//		String publishRegistry = config.getString(REGISTRY, lightAppMeta.getAppNameDotUnderlineIP());
//		if (publishRegistry != null && !publishRegistry.isEmpty()) {
//			return publishRegistry;
//		}
		return DEFAULT_REGISTRY;
	}

	public IpPort getDesignatedIpPort(String appName) {
//		String designatedIpPort = config.getString(appName, lightAppMeta.getAppNameDotUnderlineIP());
//		if (designatedIpPort != null) {
//			String[] ipPortArray = designatedIpPort.split(LightConstants.COLON);
//			if (ipPortArray.length == 2) {
//				return new IpPort(ipPortArray[0], Integer.parseInt(ipPortArray[1]));
//			}
//		}
		return null;
	}

	public long getTimeout() {
//		Long timeout = config.getLongValue(TIMEOUT, lightAppMeta.getAppNameDotUnderlineIP());
//		if (timeout != null) {
//			return timeout.longValue();
//		}
		return DEFAULT_TIMEOUT;
	}

	public String getProxyType() {
//		String proxyType = config.getString(PROXY_TYPE, lightAppMeta.getAppNameDotUnderlineIP());
//		if (proxyType != null) {
//			return proxyType;
//		}
		return DEFAULT_PROXY_TYPE;
	}

	public String getSerializer() {
//		String serializer = config.getString(SERIALIZER, lightAppMeta.getAppNameDotUnderlineIP());
//		if (serializer != null) {
//			return serializer;
//		}
		return DEFAULT_SERIALIZER;
	}

	public byte getServerLoadWeight() {
//		Integer weight = config.getIntegerValue(SERVER_LOAD_WEIGHT, lightAppMeta.getUnderlineIPDotAppName());
//		if (weight != null) {
//			return weight.byteValue();
//		}
		return DEFAULT_SERVER_LOAD_WEIGHT;
	}

	@Override
	public String getRegistry() {
		return lightAppMeta.getConfigRegistry();
	}

	@Override
	public String getPath() {
		return LIGHT_CONF_URL;
	}

	@Override
	public void subscribe() {
		doSubscribeLightConf();
		LOGGER.info("Reloaded file [{}].", getPath());
	}

}
