package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelVenkrol;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderVenkrol extends RenderMalleable<EntityVenkrol> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/venkrol.png");

   public RenderVenkrol(RenderManager manager) {
      super(manager, new ModelVenkrol(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityVenkrol entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityVenkrol entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
