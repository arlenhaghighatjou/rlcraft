package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelVenkrolSII;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderVenkrolSII extends RenderMalleable<EntityVenkrolSII> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/venkrolsii.png");

   public RenderVenkrolSII(RenderManager manager) {
      super(manager, new ModelVenkrolSII(), 0.5F);
   }

   protected ResourceLocation getEntityTexture(EntityVenkrolSII entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityVenkrolSII entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
