package com.dhanantry.scapeandrunparasites.client.renderer.entity.ancient;

import com.dhanantry.scapeandrunparasites.client.model.entity.projectile.ModelDropPod;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityDropPod;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderAncientPod extends RenderLiving<EntityDropPod> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/ancientpod.png");

   public RenderAncientPod(RenderManager manager) {
      super(manager, new ModelDropPod(), 0.8F);
   }

   protected void preRenderCallback(EntityDropPod entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityDropPod entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityDropPod entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
