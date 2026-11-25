package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelScaryOrb;
import com.dhanantry.scapeandrunparasites.entity.EntityScaryOrb;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class RenderScaryOrb extends Render<EntityScaryOrb> {
   protected ModelSRP mainModel2 = new ModelScaryOrb();
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/orb.png");

   public RenderScaryOrb(RenderManager p_i47208_1_) {
      super(p_i47208_1_);
   }

   protected ResourceLocation getEntityTexture(EntityScaryOrb entity) {
      return TEXTURES;
   }

   public void doRender(EntityScaryOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      this.doRenderCosmical(entity, x, y, z, entityYaw, partialTicks);
   }

   public void doRenderCosmical(EntityScaryOrb entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      boolean shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
      this.mainModel2.isRiding = shouldSit;

      try {
         float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
         float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
         float f2 = f1 - f;
         if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
            f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
            f2 = f1 - f;
            float f3 = MathHelper.wrapDegrees(f2);
            if (f3 < -85.0F) {
               f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
               f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
               f += f3 * 0.2F;
            }

            f2 = f1 - f;
         }

         float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
         this.renderLivingAt(entity, x, y, z);
         float f8 = this.handleRotationFloat(entity, partialTicks);
         this.applyRotations(entity, f8, f, partialTicks);
         float f4 = this.prepareScaleCosmical(entity, partialTicks);
         float f5 = 0.0F;
         float f6 = 0.0F;
         if (!entity.isRiding()) {
            f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
            if (f5 > 1.0F) {
               f5 = 1.0F;
            }

            f2 = f1 - f;
         }

         GlStateManager.enableAlpha();
         this.mainModel2.setLivingAnimations(entity, f6, f5, partialTicks);
         this.mainModel2.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
         this.mainModel2.setRotationAnglesCosmical(f6, f5, f8, f2, f7, f4, entity);
         if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
         } else {
            this.renderModelCosmical(entity, f6, f5, f8, f2, f7, f4);
            GlStateManager.depthMask(true);
         }

         GlStateManager.disableRescaleNormal();
      } catch (Exception var19) {
         ;
      }

      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
   }

   protected void renderModelCosmical(EntityScaryOrb entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = false;
      boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().player);
      if (flag || flag1) {
         if (!this.bindEntityTextureCosmical(entitylivingbaseIn)) {
            return;
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 0.35F);
         GlStateManager.depthMask(false);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.alphaFunc(516, 0.003921569F);
         this.mainModel2.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.depthMask(true);
      }

   }

   protected boolean bindEntityTextureCosmical(EntityScaryOrb entity) {
      ResourceLocation resourcelocation = this.getEntityTexture(entity);
      if (resourcelocation == null) {
         return false;
      } else {
         this.bindTexture(resourcelocation);
         return true;
      }
   }

   protected float prepareScaleCosmical(EntityScaryOrb entitylivingbaseIn, float partialTicks) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      this.preRenderCallbackCosmical(entitylivingbaseIn, partialTicks);
      float f = 0.0625F;
      GlStateManager.translate(0.0F, -1.501F, 0.0F);
      return 0.0625F;
   }

   protected void preRenderCallbackCosmical(EntityScaryOrb entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.1F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      float plusX = entitylivingbaseIn.width * 2.0F;
      float plusY = entitylivingbaseIn.height * 1.9F;
      GlStateManager.scale(plusX + f2, plusY + f3, plusX + f2);
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

   protected void renderLivingAt(EntityScaryOrb entityLivingBaseIn, double x, double y, double z) {
      GlStateManager.translate((float)x, (float)y, (float)z);
   }

   protected float handleRotationFloat(EntityScaryOrb livingBase, float partialTicks) {
      return (float)livingBase.ticksExisted + partialTicks;
   }

   protected void applyRotations(EntityScaryOrb entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
      if (entityLiving.deathTime > 0) {
         float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         GlStateManager.rotate(f * 90.0F, 0.0F, 0.0F, 1.0F);
      } else {
         String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName());
         if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s))) {
            GlStateManager.translate(0.0F, entityLiving.height + 0.1F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }

   }
}
