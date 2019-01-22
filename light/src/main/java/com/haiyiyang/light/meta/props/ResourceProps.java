package com.haiyiyang.light.meta.props;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.context.LightContext;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.meta.ResourceEnum;
import com.haiyiyang.light.service.subscription.LightSubscriber;
import com.haiyiyang.light.service.subscription.LightSubscription;
import com.haiyiyang.light.utils.LightUtils;

import jodd.props.Props;

public class ResourceProps implements LightSubscriber {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceProps.class);

	public static final String RESOURCE_PATH = "/light/resource/";
	public static final String RESOURCE_LOCAL_PATH = LightUtils.getLocalPath(RESOURCE_PATH);
	private static Map<ResourceEnum, ResourceProps> RESOURCES_PROPS = new ConcurrentHashMap<>();

	private String path;
	private Props props;

	private ResourceProps(String path, Props props) {
		this.path = path;
		this.props = props;
	}

	public static void publishResourceProps(Map<String, String> resourcesMap) {
		if (resourcesMap != null && !resourcesMap.isEmpty()) {
			Entry<String, String> entry;
			ResourceEnum resourceEnum;
			StringBuilder fullPath = new StringBuilder(64);
			fullPath.append(LightUtils.useLocalProps() ? RESOURCE_LOCAL_PATH : RESOURCE_PATH);
			int length = fullPath.length();
			for (Iterator<Entry<String, String>> ite = resourcesMap.entrySet().iterator(); ite.hasNext();) {
				entry = ite.next();
				fullPath.delete(length, fullPath.length());
				fullPath.append(entry.getValue());
				resourceEnum = ResourceEnum.valueOf(entry.getKey());
				ResourceProps resourceProps = RESOURCES_PROPS.get(resourceEnum);
				if (resourceProps == null) {
					resourceProps = new ResourceProps(fullPath.toString(), new Props());
					RESOURCES_PROPS.put(resourceEnum, resourceProps);
				}
				resourceProps.doPublishResourceProps();
			}
		}
	}

	private void doPublishResourceProps() {
		if (LightUtils.useLocalProps()) {
			File file = new File(this.path);
			if (!file.isFile()) {
				LOGGER.error("The file [{}] does not exists.", this.path);
				throw new LightException(LightException.FILE_NOT_FOUND);
			}
			try {
				this.props.load(file);
			} catch (Exception ex) {
				LOGGER.error("Loading file [{}] failed.", this.path);
				throw new LightException(LightException.LOADING_FILE_FAILED);
			}
		} else {
			doSubscribeResourceProps();
		}
	}

	private void doSubscribeResourceProps() {
		byte[] data = LightSubscription.getSubscription(this).getData(this.path);
		if (data == null || data.length == 0) {
			LOGGER.error("The file [{}] does not exists, or is empty.", this.path);
			throw new LightException(LightException.FILE_NOT_FOUND_OR_EMPTY);
		}
		try {
			Props temp = new Props();
			temp.load(new ByteArrayInputStream(data));
			this.props = temp;
		} catch (IOException e) {
			LOGGER.error("Loading file [{}] failed.", this.path);
			throw new LightException(LightException.LOADING_FILE_FAILED);
		}
	}

	private String getValue(String key) {
		return this.props.getValue(key);
	}

	private String getValue(String key, String profile) {
		return this.props.getValue(key, profile);
	}

	public String getPropsValue(ResourceEnum resourceEnum, String key) {
		if (RESOURCES_PROPS.get(resourceEnum) != null) {
			return RESOURCES_PROPS.get(resourceEnum).getValue(key);
		}
		return null;
	}

	public String getPropsValue(ResourceEnum resourceEnum, String key, String profile) {
		if (RESOURCES_PROPS.get(resourceEnum) != null) {
			return RESOURCES_PROPS.get(resourceEnum).getValue(key, profile);
		}
		return null;
	}

	@Override
	public String getRegistry() {
		return LightContext.getLightAppMeta().getConfigRegistry();
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public void subscribe() {
		doSubscribeResourceProps();
		LOGGER.info("Reloaded file [{}].", getPath());
	}
}
