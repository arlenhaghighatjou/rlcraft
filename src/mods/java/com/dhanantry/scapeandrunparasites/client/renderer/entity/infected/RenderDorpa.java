package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelDorpa;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityDorpa;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderDorpa extends RenderLiving<EntityDorpa> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("srparasites:textures/entity/monster/dorpa.png");
   public static final ResourceLocation TEXTURE2 = new ResourceLocation("srparasites:textures/entity/monster/dorpa2.png");

   public RenderDorpa(RenderManager manager) {
      super(manager, new ModelDorpa(), 1.2F);
   }

   protected void preRenderCallback(EntityDorpa entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(0.78F * f2, 0.78F * f3, 0.78F * f2);
   }

   protected ResourceLocation getEntityTexture(EntityDorpa entity) {
      switch(entity.getSkin()) {
      case 1:
         return TEXTURE2;
      default:
         return TEXTURE;
      }
   }

   protected void applyRotations(EntityDorpa entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
