package slimeknights.mantle.pulsar.pulse;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Pulse {
   String id();

   String description() default "";

   String modsRequired() default "";

   String pulsesRequired() default "";

   boolean forced() default false;

   boolean defaultEnable() default true;
}
