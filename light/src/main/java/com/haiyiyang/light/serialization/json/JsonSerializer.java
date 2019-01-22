package com.haiyiyang.light.serialization.json;

import java.nio.ByteBuffer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.haiyiyang.light.serialization.Serializer;
import com.haiyiyang.light.serialization.SerializerFactory;

public class JsonSerializer implements Serializer {

	private static final JsonSerializer JSON_SERIALIZER = new JsonSerializer();

	private JsonSerializer() {
	}

	public static JsonSerializer SINGLETON() {
		return JSON_SERIALIZER;
	}

	public ByteBuffer serialize(Object obj, Object classType) {
		return ByteBuffer.wrap(JSON.toJSONBytes(obj, SerializerFeature.WriteClassName));
	}

	public Object deserialize(ByteBuffer buffer, Object classType) {
		if (classType instanceof Class) {
			return JSON.parseObject(buffer.array(), (Class<?>) classType);
		} else {
			Class<?>[] classArray = (Class<?>[]) classType;
			if (classArray.length == 1) {
				return JSON.parseArray(SerializerFactory.getString(buffer.array()), classArray[0]).toArray();
			} else {
				return JSON.parseArray(SerializerFactory.getString(buffer.array()), classArray).toArray();
			}
		}
	}

}