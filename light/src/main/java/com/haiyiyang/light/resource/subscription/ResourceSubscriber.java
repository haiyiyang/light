package com.haiyiyang.light.resource.subscription;


public interface ResourceSubscriber {

	public String getRegistry();

	public String getPath();

	public void subscribe();

}
