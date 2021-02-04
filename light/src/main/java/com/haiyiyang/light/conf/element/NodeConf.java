package com.haiyiyang.light.conf.element;

public class NodeConf {
	private String ip;
	private int loadWeight;
	private String registry;
	private int disablePublish = 0;
	private ThreadPoolConf threadPoolConf;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getLoadWeight() {
		return loadWeight;
	}

	public void setLoadWeight(int loadWeight) {
		this.loadWeight = loadWeight;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public int getDisablePublish() {
		return disablePublish;
	}

	public void setDisablePublish(int disablePublish) {
		this.disablePublish = disablePublish;
	}

	public ThreadPoolConf getThreadPoolConf() {
		return threadPoolConf;
	}

	public void setThreadPoolConf(ThreadPoolConf threadPoolConf) {
		this.threadPoolConf = threadPoolConf;
	}

}
