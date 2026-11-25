package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelHostII;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHostII;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderHostII extends RenderMalleable<EntityHostII> {
   public static final ResourceLocation TEXTUREM = new ResourceLocation("srparasites:textures/entity/monster/hostii.png");

   public RenderHostII(RenderManager manager) {
      super(manager, new ModelHostII(), 0.0F);
   }

   protected ResourceLocation getEntityTexture(EntityHostII entity) {
      return TEXTUREM;
   }

   protected void applyRotations(EntityHostII entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
