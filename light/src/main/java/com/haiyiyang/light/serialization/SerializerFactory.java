package com.haiyiyang.light.serialization;

import java.util.HashMap;
import java.util.Map;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.serialization.json.JsonSerializer;
import com.haiyiyang.light.serialization.protobuf.ProtoBufSerializer;

public class SerializerFactory {

	public static final byte SERIALIZER_PROTOBUF = LightConstants.BYTE1;
	public static final byte SERIALIZER_JSON = LightConstants.BYTE2;

	private static Map<Byte, Serializer> SERIALIZER = new HashMap<Byte, Serializer>();

	static {
		SERIALIZER.put(SERIALIZER_PROTOBUF, ProtoBufSerializer.SINGLETON());
		SERIALIZER.put(SERIALIZER_JSON, JsonSerializer.SINGLETON());
	}

	public static Serializer getSerializer(byte type) {
		return SERIALIZER.get(type);
	}

	public static Serializer getSerializer(SerializerMode serializerMode) {
		return SERIALIZER.get(serializerMode.getValue());
	}

	public static byte[] getBytes(String data) {
		return data.getBytes(LightConstants.CHARSET_UTF8);
	}

	public static String getString(byte[] data) {
		return new String(data, LightConstants.CHARSET_UTF8);
	}

}