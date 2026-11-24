package com.arlenh7.eaglercraft.netty;

public class ChannelPromise {

	private final EmbeddedChannel channel;
	private Throwable cause;

	public ChannelPromise(EmbeddedChannel channel) {
		this.channel = channel;
	}

	public EmbeddedChannel channel() {
		return channel;
	}

	public boolean isSuccess() {
		return cause == null;
	}

	public Throwable cause() {
		return cause;
	}

	public ChannelPromise setFailure(Throwable cause) {
		this.cause = cause;
		return this;
	}

	public ChannelPromise addListener(ChannelFutureListener listener) {
		try {
			listener.operationComplete(this);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

}
