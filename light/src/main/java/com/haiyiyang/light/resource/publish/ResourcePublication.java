package com.haiyiyang.light.resource.publish;

import java.util.Map;

import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.haiyiyang.light.registry.RegistryConnection;
import com.haiyiyang.light.service.LightService;

public class ResourcePublication extends RegistryConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourcePublication.class);

	private ResourcePublisher lightPublisher;
	private static final Map<ResourcePublisher, ResourcePublication> PUBLICATIONS = Maps.newConcurrentMap();

	private ResourcePublication(ResourcePublisher publisher) {
		super(publisher.getRegistry());
		this.lightPublisher = publisher;
		PUBLICATIONS.put(this.lightPublisher, this);
	}

	public static ResourcePublication getPublish(ResourcePublisher publisher) {
		if (PUBLICATIONS.containsKey(publisher)) {
			return PUBLICATIONS.get(publisher);
		}
		synchronized (publisher) {
			if (PUBLICATIONS.containsKey(publisher)) {
				return PUBLICATIONS.get(publisher);
			} else {
				setRegistryLevelLock(publisher.getRegistry());
				return new ResourcePublication(publisher);
			}
		}
	}

	public void publishService(String path, byte[] data) {
		createServicePath(path, data);
	}

	public void unpublishService(String path) {
		deleteServicePath(path);
	}

	@Override
	public void doProcess(boolean sessionExpired, WatchedEvent event) {
		LOGGER.info("Received [WatchedEvent], sessionExpired: {}, event: {}.", sessionExpired, event);
		LightService.doPublishLightService();
	}

}
