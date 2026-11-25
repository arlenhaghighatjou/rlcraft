package net.ilexiconn.llibrary.server.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** @deprecated */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigEntry {
   String name() default "";

   String comment() default "";

   String category() default "general";

   String minValue() default "";

   String maxValue() default "";

   String[] validValues() default {""};
}
