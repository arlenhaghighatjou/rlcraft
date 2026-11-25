package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelVenkrolSV;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSV;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderVenkrolSV extends RenderMalleable<EntityVenkrolSV> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/venkrolsv.png");

   public RenderVenkrolSV(RenderManager manager) {
      super(manager, new ModelVenkrolSV(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityVenkrolSV entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityVenkrolSV entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
