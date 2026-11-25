package com.teamacronymcoders.base.blocks.properties;

import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.util.IStringSerializable;

public enum SideType implements IStringSerializable {
   INPUT,
   OUTPUT,
   NONE;

   public static final SideType[] VALUES = values();

   private SideType() {
   }

   @Nonnull
   public String getName() {
      return this.name().toLowerCase(Locale.US);
   }

   public SideType next() {
      int next = this.ordinal() + 1;
      if (next >= VALUES.length) {
         next = 0;
      }

      return VALUES[next];
   }
}
