package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelDodSIVH;
import com.dhanantry.scapeandrunparasites.entity.EntityBodyModel;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class RenderEntityBodyModel extends Render<EntityBodyModel> {
   public static final ResourceLocation TDODSIVH = new ResourceLocation("srparasites:textures/entity/monster/dodsivh.png");
   protected ModelSRP MDODSIVH = new ModelDodSIVH();

   public RenderEntityBodyModel(RenderManager p_i47208_1_) {
      super(p_i47208_1_);
   }

   protected ResourceLocation getEntityTexture(EntityBodyModel entity) {
      switch(entity.getSkin()) {
      case 1:
         return TDODSIVH;
      default:
         return TDODSIVH;
      }
   }

   public void doRender(EntityBodyModel entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      byte typePart = entity.getSkin();
      float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
      float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
      float f2 = f1 - f;
      float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
      this.renderLivingAt(entity, x, y, z);
      float f8 = this.handleRotationFloat(entity, partialTicks);
      this.applyRotations(entity, f8, f, partialTicks);
      float f4 = this.prepareScale(entity, partialTicks);
      float f5 = 0.0F;
      float f6 = 0.0F;
      f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
      f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
      GlStateManager.enableAlpha();
      switch(typePart) {
      case 1:
         this.MDODSIVH.setLivingAnimations(entity, f6, f5, partialTicks);
         this.MDODSIVH.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
      default:
         boolean flag = this.setDoRenderBrightness(entity, partialTicks);
         this.renderModel(entity, f6, f5, f8, f2, f7, f4);
         if (flag) {
            this.unsetBrightness();
         }

         GlStateManager.depthMask(true);
         GlStateManager.disableRescaleNormal();
         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.enableCull();
         GlStateManager.popMatrix();
      }
   }

   protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
      float f;
      for(f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return prevYawOffset + partialTicks * f;
   }

   protected void renderLivingAt(EntityBodyModel entityLivingBaseIn, double x, double y, double z) {
      GlStateManager.translate((float)x, (float)y, (float)z);
   }

   protected float handleRotationFloat(EntityBodyModel livingBase, float partialTicks) {
      return (float)livingBase.ticksExisted + partialTicks;
   }

   protected void applyRotations(EntityBodyModel entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
      String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName());
      if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s))) {
         GlStateManager.translate(0.0F, entityLiving.height + 0.1F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      }

   }

   public float prepareScale(EntityBodyModel entitylivingbaseIn, float partialTicks) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      this.preRenderCallback(entitylivingbaseIn, partialTicks);
      float f = 0.0625F;
      GlStateManager.translate(0.0F, -1.501F, 0.0F);
      return 0.0625F;
   }

   protected void preRenderCallback(EntityBodyModel entitylivingbaseIn, float partialTickTime) {
   }

   protected void renderModel(EntityBodyModel entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = this.isVisible(entitylivingbaseIn);
      boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().player);
      if (flag || flag1) {
         if (!this.bindEntityTexture(entitylivingbaseIn)) {
            return;
         }

         if (flag1) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         switch(entitylivingbaseIn.getSkin()) {
         case 1:
            this.MDODSIVH.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         default:
            if (flag1) {
               GlStateManager.disableBlend();
               GlStateManager.alphaFunc(516, 0.1F);
               GlStateManager.depthMask(true);
            }
         }
      }

   }

   protected boolean setDoRenderBrightness(EntityBodyModel entityLivingBaseIn, float partialTicks) {
      return this.setBrightness(entityLivingBaseIn, partialTicks, true);
   }

   protected boolean setBrightness(EntityBodyModel entitylivingbaseIn, float partialTicks, boolean combineTextures) {
      float f = entitylivingbaseIn.getBrightness();
      int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
      boolean flag = (i >> 24 & 255) > 0;
      boolean flag1 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
      if (!flag && !flag1) {
         return false;
      } else if (!flag && !combineTextures) {
         return false;
      } else {
         GlStateManager.enableShaderBlendAdd();
         float f1 = 1.0F - (float)(i >>> 24 & 255) / 255.0F;
         float f2 = (float)(i >>> 16 & 255) / 255.0F;
         float f3 = (float)(i >>> 8 & 255) / 255.0F;
         float f4 = (float)(i & 255) / 255.0F;
         GlStateManager.setShaderBlendSrc(f1, f1, f1, 1.0F);
         GlStateManager.setShaderBlendAdd(f2 * f1 + 0.4F, f3 * f1 + 0.0F, f4 * f1 + 0.0F, 0.0F);
         return true;
      }
   }

   protected int getColorMultiplier(EntityBodyModel entitylivingbaseIn, float lightBrightness, float partialTickTime) {
      return 0;
   }

   protected boolean isVisible(EntityBodyModel p_193115_1_) {
      return !p_193115_1_.isInvisible() || this.renderOutlines;
   }

   protected void unsetBrightness() {
      GlStateManager.disableShaderBlendAdd();
   }
}
