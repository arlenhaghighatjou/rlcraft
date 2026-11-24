package com.arlenh7.eaglercraft.netty;

public interface ChannelInboundHandler extends ChannelHandler {

	void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

	void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;

}
