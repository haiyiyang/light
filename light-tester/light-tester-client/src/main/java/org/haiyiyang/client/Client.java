package org.haiyiyang.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.haiyiyang.server.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.rpc.LightRpcContext;
import com.haiyiyang.light.service.proxy.LightServiceFactory;

public class Client {

	private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

	public static void main(String[] args) {
		System.setProperty("useLocalProps", LightConstants.STR1);
		UserService UserService = LightServiceFactory.getService(UserService.class);
		String helloResult = UserService.hello("Agent");
		LOGGER.info("helloResult >>> " + helloResult);

		final UserService asyncUserService = LightServiceFactory.getAsyncService(UserService.class);
		Future<String> welcomeFuture = LightRpcContext.getContext().asyncCall(asyncUserService, new Callable<String>() {
			public String call() throws Exception {
				return asyncUserService.welcome("San", "Zhang");
			}
		});
		String welcomeResult = UserService.welcome("Jack", "Ma");
		LOGGER.info("welcomeResult >>> " + welcomeResult);
		try {
			String asyncWelcomeResult = welcomeFuture.get();
			LOGGER.info("asyncWelcomeResult >>> " + asyncWelcomeResult);
		} catch (InterruptedException e) {
			LOGGER.error("Calling async welcome failed >>> " + e.getMessage());
			e.printStackTrace();
		} catch (ExecutionException e) {
			LOGGER.error("Calling async welcome failed >>> " + e.getMessage());
			e.printStackTrace();
		}
	}
}