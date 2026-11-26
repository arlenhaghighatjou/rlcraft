package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelBiomassPod;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelBiomassVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderBiomass extends RenderLiving<EntityBiomass> {
   protected static ModelBase modelV = new ModelBiomassVenkrol();
   protected static ModelBase modelP = new ModelBiomassPod();
   public static final ResourceLocation TEXTUREV = new ResourceLocation("srparasites:textures/entity/monster/biomassvenkrol.png");
   public static final ResourceLocation TEXTUREP = new ResourceLocation("srparasites:textures/entity/monster/biomasspod.png");

   public RenderBiomass(RenderManager p_i47208_1_) {
      super(p_i47208_1_, modelV, 0.5F);
   }

   protected void preRenderCallback(EntityBiomass entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityBiomass entity) {
      switch(entity.getSkin()) {
      case 1:
         return TEXTUREV;
      case 2:
         return TEXTUREV;
      case 3:
         return TEXTUREV;
      case 4:
         return TEXTUREP;
      case 5:
         return TEXTUREP;
      case 6:
         return TEXTUREP;
      default:
         return TEXTUREP;
      }
   }

   protected void applyRotations(EntityBiomass entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }

   protected void renderModel(EntityBiomass entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
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
            modelV.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            break;
         case 2:
            modelV.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            break;
         case 3:
            modelV.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            break;
         case 4:
            modelP.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            break;
         case 5:
            modelP.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            break;
         case 6:
            modelP.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         }

         if (flag1) {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthMask(true);
         }
      }

   }
}
