package com.haiyiyang.light.rpc.server.task.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.context.LightContext;
import com.haiyiyang.light.meta.props.LightProps;
import com.haiyiyang.light.protocol.ProtocolPacket;
import com.haiyiyang.light.rpc.server.task.TaskExecutor;
import com.haiyiyang.light.rpc.server.task.TaskQueue;

public class RequestHandler extends Thread {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

	private LightProps lightProps;

	private static RequestHandler requestHandler;

	private RequestHandler() {
		this.setName("Request Handler Thread.");
		this.lightProps = LightContext.getLightAppMeta().getLightProps();
		LOGGER.info("Starting request handler [RequestHandler] thread.");
	}

	public synchronized static void handle() {
		if (requestHandler == null) {
			requestHandler = new RequestHandler();
			requestHandler.start();
		}
	}

	@Override
	public void run() {
		boolean threadPoolIsExecute = false;
		ProtocolPacket protocolPacket = null;
		while (true) {
			if (!threadPoolIsExecute && protocolPacket != null) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					LOGGER.warn("InterruptedException: {}", e.getMessage());
				}
				threadPoolIsExecute = execute(protocolPacket);
			} else {
				protocolPacket = TaskQueue.SINGLETON().get();
				threadPoolIsExecute = execute(protocolPacket);
			}
		}
	}

	private boolean execute(ProtocolPacket protocolPacket) {
		boolean result = true;
		if (protocolPacket == null) {
			return result;
		}
		if ((System.currentTimeMillis() - protocolPacket.getStartTime()) < lightProps.getTimeout()) {
			return TaskExecutor.SINGLETON(lightProps).execute(new ResponseHandler(protocolPacket));
		} else {
			if (protocolPacket != null) {
				// TODO
			}
		}
		return result;
	}

}
