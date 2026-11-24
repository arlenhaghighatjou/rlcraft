package com.arlenh7.eaglercraft.netty;

public abstract class SimpleChannelInboundHandler<I> extends ChannelInboundHandlerAdapter {

	private final Class<? super I> inboundType;

	protected SimpleChannelInboundHandler(Class<? super I> inboundType) {
		this.inboundType = inboundType;
	}

	public boolean acceptInboundMessage(Object msg) {
		return inboundType.isInstance(msg);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(acceptInboundMessage(msg)) {
			channelRead0(ctx, (I) msg);
		}else {
			ctx.fireChannelRead(msg);
		}
	}

	protected abstract void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception;

}
