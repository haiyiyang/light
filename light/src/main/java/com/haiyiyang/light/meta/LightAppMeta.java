package com.haiyiyang.light.meta;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.meta.conf.AppConf;
import com.haiyiyang.light.meta.conf.LightConf;
import com.haiyiyang.light.meta.conf.PortConf;
import com.haiyiyang.light.meta.conf.SharedConf;
import com.haiyiyang.light.rpc.server.LightConfig;
import com.haiyiyang.light.utils.NetworkUtils;

public class LightAppMeta {

	private static final Logger LR = LoggerFactory.getLogger(LightAppMeta.class);

	private String appName;
	private String configRegistry;
	private LightConf lightConf;
	private PortConf portConf;
	private AppConf appConf;
	private SharedConf resourceConf;

	private byte zeroOneGrouping;
	private String appNameDotUnderlineIP;
	private String underlineIPDotAppName;
	private String machineIP = LightConstants.LOCAL_IP;
	private String machineUnderlineIP = LightConstants.LOCAL_UNDERLINE_IP;

	private static volatile LightAppMeta LIGHT_APP_META;

	private LightAppMeta(String appName) throws LightException {
		this.appName = appName;
		this.configRegistry = LightConfig.getConfigRegistry();
		this.lightConf = LightConf.singleton(this);
		this.portConf = PortConf.singleton(this);
		this.appConf = AppConf.singleton(this);
		SharedConf.subscribeShared(appConf.getShared());
		this.setMachineIPAndZeroOneGrouping();
		LR.info("Initialized LightAppMeta.");
	}

	public static LightAppMeta SINGLETON(String appName) {
		Assert.notNull(appName, "[appName] cannot be empty.");
		if (LIGHT_APP_META == null) {
			synchronized (LightAppMeta.class) {
				if (LIGHT_APP_META == null) {
					LIGHT_APP_META = new LightAppMeta(appName);
				}
			}
		}
		return LIGHT_APP_META;
	}

	private void setMachineIPAndZeroOneGrouping() {
		Set<String> ips = NetworkUtils.getLocalIps();
		String ipSegmentPrefix = lightConf.getIpSegmentPrefix();
		for (String ip : ips) {
			if (ipSegmentPrefix == null || ip.startsWith(ipSegmentPrefix)) {
				machineIP = ip;
				machineUnderlineIP = ip.replace('.', '_');
				break;
			}
		}
		if (ipSegmentPrefix != null && !machineIP.startsWith(ipSegmentPrefix)) {
			throw new LightException(LightException.Code.PERMISSION_ERROR, LightException.NO_NETWORK_PERMISSION);
		}
		zeroOneGrouping = Byte.parseByte(machineIP.substring(machineIP.length() - 1, machineIP.length()));
		appNameDotUnderlineIP = new StringBuilder(appName).append(LightConstants.DOT).append(machineUnderlineIP)
				.toString();
		underlineIPDotAppName = new StringBuilder(machineUnderlineIP).append(LightConstants.DOT).append(appName)
				.toString();
	}

	public String resolveServicePath(String serviceName) {
		List<String> domainPackageList = lightConf.getDomainPackages();
		if (!domainPackageList.isEmpty()) {
			for (String domainPackage : domainPackageList) {
				if (serviceName.indexOf(domainPackage) == 0) {
					int index = serviceName.indexOf(LightConstants.DOT, domainPackage.length() + 1);
					return serviceName.substring(domainPackage.length() + 1,
							index == -1 ? serviceName.length() : index);
				}
			}
		}
		return serviceName;
	}

	public String getMatchedDomainPackage(String serviceName) {
		List<String> domainPackageList = lightConf.getDomainPackages();
		if (!domainPackageList.isEmpty()) {
			for (String domainPackage : domainPackageList) {
				if (serviceName.indexOf(domainPackage) == 0) {
					return domainPackage;
				}
			}
		}
		return null;
	}

	public int getAppPort() {
		return portConf.getAppPort();
	}

	public LightConf getLightConf() {
		return lightConf;
	}

	public PortConf getPortConf() {
		return portConf;
	}

	public AppConf getAppConf() {
		return appConf;
	}

	public SharedConf getResourceConf() {
		return resourceConf;
	}

	public String getAppName() {
		return appName;
	}

	public String getConfigRegistry() {
		return configRegistry;
	}

	public String getMachineIp() {
		return machineIP;
	}

	public byte getZeroOneGrouping() {
		return zeroOneGrouping;
	}

	public String getAppNameDotUnderlineIP() {
		return appNameDotUnderlineIP;
	}

	public String getUnderlineIPDotAppName() {
		return underlineIPDotAppName;
	}

}