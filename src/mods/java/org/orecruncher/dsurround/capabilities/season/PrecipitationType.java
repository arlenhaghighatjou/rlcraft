package org.orecruncher.dsurround.capabilities.season;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum PrecipitationType {
   NONE,
   DUST,
   RAIN,
   SNOW;

   private PrecipitationType() {
   }
}
