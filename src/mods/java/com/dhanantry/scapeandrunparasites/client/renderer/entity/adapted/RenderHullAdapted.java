package com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted;

import com.dhanantry.scapeandrunparasites.client.model.entity.adapted.ModelHullAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderHullAdapted extends RenderMalleable<EntityHullAdapted> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/hulla.png");
   private float secShadow = 1.0F;

   public RenderHullAdapted(RenderManager manager) {
      super(manager, new ModelHullAdapted(), 1.0F);
   }

   protected void preRenderCallback(EntityHullAdapted entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityHullAdapted entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityHullAdapted entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }

   protected void renderModel(EntityHullAdapted entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = this.isVisible(entitylivingbaseIn);
      boolean flag1 = !flag;
      boolean flag2 = entitylivingbaseIn.getSSS();
      if (flag || flag1 || flag2) {
         float f1 = 0.15F;
         if (flag2) {
            f1 = 0.02F;
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
