package com.haiyiyang.light.protocol;

public class PacketIdGenerator {
	private static int packetId = 0;

	public synchronized static int getPacketId() {
		if (packetId >= Integer.MAX_VALUE) {
			packetId = 0;
		}
		return packetId++;
	}
}
