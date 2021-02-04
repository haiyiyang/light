package com.haiyiyang.light.rpc.server.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.protocol.ProtocolPacket;

public class TaskQueue {

	private static final Logger LR = LoggerFactory.getLogger(TaskQueue.class);

	private static volatile TaskQueue TASK_QUEUE = new TaskQueue();

	private BlockingQueue<ProtocolPacket> BLOCKING_QUEUE = new LinkedBlockingQueue<ProtocolPacket>(100);

	private TaskQueue() {
	}

	public static TaskQueue SINGLETON() {
		return TASK_QUEUE;
	}

	public boolean add(ProtocolPacket protocolPacket) {
		return BLOCKING_QUEUE.offer(protocolPacket);

	}

	public ProtocolPacket get() {
		try {
			return BLOCKING_QUEUE.take();
		} catch (InterruptedException e) {
			LR.error(e.getMessage());
		}
		return null;
	}

	public int getSize() {
		return BLOCKING_QUEUE.size();
	}
}