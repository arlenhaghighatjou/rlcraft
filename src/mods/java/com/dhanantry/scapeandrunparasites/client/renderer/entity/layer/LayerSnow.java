package com.dhanantry.scapeandrunparasites.client.renderer.entity.layer;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LayerSnow<T extends EntityParasiteBase> implements LayerRenderer<T> {
   private ResourceLocation LAYER;
   private final RenderLiving parasite;

   public LayerSnow(RenderLiving in, ResourceLocation l, double x, double y, double z) {
      this.parasite = in;
      this.LAYER = l;
   }

   public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entitylivingbaseIn.getColdL()) {
         this.parasite.bindTexture(this.LAYER);
         GlStateManager.enableBlend();
         if (entitylivingbaseIn.isInvisible()) {
            GlStateManager.depthMask(false);
         } else {
            GlStateManager.depthMask(true);
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
         this.parasite.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale + 0.0F);
         Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
         this.parasite.setLightmap(entitylivingbaseIn);
         GlStateManager.disableBlend();
         GlStateManager.enableAlpha();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
