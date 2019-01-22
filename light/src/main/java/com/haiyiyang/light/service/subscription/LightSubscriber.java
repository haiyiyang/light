package com.haiyiyang.light.service.subscription;


public interface LightSubscriber {

	public String getRegistry();

	public String getPath();

	public void subscribe();

}
