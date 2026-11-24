package com.arlenh7.eaglercraft.netty;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ChannelPipeline implements Iterable<Map.Entry<String, ChannelHandler>> {

	final EmbeddedChannel channel;
	private final ChannelHandlerContext head;
	private final ChannelHandlerContext tail;

	ChannelPipeline(EmbeddedChannel channel) {
		this.channel = channel;
		head = new ChannelHandlerContext(this, "head", null);
		tail = new ChannelHandlerContext(this, "tail", null);
		head.next = tail;
		tail.prev = head;
	}

	private void insertAfter(ChannelHandlerContext base, String name, ChannelHandler handler) {
		ChannelHandlerContext ctx = new ChannelHandlerContext(this, name, handler);
		ctx.prev = base;
		ctx.next = base.next;
		base.next.prev = ctx;
		base.next = ctx;
		try {
			handler.handlerAdded(ctx);
		}catch(Throwable t) {
			ctx.invokeExceptionCaught(t);
		}
	}

	private ChannelHandlerContext find(String name) {
		ChannelHandlerContext ctx = head.next;
		while(ctx != tail) {
			if(ctx.name.equals(name)) {
				return ctx;
			}
			ctx = ctx.next;
		}
		return null;
	}

	public ChannelPipeline addFirst(String name, ChannelHandler handler) {
		insertAfter(head, name, handler);
		return this;
	}

	public ChannelPipeline addLast(String name, ChannelHandler handler) {
		insertAfter(tail.prev, name, handler);
		return this;
	}

	public ChannelPipeline addLast(ChannelHandler... handlers) {
		for(int i = 0; i < handlers.length; ++i) {
			addLast(handlers[i].getClass().getSimpleName() + "#" + i, handlers[i]);
		}
		return this;
	}

	public ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
		ChannelHandlerContext base = find(baseName);
		if(base == null) {
			throw new IllegalArgumentException("Handler not found: " + baseName);
		}
		insertAfter(base, name, handler);
		return this;
	}

	public ChannelHandler get(String name) {
		ChannelHandlerContext ctx = find(name);
		return ctx != null ? ctx.handler : null;
	}

	public ChannelHandlerContext context(String name) {
		return find(name);
	}

	public ChannelPipeline remove(String name) {
		ChannelHandlerContext ctx = find(name);
		if(ctx != null) {
			ctx.prev.next = ctx.next;
			ctx.next.prev = ctx.prev;
		}
		return this;
	}

	public ChannelPipeline fireChannelRead(Object msg) {
		head.fireChannelRead(msg);
		return this;
	}

	public ChannelPipeline fireChannelActive() {
		head.fireChannelActive();
		return this;
	}

	public ChannelPipeline fireUserEventTriggered(Object evt) {
		head.fireUserEventTriggered(evt);
		return this;
	}

	public ChannelPipeline fireExceptionCaught(Throwable cause) {
		head.fireExceptionCaught(cause);
		return this;
	}

	public ChannelPromise write(Object msg) {
		return tail.write(msg);
	}

	public ChannelPipeline flush() {
		tail.flush();
		return this;
	}

	@Override
	public Iterator<Map.Entry<String, ChannelHandler>> iterator() {
		List<Map.Entry<String, ChannelHandler>> ret = new ArrayList<>();
		ChannelHandlerContext ctx = head.next;
		while(ctx != tail) {
			ret.add(new AbstractMap.SimpleImmutableEntry<>(ctx.name, ctx.handler));
			ctx = ctx.next;
		}
		return ret.iterator();
	}

}
