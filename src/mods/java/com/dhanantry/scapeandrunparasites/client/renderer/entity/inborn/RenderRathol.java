package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelRathol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityRathol;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderRathol extends RenderLiving<EntityRathol> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/rathol.png");
   public static final ResourceLocation TEXTURES1 = new ResourceLocation("srparasites:textures/entity/monster/ratholone.png");

   public RenderRathol(RenderManager manager) {
      super(manager, new ModelRathol(), 1.2F);
   }

   protected void preRenderCallback(EntityRathol entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityRathol entity) {
      switch(entity.getSkin()) {
      case 1:
         return TEXTURES1;
      default:
         return TEXTURES;
      }
   }

   protected void applyRotations(EntityRathol entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
