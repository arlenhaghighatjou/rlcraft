package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelDod;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderDod extends RenderMalleable<EntityDod> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/dod.png");

   public RenderDod(RenderManager manager) {
      super(manager, new ModelDod(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityDod entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityDod entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
