package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelDodSII;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderDodSII extends RenderMalleable<EntityDodSII> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/dodsii.png");

   public RenderDodSII(RenderManager manager) {
      super(manager, new ModelDodSII(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityDodSII entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityDodSII entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
