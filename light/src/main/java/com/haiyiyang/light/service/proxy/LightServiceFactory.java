package com.haiyiyang.light.service.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.rpc.invocation.InvocationFactor;
import com.haiyiyang.light.service.LightService;

public class LightServiceFactory {

	private static final Map<InvocationFactor, Object> CACHED_SERVICES = new ConcurrentHashMap<>();

	public static <T> T getService(Class<T> clazz) throws LightException {
		return getServiceProxy(clazz, LightConstants.BYTE1);
	}

	public static <T> T getAsyncService(Class<T> clazz) throws LightException {
		return getServiceProxy(clazz, LightConstants.BYTE2);
	}

	public static <T> T getAsyncNoResultService(Class<T> clazz) throws LightException {
		return getServiceProxy(clazz, LightConstants.BYTE0);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getServiceProxy(Class<T> clazz, byte invokeMode) throws LightException {
		InvocationFactor factor = new InvocationFactor(clazz, invokeMode);
		if (!CACHED_SERVICES.containsKey(factor)) {
			CACHED_SERVICES.put(factor, LightService.getServiceProxy(factor));
		}
		return (T) CACHED_SERVICES.get(factor);
	}

}
