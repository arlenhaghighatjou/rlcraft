package ichttt.mods.firstaid.api.enums;

import javax.annotation.Nonnull;

public enum EnumDebuffSlot {
   HEAD(new EnumPlayerPart[]{EnumPlayerPart.HEAD}),
   BODY(new EnumPlayerPart[]{EnumPlayerPart.BODY}),
   ARMS(new EnumPlayerPart[]{EnumPlayerPart.LEFT_ARM, EnumPlayerPart.RIGHT_ARM}),
   LEGS_AND_FEET(new EnumPlayerPart[]{EnumPlayerPart.LEFT_LEG, EnumPlayerPart.RIGHT_LEG, EnumPlayerPart.LEFT_FOOT, EnumPlayerPart.RIGHT_FOOT});

   @Nonnull
   public final EnumPlayerPart[] playerParts;

   private EnumDebuffSlot(@Nonnull EnumPlayerPart[] playerParts) {
      this.playerParts = playerParts;
   }
}
