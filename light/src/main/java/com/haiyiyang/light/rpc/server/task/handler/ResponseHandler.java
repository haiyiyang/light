package com.haiyiyang.light.rpc.server.task.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.protocol.ProtocolPacket;
import com.haiyiyang.light.rpc.request.RequestMeta;
import com.haiyiyang.light.serialization.SerializerFactory;
import com.haiyiyang.light.service.LightService;

public class ResponseHandler implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);

	private ProtocolPacket protocolPacket;

	public ResponseHandler(ProtocolPacket protocolPacket) {
		this.protocolPacket = protocolPacket;
	}

	@Override
	public void run() {
		byte serializerType = protocolPacket.getSerializerType();
		if (serializerType == SerializerFactory.SERIALIZER_JSON) {
			List<ByteBuffer> data = protocolPacket.getData();
			RequestMeta requestMeta = RequestMeta.deserialize(data.get(0));

			Object args = null;
			if (data.size() > 1) {
				args = SerializerFactory.getSerializer(protocolPacket.getSerializerType()).deserialize(data.get(1),
						requestMeta.getParamsTypes());
			}
			Method method = null;
			Object response = null;
			Throwable throwable = null;
			Object service = LightService.getLocalBean(requestMeta.getServiceName());
			try {
				method = service.getClass().getMethod(requestMeta.getMethod(), requestMeta.getParamsTypes());
			} catch (NoSuchMethodException e) {
				throwable = e;
				LOGGER.error("No Such Method [{}].", requestMeta.getMethod());
			} catch (SecurityException e) {
				throwable = e;
				LOGGER.error("Calling Method [{}] throws Security Exception.", requestMeta.getMethod());
			}

			if (method != null) {
				try {
					response = method.invoke(service, (Object[]) args);
				} catch (IllegalAccessException e) {
					throwable = e;
					LOGGER.error("Calling Method [{}] throws Illegal Access Exception.", requestMeta.getMethod());
				} catch (IllegalArgumentException e) {
					throwable = e;
					LOGGER.error("Calling Method [{}] throws Illegal Argument Exception.", requestMeta.getMethod());
				} catch (InvocationTargetException e) {
					throwable = e;
					LOGGER.error("Calling Method [{}] throws Invocation Target Exception.", requestMeta.getMethod());
				}
			}
			if (throwable != null) {
				LOGGER.error(throwable.getMessage());
			}
			List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
			buffers.add(ByteBuffer.allocate(1).put(throwable != null ? LightConstants.BYTE0 : LightConstants.BYTE1));
			ByteBuffer responseByteBuffer = SerializerFactory.getSerializer(protocolPacket.getSerializerType())
					.serialize(throwable != null ? new LightException(LightException.Code.INVOKE_ERROR, throwable)
							: response, null);
			buffers.add(responseByteBuffer);
			protocolPacket.setData(buffers);
			protocolPacket.getChContext().writeAndFlush(protocolPacket);
		} else if (serializerType == SerializerFactory.SERIALIZER_PROTOBUF) {

		}
	}
}
