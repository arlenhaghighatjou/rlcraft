package com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive;

import com.dhanantry.scapeandrunparasites.client.model.entity.primitive.ModelNogla;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderNogla extends RenderMalleable<EntityNogla> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("srparasites:textures/entity/monster/nogla.png");
   public static final ResourceLocation TEXTURE2 = new ResourceLocation("srparasites:textures/entity/monster/noglasp1.png");
   public static final ResourceLocation TEXTUREV = new ResourceLocation("srparasites:textures/entity/monster/noglav.png");
   public static final ResourceLocation TEXTUREB = new ResourceLocation("srparasites:textures/entity/monster/noglab.png");

   public RenderNogla(RenderManager manager) {
      super(manager, new ModelNogla(), 1.3F);
   }

   protected void preRenderCallback(EntityNogla entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityNogla entity) {
      switch(entity.getSkin()) {
      case 1:
         return TEXTURE2;
      case 5:
         return TEXTUREV;
      case 6:
         return TEXTUREB;
      default:
         return TEXTURE;
      }
   }

   protected void applyRotations(EntityNogla entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
