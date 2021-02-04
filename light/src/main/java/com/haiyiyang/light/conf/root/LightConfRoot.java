package com.haiyiyang.light.conf.root;

import java.util.List;

import com.haiyiyang.light.conf.element.AppAddress;
import com.haiyiyang.light.conf.element.ThreadPoolConf;

public class LightConfRoot {

	private List<String> domainList;
	private String registry;
	private int disableGrouping = 0;
	private ThreadPoolConf threadPoolConf;
	private List<AppAddress> appAddressList;

	public List<String> getDomainList() {
		return domainList;
	}

	public void setDomainList(List<String> domainList) {
		this.domainList = domainList;
	}

	public String getRegistry() {
		return registry;
	}

	public void setRegistry(String registry) {
		this.registry = registry;
	}

	public int getDisableGrouping() {
		return disableGrouping;
	}

	public void setDisableGrouping(int disableGrouping) {
		this.disableGrouping = disableGrouping;
	}

	public ThreadPoolConf getThreadPoolConf() {
		return threadPoolConf;
	}

	public void setThreadPoolConf(ThreadPoolConf threadPoolConf) {
		this.threadPoolConf = threadPoolConf;
	}

	public List<AppAddress> getAppAddressList() {
		return appAddressList;
	}

	public void setAppAddressList(List<AppAddress> appAddressList) {
		this.appAddressList = appAddressList;
	}

}
