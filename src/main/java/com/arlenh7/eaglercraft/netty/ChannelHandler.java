package com.arlenh7.eaglercraft.netty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface ChannelHandler {

	default void handlerAdded(ChannelHandlerContext ctx) throws Exception {
	}

	default void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.fireExceptionCaught(cause);
	}

	@Documented
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.CLASS)
	public @interface Sharable {
	}

}
