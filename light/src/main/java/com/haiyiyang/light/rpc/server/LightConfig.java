package com.haiyiyang.light.rpc.server;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.exception.LightException;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

public class LightConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(LightConfig.class);
	private static final String CONFIG_SERVER_URL = "http://config.haiyiyang.com";

	public static String getRegistry() {
		/** CharSet ISO 8859-1 */
		return HttpRequest.get(CONFIG_SERVER_URL).send().toString();
	}

	public static String getConfigRegistry() throws LightException {
		/** test switch --start */
		if (String.valueOf(1 + 1).equals("2")) {
			return "127.0.0.1:2181";
		}
		/** test switch --end */
		String result = null;
		CloseableHttpResponse response = null;
		try {
			response = HttpClients.createDefault().execute(new HttpGet(CONFIG_SERVER_URL));
		} catch (IOException e) {
			LOGGER.error("Light config server is not available: {}", CONFIG_SERVER_URL);
		}
		if (response != null) {
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream is = entity.getContent();
					try {
						StringBuffer out = new StringBuffer();
						byte[] b = new byte[4096];
						for (int n; (n = is.read(b)) != -1;) {
							out.append(new String(b, 0, n));
						}
						result = out.toString();
					} finally {
						is.close();
					}
				}
			} catch (UnsupportedOperationException | IOException e) {
				LOGGER.error("Parse Light config server URL error.");
			} finally {
				try {
					response.close();
				} catch (IOException e) {
					LOGGER.error("Close Http Response error.");
				}
			}
		}
		if (result == null) {
			throw new LightException(LightException.Code.UNDEFINED, LightException.INVALID_CONFIG_SERVER_URL);
		}
		LOGGER.info("Light config server URL: {}", result);
		return result;
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {
		String config = getConfigRegistry();
		System.out.println(config);

		HttpRequest httpRequest = HttpRequest.get("http://www.baidu.com");
		HttpResponse response = httpRequest.send();
		System.out.println(response);
		System.out.println(response.body());
	}

}
