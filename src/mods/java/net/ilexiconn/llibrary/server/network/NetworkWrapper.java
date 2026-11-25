package net.ilexiconn.llibrary.server.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface NetworkWrapper {
   Class<? extends AbstractMessage<?>>[] value() default {};
}
