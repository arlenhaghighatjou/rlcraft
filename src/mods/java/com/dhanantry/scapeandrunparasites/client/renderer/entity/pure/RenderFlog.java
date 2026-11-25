package com.dhanantry.scapeandrunparasites.client.renderer.entity.pure;

import com.dhanantry.scapeandrunparasites.client.model.entity.pure.ModelFlog;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityFlog;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderFlog extends RenderMalleable<EntityFlog> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("srparasites:textures/entity/monster/flog.png");
   public static final ResourceLocation TEXTUREV = new ResourceLocation("srparasites:textures/entity/monster/flogv.png");
   public static final ResourceLocation TEXTUREB = new ResourceLocation("srparasites:textures/entity/monster/flogb.png");

   public RenderFlog(RenderManager manager) {
      super(manager, new ModelFlog(), 0.5F);
   }

   protected void preRenderCallback(EntityFlog entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityFlog entity) {
      switch(entity.getSkin()) {
      case 5:
         return TEXTUREV;
      case 6:
         return TEXTUREB;
      default:
         return TEXTURE;
      }
   }

   protected void applyRotations(EntityFlog entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
