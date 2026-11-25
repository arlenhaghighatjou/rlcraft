package org.orecruncher.dsurround.registry.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum SoundType {
   BACKGROUND("BACKGROUND"),
   SPOT("SPOT"),
   PERIODIC("PERIODIC");

   protected final String name;

   private SoundType(@Nonnull String name) {
      this.name = name;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public static SoundType getType(@Nullable String soundType) {
      if (soundType == null) {
         return BACKGROUND;
      } else {
         try {
            return valueOf(soundType.toUpperCase());
         } catch (Throwable var2) {
            var2.printStackTrace();
            return BACKGROUND;
         }
      }
   }
}
