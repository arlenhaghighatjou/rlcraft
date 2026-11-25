package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelDodSIII;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderDodSIII extends RenderMalleable<EntityDodSIII> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/dodsiii.png");

   public RenderDodSIII(RenderManager manager) {
      super(manager, new ModelDodSIII(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityDodSIII entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityDodSIII entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
