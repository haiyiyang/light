package com.haiyiyang.light.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.context.LightContext;
import com.haiyiyang.light.rpc.server.LightRpcServer;
import com.haiyiyang.light.service.LightService;

public class ShutdownHook extends Thread {

	private static final Logger LR = LoggerFactory.getLogger(ShutdownHook.class);

	private ShutdownHook() {
	}

	private static ShutdownHook SHUTDOWN_HOOK;

	public synchronized static void hook() {
		if (SHUTDOWN_HOOK == null) {
			Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		}
	}

	public void run() {
		try {
			LR.info("The App start shutting down.");
			LightService.doUnpublishLightService();
			LR.info("THe thread sleeps 30 secondes.");
			Thread.sleep(30 * 1000);
			LightRpcServer.SINGLETON().stop();
			LR.info("The netty server has been shut down.");
		} catch (Throwable e) {
			LR.error("The Light App shut down failed, exception: {}", e.getMessage());
		} finally {
			if (LightContext.getContext() != null) {
				LightContext.getContext().close();
				LR.info("The Light Context closed.");
			}
		}
	}
}
