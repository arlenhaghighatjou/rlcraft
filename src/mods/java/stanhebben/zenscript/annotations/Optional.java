package stanhebben.zenscript.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Optional {
   Class<?> methodClass() default Optional.class;

   String value() default "";

   String methodName() default "getValue";

   long valueLong() default 0L;

   boolean valueBoolean() default false;

   double valueDouble() default 0.0D;
}
