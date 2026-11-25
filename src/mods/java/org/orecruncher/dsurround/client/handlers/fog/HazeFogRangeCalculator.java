package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfo;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;

@SideOnly(Side.CLIENT)
public class HazeFogRangeCalculator extends VanillaFogRangeCalculator {
   protected static final int BAND_OFFSETS = 15;
   protected static final int BAND_CORE_SIZE = 10;
   protected static final float IMPACT_FAR = 0.6F;
   protected static final float IMPACT_NEAR = 0.95F;
   protected final FogResult cached = new FogResult();

   public HazeFogRangeCalculator() {
   }

   @Nonnull
   public String getName() {
      return "HazeFogRangeCalculator";
   }

   @Nonnull
   public FogResult calculate(@Nonnull RenderFogEvent event) {
      IDimensionInfo di = EnvironStateHandler.EnvironState.getDimensionInfo();
      if (di.hasHaze()) {
         float lowY = (float)(di.getCloudHeight() - 15);
         float highY = (float)(di.getCloudHeight() + 15 + 10);
         Vec3d eyes = EnvironStateHandler.EnvironState.getPlayer().getPositionEyes((float)event.getRenderPartialTicks());
         if (eyes.y > (double)lowY && eyes.y < (double)highY) {
            float coreLowY = lowY + 15.0F;
            float coreHighY = coreLowY + 10.0F;
            float scaleFar = 0.6F;
            float scaleNear = 0.95F;
            if (eyes.y < (double)coreLowY) {
               float factor = (float)((eyes.y - (double)lowY) / 15.0D);
               scaleFar *= factor;
               scaleNear *= factor;
            } else if (eyes.y > (double)coreHighY) {
               float factor = (float)(((double)highY - eyes.y) / 15.0D);
               scaleFar *= factor;
               scaleNear *= factor;
            }

            float end = event.getFarPlaneDistance() * (1.0F - scaleFar);
            float start = event.getFarPlaneDistance() * (1.0F - scaleNear);
            this.cached.set(start, end);
            return this.cached;
         }
      }

      this.cached.set(event);
      return this.cached;
   }
}
