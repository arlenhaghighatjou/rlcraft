package com.charles445.rltweaker.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
public @interface RLConfig {
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface ImprovementsOnly {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface MapSignature {
      String key();

      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface RLCraftTwoEightTwo {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface RLCraftTwoNine {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   public @interface SpecialSignature {
      SpecialEnum value();

      String pass();

      String fail();
   }
}
