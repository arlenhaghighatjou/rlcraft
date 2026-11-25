package com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.preeminent;

import com.dhanantry.scapeandrunparasites.client.model.entity.pure.preeminent.ModelVesta;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.layer.LayerSnow;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderVesta extends RenderMalleable<EntityVesta> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/vesta.png");
   public static final ResourceLocation TEXTURE1 = new ResourceLocation("srparasites:textures/entity/monster/vestare.png");

   public RenderVesta(RenderManager manager) {
      super(manager, new ModelVesta(), 1.3F);
      this.addLayer(new LayerSnow(this, new ResourceLocation("srparasites:textures/entity/layer/vestasnow.png"), 1.01D, 1.001D, 1.01D));
   }

   protected void preRenderCallback(EntityVesta entitylivingbaseIn, float partialTickTime) {
      float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
      float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      f = f * f;
      f = f * f;
      float f2 = (1.0F + f * 0.4F) * f1;
      float f3 = (1.0F + f * 0.1F) / f1;
      GlStateManager.scale(f2, f3, f2);
   }

   protected ResourceLocation getEntityTexture(EntityVesta entity) {
      switch(entity.getSkin()) {
      case 1:
         return TEXTURE1;
      default:
         return TEXTURES;
      }
   }

   protected void applyRotations(EntityVesta entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
