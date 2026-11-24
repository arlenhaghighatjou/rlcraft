package com.arlenh7.eaglercraft.netty;

import java.util.ArrayList;
import java.util.List;

public abstract class MessageToMessageCodec<INBOUND_IN, OUTBOUND_IN> implements ChannelInboundHandler, ChannelOutboundHandler {

	private final Class<? super INBOUND_IN> inboundType;
	private final Class<? super OUTBOUND_IN> outboundType;

	protected MessageToMessageCodec(Class<? super INBOUND_IN> inboundType, Class<? super OUTBOUND_IN> outboundType) {
		this.inboundType = inboundType;
		this.outboundType = outboundType;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(!inboundType.isInstance(msg)) {
			ctx.fireChannelRead(msg);
			return;
		}
		List<Object> out = new ArrayList<>(1);
		decode(ctx, (INBOUND_IN) msg, out);
		for(int i = 0, l = out.size(); i < l; ++i) {
			ctx.fireChannelRead(out.get(i));
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		ctx.fireUserEventTriggered(evt);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if(!outboundType.isInstance(msg)) {
			ctx.write(msg, promise);
			return;
		}
		List<Object> out = new ArrayList<>(1);
		encode(ctx, (OUTBOUND_IN) msg, out);
		for(int i = 0, l = out.size(); i < l; ++i) {
			ctx.write(out.get(i), promise);
		}
	}

	@Override
	public void flush(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	protected abstract void encode(ChannelHandlerContext ctx, OUTBOUND_IN msg, List<Object> out) throws Exception;

	protected abstract void decode(ChannelHandlerContext ctx, INBOUND_IN msg, List<Object> out) throws Exception;

}
