package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelDodSIV;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderDodSIV extends RenderMalleable<EntityDodSIV> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/dodsiv.png");

   public RenderDodSIV(RenderManager manager) {
      super(manager, new ModelDodSIV(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityDodSIV entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityDodSIV entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
