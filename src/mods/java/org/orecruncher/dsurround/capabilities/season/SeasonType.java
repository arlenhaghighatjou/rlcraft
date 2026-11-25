package org.orecruncher.dsurround.capabilities.season;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum SeasonType {
   NONE("noseason"),
   SPRING("spring"),
   SUMMER("summer"),
   AUTUMN("autumn"),
   WINTER("winter");

   private final String val;

   private SeasonType(@Nonnull String val) {
      this.val = val;
   }

   @Nonnull
   public String getValue() {
      return this.val;
   }

   public static enum SubType {
      NONE("nosubtype"),
      EARLY("early"),
      MID("mid"),
      LATE("late");

      private final String val;

      private SubType(@Nonnull String val) {
         this.val = val;
      }

      @Nonnull
      public String getValue() {
         return this.val;
      }
   }
}
