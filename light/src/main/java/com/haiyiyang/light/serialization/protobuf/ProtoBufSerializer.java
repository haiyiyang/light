package com.haiyiyang.light.serialization.protobuf;

import java.nio.ByteBuffer;

import com.haiyiyang.light.serialization.Serializer;

public class ProtoBufSerializer implements Serializer {

	private static final ProtoBufSerializer PROTOBUF_SERIALIZER = new ProtoBufSerializer();

	private ProtoBufSerializer() {
	}

	public static ProtoBufSerializer SINGLETON() {
		return PROTOBUF_SERIALIZER;
	}

	public ByteBuffer serialize(Object obj, Object classType) {
		return null; // TODO
	}

	public Object deserialize(ByteBuffer buffer, Object classType) {
		return null; // TODO
	}
}