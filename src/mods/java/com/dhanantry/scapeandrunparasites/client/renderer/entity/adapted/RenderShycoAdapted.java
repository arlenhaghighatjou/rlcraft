package com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted;

import com.dhanantry.scapeandrunparasites.client.model.entity.adapted.ModelShycoAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderShycoAdapted extends RenderMalleable<EntityShycoAdapted> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("srparasites:textures/entity/monster/shycoa.png");
   public static final ResourceLocation TEXTURE2 = new ResourceLocation("srparasites:textures/entity/monster/shycoatyrant.png");
   public static final ResourceLocation TEXTURE3 = new ResourceLocation("srparasites:textures/entity/monster/shycoalovecraft.png");
   public static final ResourceLocation TEXTURE4 = new ResourceLocation("srparasites:textures/entity/monster/shycoaabyss.png");
   public static final ResourceLocation TEXTUREV = new ResourceLocation("srparasites:textures/entity/monster/shycoav.png");
   public static final ResourceLocation TEXTUREB = new ResourceLocation("srparasites:textures/entity/monster/shycoab.png");

   public RenderShycoAdapted(RenderManager manager) {
      super(manager, new ModelShycoAdapted(), 1.0F);
   }

   protected void preRenderCallback(EntityShycoAdapted entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityShycoAdapted entity) {
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

   protected void applyRotations(EntityShycoAdapted entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
