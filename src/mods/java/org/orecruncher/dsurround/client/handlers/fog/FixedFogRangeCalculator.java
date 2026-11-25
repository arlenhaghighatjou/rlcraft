package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FixedFogRangeCalculator extends VanillaFogRangeCalculator {
   protected final FogResult cached;

   public FixedFogRangeCalculator(float minDistance, float maxDistance) {
      this.cached = new FogResult(minDistance, maxDistance);
   }

   @Nonnull
   public String getName() {
      return "FixedFogRangeCalculator";
   }

   @Nonnull
   public FogResult calculate(@Nonnull RenderFogEvent event) {
      return this.cached;
   }
}
