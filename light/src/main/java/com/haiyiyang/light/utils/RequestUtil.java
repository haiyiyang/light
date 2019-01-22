package com.haiyiyang.light.utils;

import java.util.concurrent.atomic.AtomicLong;

public class RequestUtil {

	private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<String>();
	private static volatile AtomicLong REQUEST_COUNT = new AtomicLong();

	public static String getRequestUUID() {
		String requestId = java.util.UUID.randomUUID().toString();
		THREAD_LOCAL.set(requestId);
		return requestId;
	}

	public static void setRequestUUID(String requestId) {
		THREAD_LOCAL.set(requestId);
	}

	public static String getThreadLocalUUID() {
		return THREAD_LOCAL.get();
	}

	public static void release() {
		THREAD_LOCAL.remove();
	}

	public static void increment() {
		REQUEST_COUNT.incrementAndGet();
	}

	public static void decrement() {
		REQUEST_COUNT.decrementAndGet();
	}

	public static long getrequestCount() {
		return REQUEST_COUNT.longValue();
	}

}
