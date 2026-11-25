package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCow;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderInfCow extends RenderLiving<EntityInfCow> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/cow.png");

   public RenderInfCow(RenderManager manager) {
      super(manager, new ModelInfCow(), 0.5F);
   }

   protected void preRenderCallback(EntityInfCow entitylivingbaseIn, float partialTickTime) {
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

   protected ResourceLocation getEntityTexture(EntityInfCow entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInfCow entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
