package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.common.MinecraftForge;

public abstract class RenderCosmical<T extends EntityPCosmical> extends RenderMalleable<T> {
   protected ModelSRP mainModel2;

   public RenderCosmical(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
      super(rendermanagerIn, modelbaseIn, shadowsizeIn);
      this.mainModel2 = (ModelSRP)modelbaseIn;
   }

   public RenderCosmical(RenderManager rendermanagerIn, ModelBase modelbaseIn, ModelSRP modelbaseIn2, float shadowsizeIn) {
      super(rendermanagerIn, modelbaseIn, shadowsizeIn);
      this.mainModel2 = modelbaseIn2;
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      this.doRenderCosmical(entity, x, y, z, entityYaw, partialTicks);
   }

   public void doRenderCosmical(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!MinecraftForge.EVENT_BUS.post(new Pre(entity, this, partialTicks, x, y, z))) {
         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         this.mainModel2.swingProgress = this.getSwingProgress(entity, partialTicks);
         boolean shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
         this.mainModel2.isRiding = shouldSit;
         this.mainModel2.isChild = entity.isChild();

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
               if (entity.isChild()) {
                  f6 *= 3.0F;
               }

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
               boolean flag1 = this.setScoreTeamColor(entity);
               GlStateManager.enableColorMaterial();
               GlStateManager.enableOutlineMode(this.getTeamColor(entity));
               if (!this.renderMarker) {
                  this.renderModelCosmical(entity, f6, f5, f8, f2, f7, f4);
               }

               this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
               GlStateManager.disableOutlineMode();
               GlStateManager.disableColorMaterial();
               if (flag1) {
                  this.unsetScoreTeamColor();
               }
            } else {
               boolean flag = this.setDoRenderBrightness(entity, partialTicks);
               this.renderModelCosmical(entity, f6, f5, f8, f2, f7, f4);
               if (flag) {
                  this.unsetBrightness();
               }

               GlStateManager.depthMask(true);
               this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
            }

            GlStateManager.disableRescaleNormal();
         } catch (Exception var20) {
            ;
         }

         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.enableCull();
         GlStateManager.popMatrix();
         MinecraftForge.EVENT_BUS.post(new Post(entity, this, partialTicks, x, y, z));
      }
   }

   protected void renderModelCosmical(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = this.isVisible(entitylivingbaseIn);
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
         this.mainModel2.renderC(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.depthMask(true);
      }

   }

   protected abstract ResourceLocation getEntityTextureCosmical(T var1);

   protected boolean bindEntityTextureCosmical(T entity) {
      ResourceLocation resourcelocation = this.getEntityTextureCosmical(entity);
      if (resourcelocation == null) {
         return false;
      } else {
         this.bindTexture(resourcelocation);
         return true;
      }
   }

   protected float prepareScaleCosmical(T entitylivingbaseIn, float partialTicks) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      this.preRenderCallbackCosmical(entitylivingbaseIn, partialTicks);
      float f = 0.0625F;
      GlStateManager.translate(0.0F, -1.501F, 0.0F);
      return 0.0625F;
   }

   protected void preRenderCallbackCosmical(T entitylivingbaseIn, float partialTickTime) {
      GlStateManager.scale(1.2D, 1.2D, 1.2D);
   }
}
