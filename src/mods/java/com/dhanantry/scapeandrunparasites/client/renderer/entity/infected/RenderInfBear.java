package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfBear;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfBear;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderInfBear extends RenderLiving<EntityInfBear> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/infbear.png");

   public RenderInfBear(RenderManager manager) {
      super(manager, new ModelInfBear(), 0.7F);
   }

   protected void preRenderCallback(EntityInfBear entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float ff = entitylivingbaseIn.getSelfeFlashIntensity2();
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(1.2F * f2, 1.2F * ff * f3, 1.2F * f2);
   }

   protected ResourceLocation getEntityTexture(EntityInfBear entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInfBear entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
