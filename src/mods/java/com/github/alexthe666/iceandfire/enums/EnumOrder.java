package com.github.alexthe666.iceandfire.enums;

public enum EnumOrder {
   WANDER,
   SIT,
   FOLLOW,
   SLEEP;

   private EnumOrder() {
   }

   public final EnumOrder next() {
      return values()[(this.ordinal() + 1) % values().length];
   }
}
