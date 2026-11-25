package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.ModelUnvo;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderUnvo extends RenderMalleable<EntityUnvo> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/unvo.png");

   public RenderUnvo(RenderManager manager) {
      super(manager, new ModelUnvo(), 0.5F);
   }

   protected ResourceLocation getEntityTexture(EntityUnvo entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityUnvo entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
