package reborncore.common.registration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import reborncore.Distribution;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RebornRegistry {
   String modID() default "reborncore";

   int priority() default 0;

   boolean earlyReg() default false;

   Distribution side() default Distribution.UNIVERSAL;

   String modOnly() default "";
}
