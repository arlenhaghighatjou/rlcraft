package com.arlenh7.eaglercraft.netty;

public interface ChannelFutureListener {

	void operationComplete(ChannelPromise future) throws Exception;

	public static final ChannelFutureListener FIRE_EXCEPTION_ON_FAILURE = (future) -> {
		if(!future.isSuccess()) {
			future.channel().pipeline().fireExceptionCaught(future.cause());
		}
	};

	public static final ChannelFutureListener CLOSE = (future) -> future.channel().close();

}
