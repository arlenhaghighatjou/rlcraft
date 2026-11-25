package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.ModelNak;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderNak extends RenderMalleable<EntityNak> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/nak.png");

   public RenderNak(RenderManager manager) {
      super(manager, new ModelNak(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityNak entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityNak entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
