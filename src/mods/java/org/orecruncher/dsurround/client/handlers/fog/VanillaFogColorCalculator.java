package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Color;

@SideOnly(Side.CLIENT)
public class VanillaFogColorCalculator implements IFogColorCalculator {
   public VanillaFogColorCalculator() {
   }

   @Nonnull
   public Color calculate(@Nonnull FogColors event) {
      return new Color(event.getRed(), event.getGreen(), event.getBlue());
   }

   public void tick() {
   }
}
