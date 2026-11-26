package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;

public abstract class RenderMalleable<T extends EntityPMalleable> extends RenderLiving<T> {
   public RenderMalleable(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
      super(rendermanagerIn, modelbaseIn, shadowsizeIn);
   }

   protected boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures) {
      float f = entitylivingbaseIn.getBrightness();
      int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
      boolean flag = (i >> 24 & 255) > 0;
      boolean flag1 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
      if (!flag && !flag1) {
         return false;
      } else if (!flag && !combineTextures) {
         return false;
      } else {
         float hurtRed = 0.4F;
         float hurtGreen = 0.0F;
         float hurtBlue = 0.0F;
         if (flag1) {
            switch(entitylivingbaseIn.getHitStatus()) {
            case 1:
               hurtRed = 0.0F;
               hurtGreen = 0.4F;
               break;
            case 2:
               hurtBlue = 0.4F;
            }
         }

         GlStateManager.enableShaderBlendAdd();
         float f1 = 1.0F - (float)(i >>> 24 & 255) / 255.0F;
         float f2 = (float)(i >>> 16 & 255) / 255.0F;
         float f3 = (float)(i >>> 8 & 255) / 255.0F;
         float f4 = (float)(i & 255) / 255.0F;
         GlStateManager.setShaderBlendSrc(f1, f1, f1, 1.0F);
         GlStateManager.setShaderBlendAdd(f2 * f1 + hurtRed, f3 * f1 + hurtGreen, f4 * f1 + hurtBlue, 0.0F);
         return true;
      }
   }
}
