package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VanillaFogRangeCalculator implements IFogRangeCalculator {
   public VanillaFogRangeCalculator() {
   }

   @Nonnull
   public String getName() {
      return "VanillaFogRangeCalculator";
   }

   @Nonnull
   public FogResult calculate(@Nonnull RenderFogEvent event) {
      return new FogResult(event);
   }

   public void tick() {
   }
}
