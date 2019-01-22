package com.haiyiyang.light.serialization;

import java.nio.ByteBuffer;

public interface Serializer {

	public ByteBuffer serialize(Object obj, Object type);

	public Object deserialize(ByteBuffer buffer, Object type);

}
