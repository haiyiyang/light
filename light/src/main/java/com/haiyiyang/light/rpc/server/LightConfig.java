package com.haiyiyang.light.rpc.server;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.constant.LightConstants;

import jodd.http.HttpRequest;

public class LightConfig {

	private static final Logger LR = LoggerFactory.getLogger(LightConfig.class);
	private static final String CONFIG_SERVER_URL = "http://light-host/config/registry";

	public static String getConfigRegistry() {
		try {
			return HttpRequest.get(CONFIG_SERVER_URL).send().body().toString();
		} catch (Exception e) {
			LR.error(e.getMessage());
			return LightConstants.ZK_DEFAULT_ADDRESS;
		}
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {
		System.out.println(getConfigRegistry());
	}

}
