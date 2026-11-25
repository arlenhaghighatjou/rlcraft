package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.WorldUtils;

@SideOnly(Side.CLIENT)
public class BedrockFogRangeCalculator extends VanillaFogRangeCalculator {
   protected final FogResult cached = new FogResult();
   protected double skyLight;

   public BedrockFogRangeCalculator() {
   }

   @Nonnull
   public String getName() {
      return "BedrockFogRangeCalculator";
   }

   @Nonnull
   public FogResult calculate(@Nonnull RenderFogEvent event) {
      this.cached.set(event);
      if (WorldUtils.hasVoidPartiles(EnvironStateHandler.EnvironState.getWorld())) {
         EntityLivingBase player = EnvironStateHandler.EnvironState.getPlayer();
         double factor = (player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getRenderPartialTicks() + 4.0D) / 32.0D;
         double d0 = this.skyLight / 16.0D + factor;
         float end = event.getFarPlaneDistance();
         if (d0 < 1.0D) {
            if (d0 < 0.0D) {
               d0 = 0.0D;
            }

            d0 = d0 * d0;
            float f2 = 100.0F * (float)d0;
            if (f2 < 5.0F) {
               f2 = 5.0F;
            }

            if (end > f2) {
               end = f2;
            }
         }

         this.cached.set(event.getFogMode(), end, 0.75F);
      }

      return this.cached;
   }

   public void tick() {
      this.skyLight = (double)((EnvironStateHandler.EnvironState.getPlayer().getBrightnessForRender() & 15728640) >> 20);
   }
}
