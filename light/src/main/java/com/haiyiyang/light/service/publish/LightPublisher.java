package com.haiyiyang.light.service.publish;


public interface LightPublisher {

	public String getRegistry();

	public String getPath();

	public void publish();

}
