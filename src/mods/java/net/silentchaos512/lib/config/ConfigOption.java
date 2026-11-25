package net.silentchaos512.lib.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigOption {
   String name();

   String category();

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface BooleanDefault {
      boolean value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface Comment {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface RangeDouble {
      double value();

      double min() default -1.7976931348623157E308D;

      double max() default Double.MAX_VALUE;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface RangeFloat {
      float value();

      float min() default -3.4028235E38F;

      float max() default Float.MAX_VALUE;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface RangeInt {
      int value();

      int min() default Integer.MIN_VALUE;

      int max() default Integer.MAX_VALUE;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface RangeLong {
      long value();

      long min() default Long.MIN_VALUE;

      long max() default Long.MAX_VALUE;
   }
}
