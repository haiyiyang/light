package com.haiyiyang.light.service.proxy;

import com.haiyiyang.light.constant.LightConstants;

public enum ProxyMode {

	JDK(LightConstants.BYTE1), CGLIB(LightConstants.BYTE2);

	private byte value;

	ProxyMode(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return this.value;
	}

}
