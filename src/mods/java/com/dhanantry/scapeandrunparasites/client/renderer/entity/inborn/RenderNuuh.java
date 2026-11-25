package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelNuuh;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderNuuh extends RenderMalleable<EntityNuuh> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/nuuh.png");

   public RenderNuuh(RenderManager manager) {
      super(manager, new ModelNuuh(), 0.7F);
   }

   protected ResourceLocation getEntityTexture(EntityNuuh entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityNuuh entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
