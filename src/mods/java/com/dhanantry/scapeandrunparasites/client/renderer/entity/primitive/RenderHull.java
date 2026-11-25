package com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive;

import com.dhanantry.scapeandrunparasites.client.model.entity.primitive.ModelHull;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderHull extends RenderMalleable<EntityHull> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/hull.png");
   private float secShadow = 0.8F;

   public RenderHull(RenderManager manager) {
      super(manager, new ModelHull(), 0.8F);
   }

   protected void preRenderCallback(EntityHull entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityHull entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityHull entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }

   protected void renderModel(EntityHull entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = this.isVisible(entitylivingbaseIn);
      boolean flag1 = !flag;
      boolean flag2 = entitylivingbaseIn.getSSS();
      if (flag || flag1 || flag2) {
         float f1 = 0.15F;
         if (flag2) {
            f1 = 0.04F;
         }

         if (!this.bindEntityTexture(entitylivingbaseIn)) {
            return;
         }

         if (flag1 || flag2) {
            this.shadowSize = 0.0F;
            GlStateManager.color(1.0F, 1.0F, 1.0F, f1);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         if (!flag1 && !flag2) {
            this.shadowSize = this.secShadow;
         } else {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthMask(true);
         }
      }

   }
}
