package com.haiyiyang.light.rpc.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.haiyiyang.light.context.LightContext;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.meta.props.LightProps;
import com.haiyiyang.light.protocol.PacketIdGenerator;
import com.haiyiyang.light.protocol.ProtocolPacket;
import com.haiyiyang.light.rpc.client.LightRpcClient;
import com.haiyiyang.light.rpc.request.RequestMeta;
import com.haiyiyang.light.serialization.SerializerFactory;
import com.haiyiyang.light.serialization.SerializerMode;
import com.haiyiyang.light.service.ServerResolver;
import com.haiyiyang.light.service.entry.ServiceEntry;
import com.haiyiyang.light.service.proxy.ProxyMode;
import com.haiyiyang.light.utils.RequestUtil;

import io.netty.channel.Channel;

public class LightInvocationHandler implements InvocationHandler, MethodInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LightInvocationHandler.class);

	private static final Map<InvocationFactor, LightInvocationHandler> INVOCATION_HANDLER = new ConcurrentHashMap<>();

	private Object objectProxy;
	private InvocationFactor invocationFactor;
	private static final String TO_STRING = "toString";
	private LightRpcClient client;
	private ProxyMode proxyMode;

	private LightInvocationHandler(InvocationFactor factor) {
		this.invocationFactor = factor;
		this.client = new LightRpcClient();
		this.proxyMode = ProxyMode.valueOf(getLightProps().getProxyType());
		if (ProxyMode.CGLIB == this.proxyMode) {
			Enhancer en = new Enhancer();
			en.setSuperclass(factor.getClazz());
			en.setCallback(this);
			this.objectProxy = en.create();
		} else {
			this.objectProxy = Proxy.newProxyInstance(LightInvocationHandler.class.getClassLoader(),
					new Class[] { factor.getClazz() }, this);
		}
	}

	private static LightProps getLightProps() {
		return LightContext.getLightAppMeta().getLightProps();
	}

	public static Object getProxyService(InvocationFactor factor) {
		if (!INVOCATION_HANDLER.containsKey(factor)) {
			INVOCATION_HANDLER.putIfAbsent(factor, new LightInvocationHandler(factor));
		}
		return INVOCATION_HANDLER.get(factor).objectProxy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return doInvoke(proxy, method, args, null);
	}

	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		return doInvoke(proxy, method, args, methodProxy);
	}

	private Object doInvoke(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		if (TO_STRING.equals(method.getName())) {
			return proxy.getClass().getName();
		}
		LOGGER.info("Prepare to start calling method [{}].", method.getName());
		Byte group = null;
		if (getLightProps().isOpenGroup()) {
			group = LightContext.getLightAppMeta().getZeroOneGrouping();
		}

		ServiceEntry serviceEntry = ServerResolver.getServer(invocationFactor.getClazz().getName(), group);
		if (serviceEntry == null) {
			throw new LightException(LightException.Code.SERVICE_ERROR, LightException.SERVICE_NOT_FOUND);
		}
		Channel channel = client.getChannel(serviceEntry.getIpPort());
		while (channel == null) {
			try {
				channel = client.connect(serviceEntry.getIpPort());
			} catch (Exception e) {
				LOGGER.error("Getting Channel failed, IP: {}, port: {}.", serviceEntry.getIpPort().getIp(),
						serviceEntry.getIpPort().getPort());
			}
		}

		SerializerMode serializerType = SerializerMode.valueOf(getLightProps().getSerializer());

		RequestMeta message = new RequestMeta();
		String requestId = RequestUtil.getThreadLocalUUID();
		if (requestId != null) {
			message.setRequestId(requestId);
		} else {
			message.setRequestId(RequestUtil.getRequestUUID());
		}
		message.setServiceName(invocationFactor.getClazz().getName());
		message.setMethod(method.getName());
		message.setParamsTypes(method.getParameterTypes());
		message.setClientAppName(LightContext.getLightAppMeta().getAppName());
		message.setClientIP(LightContext.getLightAppMeta().getMachineIp());
		message.setDatetime(String.valueOf(System.currentTimeMillis()));

		List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
		buffers.add(message.serialize());
		if (args != null && args.length > 0) {
			buffers.add(SerializerFactory.getSerializer(serializerType).serialize(args, args.getClass()));
		}
		ProtocolPacket protocolPacket = new ProtocolPacket(PacketIdGenerator.getPacketId(),
				invocationFactor.getInvokeMode(), serializerType.getValue(), System.currentTimeMillis(), buffers);
		return client.sendMessage(protocolPacket, method.getReturnType(), channel);
	}

}
