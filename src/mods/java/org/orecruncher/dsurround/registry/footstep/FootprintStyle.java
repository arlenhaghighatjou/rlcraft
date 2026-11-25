package org.orecruncher.dsurround.registry.footstep;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum FootprintStyle {
   SHOE,
   SQUARE,
   HORSESHOE,
   BIRD,
   PAW,
   SQUARE_SOLID,
   LOWRES_SQUARE;

   private FootprintStyle() {
   }

   @Nonnull
   public static FootprintStyle getStyle(int v) {
      return v >= values().length ? LOWRES_SQUARE : values()[v];
   }
}
