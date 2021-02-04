package org.haiyiyang.server;

import com.haiyiyang.light.context.LightContext;

public class Server {

	public static void main(String[] args) {
		System.setProperty("useLocalProps", "1");
		LightContext.buildContext("server", new String[] { "org.haiyiyang" }).start();
	}
}
