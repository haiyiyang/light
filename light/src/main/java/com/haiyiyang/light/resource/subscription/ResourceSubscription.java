package com.haiyiyang.light.resource.subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.registry.RegistryConnection;

public class ResourceSubscription extends RegistryConnection {

	private static final Logger LR = LoggerFactory.getLogger(ResourceSubscription.class);

	private ResourceSubscriber lightSubscriber;
	private static final Map<ResourceSubscriber, ResourceSubscription> SUBSCRIPTIONS = Maps.newConcurrentMap();

	private ResourceSubscription(ResourceSubscriber subscriber) {
		super(subscriber.getRegistry());
		this.lightSubscriber = subscriber;
		SUBSCRIPTIONS.put(this.lightSubscriber, this);
	}

	public static ResourceSubscription getSubscription(ResourceSubscriber subscriber) {
		if (SUBSCRIPTIONS.containsKey(subscriber)) {
			return SUBSCRIPTIONS.get(subscriber);
		}
		synchronized (subscriber) {
			if (SUBSCRIPTIONS.containsKey(subscriber)) {
				return SUBSCRIPTIONS.get(subscriber);
			} else {
				setRegistryLevelLock(subscriber.getRegistry());
				return new ResourceSubscription(subscriber);
			}
		}
	}

	public byte[] getData(String path) {
		return getData(path, this);
	}

	public List<byte[]> getChildrenData(String path) {
		List<String> childrenPaths = getChildren(path, this);
		if (childrenPaths != null && !childrenPaths.isEmpty()) {
			int length = path.length();
			StringBuilder pathStrb = new StringBuilder(length + 16).append(path);
			List<byte[]> result = new ArrayList<>(childrenPaths.size());
			for (String childrenPath : childrenPaths) {
				pathStrb.delete(length, pathStrb.length());
				result.add(getData(pathStrb.append(LightConstants.SLASH).append(childrenPath).toString()));
			}
			return result;
		}
		return Collections.emptyList();
	}

	@Override
	public void doProcess(boolean sessionExpired, WatchedEvent event) {
		LR.info("Received [WatchedEvent], sessionExpired: {}, event: {}.", sessionExpired, event);
		lightSubscriber.subscribe();
	}

}
