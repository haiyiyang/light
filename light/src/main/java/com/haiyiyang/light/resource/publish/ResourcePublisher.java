package com.haiyiyang.light.resource.publish;


public interface ResourcePublisher {

	public String getRegistry();

	public String getPath();

	public void publish();

}
