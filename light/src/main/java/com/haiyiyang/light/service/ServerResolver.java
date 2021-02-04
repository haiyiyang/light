package com.haiyiyang.light.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.context.LightContext;
import com.haiyiyang.light.meta.LightAppMeta;
import com.haiyiyang.light.rpc.server.IpPort;
import com.haiyiyang.light.service.entry.ServiceEntry;
import com.haiyiyang.light.utils.LightUtil;

public class ServerResolver {

	private static final Logger LR = LoggerFactory.getLogger(ServerResolver.class);

	private static Map<String, ServiceEntry> LATEST_SERVICE_ENTRY = new ConcurrentHashMap<>();

	public static ServiceEntry getServer(String serviceName, Byte group) {
		if (LightUtil.useLocalConf()) {
			LightAppMeta lightAppMeta = LightContext.getLightAppMeta();
			String appName = lightAppMeta.resolveServicePath(serviceName);
			IpPort designatedIpPort = lightAppMeta.getLightConf().getDesignatedIpPort(appName);
			if (designatedIpPort != null) {
				LR.info("Designated IpPort was found, service name [{}], {}.", serviceName, designatedIpPort);
				return new ServiceEntry(designatedIpPort);
			}
		}
		List<ServiceEntry> list = LightService.subscribeLightService(serviceName);
		if (list == null || list.isEmpty()) {
			LR.error("No ServiceEntry was found, service name [{}].", serviceName);
			return null;
		}
		List<ServiceEntry> serviceEntryList = new ArrayList<>(list.size());
		for (ServiceEntry serviceEntry : list) {
			if (serviceEntry.getServiceNames().contains(serviceName)
					&& (group == null || serviceEntry.getGroup() == group.byteValue())) {
				serviceEntryList.add(serviceEntry);
			}
		}
		if (serviceEntryList == null || serviceEntryList.isEmpty()) {
			LR.error("No ServiceEntry was found for the group [{}], service name [{}].", group, serviceName);
			return null;
		}
		return getNextServiceEntry(serviceName, serviceEntryList);
	}

	private static int GCD(int m, int n) {
		int r;
		while (m % n != 0) {
			r = n;
			n = m % n;
			m = r;
		}
		return n;
	}

	private static int GCD(List<ServiceEntry> serviceEntry) {
		int r = 0, weight = 0;
		Iterator<ServiceEntry> iter = serviceEntry.iterator();
		while (iter.hasNext()) {
			weight = iter.next().getWeight();
			if (weight != 0) {
				r = GCD(r, weight);
			}
		}
		return r;
	}

	private static byte maxWeight(List<ServiceEntry> entryList) {
		byte maxWeight = 0;
		Iterator<ServiceEntry> iter = entryList.iterator();
		while (iter.hasNext()) {
			byte curWeight = iter.next().getWeight();
			if (curWeight > maxWeight)
				maxWeight = curWeight;
		}
		return maxWeight;
	}

	public static ServiceEntry getNextServiceEntry(String serviceName, List<ServiceEntry> entrieList) {
		ServiceEntry latestServiceEntry = LATEST_SERVICE_ENTRY.get(serviceName);
		int index = -1;
		byte currentWeight = 0;
		if (latestServiceEntry != null) {
			index = entrieList.indexOf(latestServiceEntry);
			currentWeight = latestServiceEntry.getWeight();
		}
		int gcd = GCD(entrieList);
		byte maxWeight = maxWeight(entrieList);
		int size = entrieList.size();
		while (true) {
			index = (index + 1) % size;
			if (index == 0) {
				currentWeight -= gcd;
				if (currentWeight <= 0) {
					currentWeight = maxWeight;
					if (currentWeight == 0)
						return null;
				}
			}
			ServiceEntry serviceEntry = entrieList.get(index);
			if (serviceEntry.getWeight() >= currentWeight) {
				LATEST_SERVICE_ENTRY.put(serviceName, new ServiceEntry(serviceEntry.getIpPort(), currentWeight));
				return serviceEntry;
			}
		}
	}
}
