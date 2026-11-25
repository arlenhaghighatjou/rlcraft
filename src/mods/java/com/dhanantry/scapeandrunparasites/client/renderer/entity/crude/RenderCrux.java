package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelCruxA;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCrux;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderCrux extends RenderLiving<EntityCrux> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/cruxa.png");

   public RenderCrux(RenderManager manager) {
      super(manager, new ModelCruxA(), 1.3F);
   }

   protected void preRenderCallback(EntityCrux entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityCrux entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityCrux entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
