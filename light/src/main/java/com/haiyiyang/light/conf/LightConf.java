package com.haiyiyang.light.conf;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.conf.root.LightConfRoot;
import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.meta.LightAppMeta;
import com.haiyiyang.light.resource.subscription.ResourceSubscriber;
import com.haiyiyang.light.resource.subscription.ResourceSubscription;
import com.haiyiyang.light.rpc.server.IpPort;
import com.haiyiyang.light.utils.LightUtil;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

public class LightConf implements ResourceSubscriber {

	private static final Logger LR = LoggerFactory.getLogger(LightConf.class);

	private static final String LIGHT_CONF_URL = "/light/light.conf";
	private static final String LIGHT_CONF_LOCAL_URL = LightUtil.getLocalPath(LIGHT_CONF_URL);

	private static volatile LightConf LIGHT_CONF;

	private LightAppMeta lightAppMeta;

	LightConfRoot lightConfRoot;

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
			lightConfRoot = ConfigBeanFactory.create(ConfigFactory.parseFile(new File(LIGHT_CONF_LOCAL_URL)),
					LightConfRoot.class);
		} else {
			doSubscribeLightConf();
		}
		if (lightConfRoot.getDomainList() != null) {
			lightConfRoot.getDomainList().sort((a, b) -> (a.length() > b.length()) ? -1 : 1);
		}
	}

	private void doSubscribeLightConf() {
		byte[] data = ResourceSubscription.getSubscription(this).getData(LIGHT_CONF_URL);
		if (data == null || data.length == 0) {
			LR.error("The file [{}] does not exists, or is empty.", LIGHT_CONF_URL);
			throw new LightException(LightException.FILE_NOT_FOUND_OR_EMPTY);
		}
		lightConfRoot = ConfigBeanFactory
				.create(ConfigFactory.parseString(new String(data, LightConstants.CHARSET_UTF8)), LightConfRoot.class);
	}

	public List<String> getDomainPackages() {
		if (lightConfRoot.getDomainList() != null) {
			return lightConfRoot.getDomainList();
		}
		return Collections.emptyList();
	}

	public boolean isOpenGroup() {
		return LightConstants.INT1 == lightConfRoot.getDisableGrouping();
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
		LR.info("Reloaded file [{}].", getPath());
	}

	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getServerThreadQuantity() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getSerializer() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMinThread() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaxThread() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IpPort getDesignatedIpPort(String appName) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte getServerLoadWeight() {
		// TODO Auto-generated method stub
		return 0;
	}

}
