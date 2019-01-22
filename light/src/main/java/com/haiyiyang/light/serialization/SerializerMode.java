package com.haiyiyang.light.serialization;

import com.haiyiyang.light.constant.LightConstants;

public enum SerializerMode {

	PROTOBUF(LightConstants.BYTE1), JSON(LightConstants.BYTE2);

	private byte value;

	SerializerMode(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return this.value;
	}
}
