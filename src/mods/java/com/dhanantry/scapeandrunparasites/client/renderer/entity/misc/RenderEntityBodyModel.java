package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelDodSIVH;
import com.dhanantry.scapeandrunparasites.entity.EntityBodyModel;
import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class RenderEntityBodyModel extends Render<EntityBodyModel> {
   public static final ResourceLocation TDODSIVH = new ResourceLocation("srparasites:textures/entity/monster/dodsivh.png");
   protected ModelSRP MDODSIVH = new ModelDodSIVH();
   private static final DynamicTexture TEXTURE_BRIGHTNESS = new DynamicTexture(16, 16);
   protected FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);

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
            GlStateManager.enableBlendProfile(Profile.TRANSPARENT_MODEL);
         }

         switch(entitylivingbaseIn.getSkin()) {
         case 1:
            this.MDODSIVH.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         default:
            if (flag1) {
               GlStateManager.disableBlendProfile(Profile.TRANSPARENT_MODEL);
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
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
         this.brightnessBuffer.position(0);
         if (flag1) {
            this.brightnessBuffer.put(1.0F);
            this.brightnessBuffer.put(0.0F);
            this.brightnessBuffer.put(0.0F);
            this.brightnessBuffer.put(0.3F);
         } else {
            float f1 = (float)(i >> 24 & 255) / 255.0F;
            float f2 = (float)(i >> 16 & 255) / 255.0F;
            float f3 = (float)(i >> 8 & 255) / 255.0F;
            float f4 = (float)(i & 255) / 255.0F;
            this.brightnessBuffer.put(f2);
            this.brightnessBuffer.put(f3);
            this.brightnessBuffer.put(f4);
            this.brightnessBuffer.put(1.0F - f1);
         }

         this.brightnessBuffer.flip();
         GlStateManager.glTexEnv(8960, 8705, this.brightnessBuffer);
         GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
         GlStateManager.enableTexture2D();
         GlStateManager.bindTexture(TEXTURE_BRIGHTNESS.getGlTextureId());
         GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
         GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
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
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
      GlStateManager.disableTexture2D();
      GlStateManager.bindTexture(0);
      GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }
}
