package com.dhanantry.scapeandrunparasites.client.renderer.entity.ancient;

import com.dhanantry.scapeandrunparasites.client.model.entity.ancient.ModelOronco;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderOronco extends RenderMalleable<EntityOronco> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/oronco.png");

   public RenderOronco(RenderManager manager) {
      super(manager, new ModelOronco(), 4.0F);
   }

   protected ResourceLocation getEntityTexture(EntityOronco entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityOronco entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
