package com.haiyiyang.light.meta.props;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.meta.LightAppMeta;
import com.haiyiyang.light.rpc.server.IpPort;
import com.haiyiyang.light.service.subscription.LightSubscriber;
import com.haiyiyang.light.service.subscription.LightSubscription;
import com.haiyiyang.light.utils.LightUtils;

import jodd.props.Props;

public class LightProps implements LightSubscriber {

	private static final Logger LOGGER = LoggerFactory.getLogger(LightProps.class);

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

	private static final String LIGHT_PROPS_URL = "/light/light.props";
	private static final String LIGHT_PROPS_LOCAL_URL = LightUtils.getLocalPath(LIGHT_PROPS_URL);

	private Props props = new Props();;
	private static volatile LightProps LIGHT_PROPS;

	private LightAppMeta lightAppMeta;
	private List<String> domainPackageList;

	private LightProps(LightAppMeta lightAppMeta) {
		this.lightAppMeta = lightAppMeta;
		initializeLightProps();
	}

	public static LightProps SINGLETON(LightAppMeta lightAppMeta) {
		if (LIGHT_PROPS != null) {
			return LIGHT_PROPS;
		}
		synchronized (LightProps.class) {
			if (LIGHT_PROPS == null) {
				LIGHT_PROPS = new LightProps(lightAppMeta);
			}
		}
		return LIGHT_PROPS;
	}

	private void initializeLightProps() {
		if (LightUtils.useLocalProps()) {
			File file = new File(LIGHT_PROPS_LOCAL_URL);
			if (!file.isFile()) {
				LOGGER.error("The file [{}] does not exists.", LIGHT_PROPS_LOCAL_URL);
				throw new LightException(LightException.FILE_NOT_FOUND);
			}
			try {
				props.load(file);
			} catch (Exception ex) {
				LOGGER.error("Loading file [{}] failed.", LIGHT_PROPS_LOCAL_URL);
				throw new LightException(LightException.LOADING_FILE_FAILED);
			}
		} else {
			doSubscribeLightProps();
		}
	}

	private void doSubscribeLightProps() {
		byte[] data = LightSubscription.getSubscription(this).getData(LIGHT_PROPS_URL);
		if (data == null || data.length == 0) {
			LOGGER.error("The file [{}] does not exists, or is empty.", LIGHT_PROPS_URL);
			throw new LightException(LightException.FILE_NOT_FOUND_OR_EMPTY);
		}
		try {
			props.load(new ByteArrayInputStream(data));
		} catch (IOException e) {
			LOGGER.error("Loading file [{}] failed.", LIGHT_PROPS_URL);
			throw new LightException(LightException.LOADING_FILE_FAILED);
		}
	}

	public List<String> getDomainPackages() {
		if (domainPackageList != null) {
			return domainPackageList;
		}
		String domainPackages = props.getValue(DOMAIN_PACKAGES);
		if (domainPackages == null) {
			domainPackageList = Collections.emptyList();
		} else {
			domainPackageList = Lists.newArrayList(domainPackages.split(LightConstants.COMMA));
			domainPackageList.sort((a, b) -> (a.length() > b.length()) ? -1 : 1);
		}
		return domainPackageList;
	}

	public boolean isOpenGroup() {
		return LightConstants.STR1.equals(props.getValue(OPEN_GROUP));
	}

	public String getIpSegmentPrefix() {
		return props.getValue(IP_SEGMENT_PREFIX, lightAppMeta.getAppNameDotUnderlineIP());
	}

	public boolean isDisablePublish() {
		return LightConstants.STR1.equals(props.getValue(DISABLE_PUBLISH, lightAppMeta.getAppNameDotUnderlineIP()));
	}

	public int getServerThreadQuantity() {
		Integer quantity = props.getIntegerValue(SERVER_THREAD_QUANTITY, lightAppMeta.getAppNameDotUnderlineIP());
		if (quantity != null) {
			return quantity.intValue();
		}
		return LightConstants.INT1;
	}

	public int getMinThread() {
		Integer minThread = props.getIntegerValue(MIN_THREAD, lightAppMeta.getAppNameDotUnderlineIP());
		if (minThread != null) {
			return minThread.intValue();
		}
		return DEFAULT_MIN_THREAD;
	}

	public int getMaxThread() {
		Integer maxThread = props.getIntegerValue(MAX_THREAD, lightAppMeta.getAppNameDotUnderlineIP());
		if (maxThread != null) {
			return maxThread.intValue();
		}
		return DEFAULT_MAX_THREAD;
	}

	public String getPublishRegistry(String appName) {
		String publishRegistry = props.getValue(REGISTRY, appName);
		if (publishRegistry != null) {
			return publishRegistry;
		}
		return DEFAULT_REGISTRY;
	}

	public String getPublishRegistry() {
		String publishRegistry = props.getValue(REGISTRY, lightAppMeta.getAppNameDotUnderlineIP());
		if (publishRegistry != null && !publishRegistry.isEmpty()) {
			return publishRegistry;
		}
		return DEFAULT_REGISTRY;
	}

	public IpPort getDesignatedIpPort(String appName) {
		String designatedIpPort = props.getValue(appName, lightAppMeta.getAppNameDotUnderlineIP());
		if (designatedIpPort != null) {
			String[] ipPortArray = designatedIpPort.split(LightConstants.COLON);
			if (ipPortArray.length == 2) {
				return new IpPort(ipPortArray[0], Integer.parseInt(ipPortArray[1]));
			}
		}
		return null;
	}

	public long getTimeout() {
		Long timeout = props.getLongValue(TIMEOUT, lightAppMeta.getAppNameDotUnderlineIP());
		if (timeout != null) {
			return timeout.longValue();
		}
		return DEFAULT_TIMEOUT;
	}

	public String getProxyType() {
		String proxyType = props.getValue(PROXY_TYPE, lightAppMeta.getAppNameDotUnderlineIP());
		if (proxyType != null) {
			return proxyType;
		}
		return DEFAULT_PROXY_TYPE;
	}

	public String getSerializer() {
		String serializer = props.getValue(SERIALIZER, lightAppMeta.getAppNameDotUnderlineIP());
		if (serializer != null) {
			return serializer;
		}
		return DEFAULT_SERIALIZER;
	}

	public byte getServerLoadWeight() {
		Integer weight = props.getIntegerValue(SERVER_LOAD_WEIGHT, lightAppMeta.getUnderlineIPDotAppName());
		if (weight != null) {
			return weight.byteValue();
		}
		return DEFAULT_SERVER_LOAD_WEIGHT;
	}

	@Override
	public String getRegistry() {
		return lightAppMeta.getConfigRegistry();
	}

	@Override
	public String getPath() {
		return LIGHT_PROPS_URL;
	}

	@Override
	public void subscribe() {
		doSubscribeLightProps();
		LOGGER.info("Reloaded file [{}].", getPath());
	}

}
