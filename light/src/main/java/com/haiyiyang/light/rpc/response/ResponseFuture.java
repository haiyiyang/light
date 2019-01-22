package com.haiyiyang.light.rpc.response;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.protocol.ProtocolPacket;
import com.haiyiyang.light.serialization.SerializerFactory;

public class ResponseFuture<V> implements Future<V> {

	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();
	private final long startTime = System.currentTimeMillis();

	private long timeout;
	private TimeUnit timeUnit;
	private Object classType = null;
	private boolean returnNull = false;
	private volatile ProtocolPacket packet;

	public ResponseFuture(boolean returnNull) {
		this.returnNull = returnNull;
	}

	public ResponseFuture(Object classType, long timeout, TimeUnit timeUnit) {
		this.classType = classType;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO
		return false;
	}

	@Override
	public boolean isDone() {
		return packet != null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get() throws InterruptedException, ExecutionException {
		return (V) getResponse();
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
		this.timeout = timeout;
		this.timeUnit = timeUnit;
		return (V) getResponse();
	}

	private Object getResponse() {
		if (returnNull) {
			return null;
		}
		if (!isDone()) {
			lock.lock();
			try {
				while (!isDone()) {
					condition.await(timeout, timeUnit);
					if (isDone() || System.currentTimeMillis() - startTime > timeout) {
						break;
					}
				}
			} catch (InterruptedException e) {
				// TODO
			} finally {
				lock.unlock();
			}
			if (!isDone()) {
				// TODO
			}
		}
		return getResult();
	}

	private Object getResult() {
		if (packet != null) {
			List<ByteBuffer> buffer = packet.getData();
			if (buffer == null || buffer.size() == 0) {
				// TODO
			}
			ByteBuffer bf0 = buffer.get(0);
			ByteBuffer result = null;
			if (buffer.size() > 1) {
				result = buffer.get(1);
			}
			if (bf0.get() == LightConstants.BYTE0) {
				// TODO
			} else {
				return SerializerFactory.getSerializer(packet.getSerializerType()).deserialize(result, classType);
			}
		}
		return null;
	}

	public void receiveProtocolPacket(ProtocolPacket packet) {
		this.packet = packet;
		if (condition != null) {
			lock.lock();
			try {
				condition.signal();
			} finally {
				lock.unlock();
			}
		}
	}

}
