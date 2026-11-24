package com.arlenh7.eaglercraft.netty;

public interface ChannelOutboundHandler extends ChannelHandler {

	void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception;

	void flush(ChannelHandlerContext ctx) throws Exception;

}
