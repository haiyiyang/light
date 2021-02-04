package com.haiyiyang.light.protocol.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.protocol.ProtocolPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtocolEncoder extends MessageToByteEncoder<ProtocolPacket> {

	private static final Logger LR = LoggerFactory.getLogger(ProtocolEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, ProtocolPacket msg, ByteBuf out) throws Exception {
		byte[] data = msg.encode().array();
		out.writeByte(LightConstants.PROTOCOL_MAGIC_NUMBER).writeInt(data.length).writeBytes(data);
		LR.info("Encoded the protocol packet, packetId: {}.", msg.getPacketId());
	}
}
