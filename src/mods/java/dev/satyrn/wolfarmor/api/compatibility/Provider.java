package dev.satyrn.wolfarmor.api.compatibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Provider {
   String value();

   short priority() default -32768;

   public static class Priority {
      public static final short HIGHEST = 32767;
      public static final short LOWEST = -32768;

      private Priority() {
      }
   }

   public abstract static class ProviderType {
      public ProviderType() {
      }

      @Nonnull
      public final String getModId() {
         return this.getClass().isAnnotationPresent(Provider.class) ? ((Provider)this.getClass().getAnnotation(Provider.class)).value() : "";
      }

      public final short getPriority() {
         return this.getClass().isAnnotationPresent(Provider.class) ? ((Provider)this.getClass().getAnnotation(Provider.class)).priority() : -32768;
      }
   }
}
