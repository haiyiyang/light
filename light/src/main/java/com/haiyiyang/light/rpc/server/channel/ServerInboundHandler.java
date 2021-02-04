package com.haiyiyang.light.rpc.server.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.protocol.ProtocolPacket;
import com.haiyiyang.light.rpc.server.task.TaskQueue;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class ServerInboundHandler extends SimpleChannelInboundHandler<ProtocolPacket> {

	private static final Logger LR = LoggerFactory.getLogger(ServerInboundHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ProtocolPacket msg) throws Exception {
		LR.info("Received a protocol packet, packetId: {}.", msg.getPacketId());
		msg.setChContext(ctx);
		TaskQueue.SINGLETON().add(msg);
	}

}
