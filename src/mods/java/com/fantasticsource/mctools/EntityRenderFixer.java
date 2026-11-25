package com.fantasticsource.mctools;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityRenderFixer {
   public EntityRenderFixer() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST,
      receiveCanceled = true
   )
   public static void entityPostRender(Post event) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableTexture2D();
   }
}
