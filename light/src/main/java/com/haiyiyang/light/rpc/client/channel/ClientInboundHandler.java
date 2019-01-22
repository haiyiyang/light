package com.haiyiyang.light.rpc.client.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.protocol.ProtocolPacket;
import com.haiyiyang.light.rpc.client.LightRpcClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientInboundHandler extends SimpleChannelInboundHandler<ProtocolPacket> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientInboundHandler.class);

	private LightRpcClient lightRpcClient;

	public ClientInboundHandler(LightRpcClient lightRpcClient) {
		this.lightRpcClient = lightRpcClient;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}
		IdleStateEvent e = (IdleStateEvent) evt;
		if (e.state() == IdleState.ALL_IDLE) {
			ctx.close();
		}
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
		// lightRpcClient.disconnect(ctx.channel());
		// TODO
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ProtocolPacket msg) throws Exception {
		msg.setChContext(ctx);
		lightRpcClient.receiveMessage(msg);
		LOGGER.info("Received a protocol packet, packetId: {}.", msg.getPacketId());
	}

}
