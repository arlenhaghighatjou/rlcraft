package org.orecruncher.dsurround.capabilities.season;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum TemperatureRating {
   ICY("icy", 0.0F),
   COOL("cool", 0.2F),
   MILD("mild", 1.0F),
   WARM("warm", 1.8F),
   HOT("hot", 100.0F);

   private final String val;
   private final float tempRange;

   private TemperatureRating(@Nonnull String val, float tempRange) {
      this.val = val;
      this.tempRange = tempRange;
   }

   @Nonnull
   public String getValue() {
      return this.val;
   }

   public float getTempRange() {
      return this.tempRange;
   }

   @Nonnull
   public static TemperatureRating fromTemp(float temp) {
      for(TemperatureRating rating : values()) {
         if (temp < rating.getTempRange()) {
            return rating;
         }
      }

      return MILD;
   }
}
