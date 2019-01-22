package com.haiyiyang.light.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class NetworkUtils {

	private static Set<InetAddress> getInetAddresses() {
		Set<InetAddress> inetAddressSet = new HashSet<InetAddress>();
		Enumeration<NetworkInterface> ns = null;
		try {
			ns = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
		}
		while (ns != null && ns.hasMoreElements()) {
			NetworkInterface n = ns.nextElement();
			Enumeration<InetAddress> inetAddresses = n.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				InetAddress inetAddress = inetAddresses.nextElement();
				if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
						&& !inetAddress.isMulticastAddress() && !isSpecialIp(inetAddress.getHostAddress()))
					inetAddressSet.add(inetAddress);
			}
		}
		return inetAddressSet;
	}

	public static Set<String> getLocalIps() {
		Set<InetAddress> inetAddressSet = getInetAddresses();
		Set<String> IPs = new HashSet<String>(inetAddressSet.size());
		for (InetAddress inetAddress : inetAddressSet)
			IPs.add(inetAddress.getHostAddress());
		return IPs;
	}

	private static boolean isSpecialIp(String ip) {
		if (ip.contains(":") || ip.startsWith("127.") || ip.startsWith("169.254.") || ip.startsWith("255.")) {
			return true;
		}
		return false;
	}
}
