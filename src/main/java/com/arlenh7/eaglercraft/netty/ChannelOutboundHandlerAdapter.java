package com.arlenh7.eaglercraft.netty;

public class ChannelOutboundHandlerAdapter implements ChannelOutboundHandler {

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		ctx.write(msg, promise);
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

}
