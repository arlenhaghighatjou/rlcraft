package com.arlenh7.eaglercraft.netty;

public final class ChannelHandlerContext {

	final ChannelPipeline pipeline;
	final String name;
	final ChannelHandler handler;
	ChannelHandlerContext prev;
	ChannelHandlerContext next;

	ChannelHandlerContext(ChannelPipeline pipeline, String name, ChannelHandler handler) {
		this.pipeline = pipeline;
		this.name = name;
		this.handler = handler;
	}

	public EmbeddedChannel channel() {
		return pipeline.channel;
	}

	public ChannelPipeline pipeline() {
		return pipeline;
	}

	public String name() {
		return name;
	}

	public ChannelHandler handler() {
		return handler;
	}

	public <T> Attribute<T> attr(AttributeKey<T> key) {
		return pipeline.channel.attr(key);
	}

	public ChannelHandlerContext fireChannelRead(Object msg) {
		ChannelHandlerContext ctx = next;
		while(ctx != null) {
			if(ctx.handler instanceof ChannelInboundHandler) {
				try {
					((ChannelInboundHandler) ctx.handler).channelRead(ctx, msg);
				}catch(Throwable t) {
					ctx.invokeExceptionCaught(t);
				}
				return this;
			}
			ctx = ctx.next;
		}
		pipeline.channel.inboundMessages().add(msg);
		return this;
	}

	public ChannelHandlerContext fireUserEventTriggered(Object evt) {
		ChannelHandlerContext ctx = next;
		while(ctx != null) {
			if(ctx.handler instanceof ChannelInboundHandler) {
				try {
					((ChannelInboundHandler) ctx.handler).userEventTriggered(ctx, evt);
				}catch(Throwable t) {
					ctx.invokeExceptionCaught(t);
				}
				return this;
			}
			ctx = ctx.next;
		}
		return this;
	}

	public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
		if(next != null && next.handler != null) {
			next.invokeExceptionCaught(cause);
		}else {
			EmbeddedChannel.logger.error("Unhandled exception in channel \"{}\"", pipeline.channel, cause);
		}
		return this;
	}

	void invokeExceptionCaught(Throwable cause) {
		try {
			handler.exceptionCaught(this, cause);
		}catch(Throwable t) {
			EmbeddedChannel.logger.error("Exception thrown while handling an exception in channel \"{}\"", pipeline.channel, t);
		}
	}

	public ChannelPromise write(Object msg) {
		return write(msg, new ChannelPromise(pipeline.channel));
	}

	public ChannelPromise write(Object msg, ChannelPromise promise) {
		ChannelHandlerContext ctx = prev;
		while(ctx != null) {
			if(ctx.handler instanceof ChannelOutboundHandler) {
				try {
					((ChannelOutboundHandler) ctx.handler).write(ctx, msg, promise);
				}catch(Throwable t) {
					promise.setFailure(t);
				}
				return promise;
			}
			ctx = ctx.prev;
		}
		pipeline.channel.outboundMessages().add(msg);
		return promise;
	}

	public ChannelHandlerContext flush() {
		ChannelHandlerContext ctx = prev;
		while(ctx != null) {
			if(ctx.handler instanceof ChannelOutboundHandler) {
				try {
					((ChannelOutboundHandler) ctx.handler).flush(ctx);
				}catch(Throwable t) {
					ctx.invokeExceptionCaught(t);
				}
				return this;
			}
			ctx = ctx.prev;
		}
		return this;
	}

	public ChannelPromise writeAndFlush(Object msg) {
		ChannelPromise ret = write(msg);
		flush();
		return ret;
	}

	public ChannelPromise writeAndFlush(Object msg, ChannelPromise promise) {
		write(msg, promise);
		flush();
		return promise;
	}

	public ChannelPromise voidPromise() {
		return new ChannelPromise(pipeline.channel);
	}

	public void close() {
		pipeline.channel.close();
	}

}
