package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.config.FantasticConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.client.event.RenderTooltipEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TooltipAlterer {
   public TooltipAlterer() {
   }

   @SubscribeEvent
   public static void renderTooltipPre(Pre event) {
      double scale = FantasticConfig.tooltipScaling;
      GlStateManager.pushMatrix();
      GlStateManager.translate((double)event.getX() * (1.0D - scale), (double)event.getY() * (1.0D - scale), 0.0D);
      GlStateManager.scale(scale, scale, scale);
   }

   @SubscribeEvent
   public static void renderTooltipPost(PostText event) {
      GlStateManager.popMatrix();
   }
}
