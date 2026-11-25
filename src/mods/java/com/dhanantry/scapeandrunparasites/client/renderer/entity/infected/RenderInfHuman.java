package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderInfHuman extends RenderLiving<EntityInfHuman> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("srparasites:textures/entity/monster/human.png");
   public static final ResourceLocation TEXTURE1 = new ResourceLocation("srparasites:textures/entity/monster/human1.png");
   public static final ResourceLocation TEXTURE2 = new ResourceLocation("srparasites:textures/entity/monster/human2.png");
   public static final ResourceLocation TEXTURE3 = new ResourceLocation("srparasites:textures/entity/monster/humannocturn.png");

   public RenderInfHuman(RenderManager manager) {
      super(manager, new ModelInfHuman(), 0.5F);
   }

   protected void preRenderCallback(EntityInfHuman entitylivingbaseIn, float partialTickTime) {
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

   protected ResourceLocation getEntityTexture(EntityInfHuman entity) {
      switch(entity.getSkin()) {
      case 1:
         return TEXTURE1;
      case 2:
         return TEXTURE;
      case 10:
         return TEXTURE3;
      default:
         return TEXTURE;
      }
   }

   protected void applyRotations(EntityInfHuman entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
