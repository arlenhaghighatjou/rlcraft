package com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive;

import com.dhanantry.scapeandrunparasites.client.model.entity.primitive.ModelShyco;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderShyco extends RenderMalleable<EntityShyco> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/shyco.png");
   public static final ResourceLocation TEXTUREV = new ResourceLocation("srparasites:textures/entity/monster/shycov.png");
   public static final ResourceLocation TEXTUREB = new ResourceLocation("srparasites:textures/entity/monster/shycob.png");

   public RenderShyco(RenderManager manager) {
      super(manager, new ModelShyco(), 0.7F);
   }

   protected void preRenderCallback(EntityShyco entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityShyco entity) {
      switch(entity.getSkin()) {
      case 5:
         return TEXTUREV;
      case 6:
         return TEXTUREB;
      default:
         return TEXTURES;
      }
   }

   protected void applyRotations(EntityShyco entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
