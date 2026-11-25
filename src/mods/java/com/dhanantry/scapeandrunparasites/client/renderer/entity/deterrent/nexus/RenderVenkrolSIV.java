package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelVenkrolSIV;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIV;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderVenkrolSIV extends RenderMalleable<EntityVenkrolSIV> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/venkrolsiv.png");

   public RenderVenkrolSIV(RenderManager manager) {
      super(manager, new ModelVenkrolSIV(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityVenkrolSIV entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityVenkrolSIV entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
