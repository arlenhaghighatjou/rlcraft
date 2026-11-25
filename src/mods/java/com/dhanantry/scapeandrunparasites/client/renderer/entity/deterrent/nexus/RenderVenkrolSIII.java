package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelVenkrolSIII;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderVenkrolSIII extends RenderMalleable<EntityVenkrolSIII> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/venkrolsiii.png");

   public RenderVenkrolSIII(RenderManager manager) {
      super(manager, new ModelVenkrolSIII(), 0.6F);
   }

   protected ResourceLocation getEntityTexture(EntityVenkrolSIII entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityVenkrolSIII entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
