package io.github.xcube16.iseedragons.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Patcher {
   String name() default "";

   String config() default "";

   boolean defaultEnable() default true;
}
