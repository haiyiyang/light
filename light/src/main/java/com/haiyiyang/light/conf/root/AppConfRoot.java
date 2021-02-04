package com.haiyiyang.light.conf.root;

import java.util.List;

import com.haiyiyang.light.conf.element.NodeConf;
import com.haiyiyang.light.conf.element.ThreadPoolConf;

public class AppConfRoot {

	private String registry;
	private int disablePublish = 0;
	private ThreadPoolConf threadPoolConf;
	private List<NodeConf> nodeConfList;

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

	public List<NodeConf> getNodeConfList() {
		return nodeConfList;
	}

	public void setNodeConfList(List<NodeConf> nodeConfList) {
		this.nodeConfList = nodeConfList;
	}

}
