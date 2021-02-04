package com.haiyiyang.light.protocol.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.protocol.ProtocolPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

public class ProtocolDecoder extends ByteToMessageDecoder {

	private static final Logger LR = LoggerFactory.getLogger(ProtocolDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < ProtocolPacket.HEADER_LENGTH) {
			return;
		}
		in.markReaderIndex();
		if (in.readByte() != LightConstants.PROTOCOL_MAGIC_NUMBER) {
			LR.error("Invalid protocol magic number.");
			throw new CorruptedFrameException("Invalid protocol magic number.");
		}
		int dataLength = in.readInt();
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			LR.error("The length of readable bytes is less than specified length.");
			return;
		}
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		ProtocolPacket packet = ProtocolPacket.decode(data);
		out.add(packet);
		LR.info("Decoded the protocol packet, packetId: {}.", packet.getPacketId());
	}
}
