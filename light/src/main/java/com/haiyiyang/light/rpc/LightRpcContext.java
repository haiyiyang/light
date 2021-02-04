package com.haiyiyang.light.rpc;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.haiyiyang.light.rpc.response.ResponseFuture;
import com.haiyiyang.light.service.LightService;

public class LightRpcContext {

	private static final Logger LR = LoggerFactory.getLogger(LightRpcContext.class);

	private Integer packetId;

	private static LoadingCache<Integer, Future<?>> FUTURE_CACHE = CacheBuilder.newBuilder().maximumSize(1024)
			.expireAfterWrite(60, TimeUnit.SECONDS).build(new CacheLoader<Integer, Future<?>>() {
				@Override
				public Future<?> load(Integer key) throws Exception {
					return new ResponseFuture<>(true);
				}
			});

	private static final ThreadLocal<LightRpcContext> THREAD_LOCAL = new ThreadLocal<LightRpcContext>() {
		@Override
		protected LightRpcContext initialValue() {
			return new LightRpcContext();
		}
	};

	public static LightRpcContext getContext() {
		return THREAD_LOCAL.get();
	}

	@SuppressWarnings("unchecked")
	public static <T> Future<T> getFuture(Integer packetId) {
		return (Future<T>) FUTURE_CACHE.getUnchecked(packetId);
	}

	@SuppressWarnings("unchecked")
	public static <T> ResponseFuture<T> getResponseFuture(Integer packetId) {
		return (ResponseFuture<T>) FUTURE_CACHE.getUnchecked(packetId);
	}

	@SuppressWarnings("unchecked")
	private static <T> Future<T> getCurrentFuture() {
		return (Future<T>) FUTURE_CACHE.getUnchecked(THREAD_LOCAL.get().packetId);
	}

	public static void setResponseFuture(Integer packetId, ResponseFuture<?> future) {
		THREAD_LOCAL.get().packetId = packetId;
		FUTURE_CACHE.put(THREAD_LOCAL.get().packetId, future);
	}

	public <T> Future<T> asyncCall(Object service, Callable<T> callable) {
		try {
			if (LightService.isLocalService(service)) {
				FutureTask<T> futureTask = new FutureTask<T>(callable);
				futureTask.run();
				return futureTask;
			} else {
				callable.call();
				return getCurrentFuture();
			}
		} catch (Exception e) {
			LR.error(e.getMessage());
		}
		return null;
	}

}
