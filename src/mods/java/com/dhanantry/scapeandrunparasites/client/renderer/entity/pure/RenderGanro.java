package com.dhanantry.scapeandrunparasites.client.renderer.entity.pure;

import com.dhanantry.scapeandrunparasites.client.model.entity.pure.ModelGanro;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderGanro extends RenderMalleable<EntityGanro> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/ganro.png");

   public RenderGanro(RenderManager manager) {
      super(manager, new ModelGanro(), 1.2F);
   }

   protected void preRenderCallback(EntityGanro entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityGanro entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityGanro entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
