package com.arlenh7.eaglercraft.netty;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class EmbeddedChannel {

	static final Logger logger = LogManager.getLogger("EmbeddedChannel");

	private final ChannelPipeline pipeline;
	private final Map<AttributeKey<?>, Attribute<?>> attributes = new HashMap<>();
	private final Queue<Object> inboundMessages = new ArrayDeque<>();
	private final Queue<Object> outboundMessages = new ArrayDeque<>();
	private boolean open = true;

	public EmbeddedChannel(ChannelHandler... handlers) {
		pipeline = new ChannelPipeline(this);
		pipeline.addLast(handlers);
	}

	public ChannelPipeline pipeline() {
		return pipeline;
	}

	@SuppressWarnings("unchecked")
	public <T> Attribute<T> attr(AttributeKey<T> key) {
		Attribute<?> attr = attributes.get(key);
		if(attr == null) {
			attr = new Attribute<T>();
			attributes.put(key, attr);
		}
		return (Attribute<T>) attr;
	}

	public boolean writeInbound(Object... msgs) {
		for(int i = 0; i < msgs.length; ++i) {
			pipeline.fireChannelRead(msgs[i]);
		}
		return !inboundMessages.isEmpty();
	}

	public boolean writeOutbound(Object... msgs) {
		for(int i = 0; i < msgs.length; ++i) {
			pipeline.write(msgs[i]);
		}
		pipeline.flush();
		return !outboundMessages.isEmpty();
	}

	public ChannelPromise writeAndFlush(Object msg) {
		ChannelPromise ret = pipeline.write(msg);
		pipeline.flush();
		return ret;
	}

	public Queue<Object> inboundMessages() {
		return inboundMessages;
	}

	public Queue<Object> outboundMessages() {
		return outboundMessages;
	}

	@SuppressWarnings("unchecked")
	public <T> T readInbound() {
		return (T) inboundMessages.poll();
	}

	@SuppressWarnings("unchecked")
	public <T> T readOutbound() {
		return (T) outboundMessages.poll();
	}

	public boolean isOpen() {
		return open;
	}

	public void close() {
		open = false;
	}

}
