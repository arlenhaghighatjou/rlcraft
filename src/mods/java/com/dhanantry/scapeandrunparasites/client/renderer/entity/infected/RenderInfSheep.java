package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheep;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderInfSheep extends RenderLiving<EntityInfSheep> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/sheep.png");

   public RenderInfSheep(RenderManager manager) {
      super(manager, new ModelInfSheep(), 0.5F);
   }

   protected void preRenderCallback(EntityInfSheep entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float ff = entitylivingbaseIn.getSelfeFlashIntensity2();
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, ff * f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityInfSheep entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInfSheep entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
