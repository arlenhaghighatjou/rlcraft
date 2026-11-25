package com.dhanantry.scapeandrunparasites.client.renderer.entity.ancient;

import com.dhanantry.scapeandrunparasites.client.model.entity.ancient.ModelOroncoTen;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOroncoTen;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderOroncoTen extends RenderLiving<EntityOroncoTen> {
   public static final ResourceLocation TEXTURE00 = new ResourceLocation("srparasites:textures/entity/monster/oroncoten0.png");
   public static final ResourceLocation TEXTURE01 = new ResourceLocation("srparasites:textures/entity/monster/oroncoten1.png");

   public RenderOroncoTen(RenderManager manager) {
      super(manager, new ModelOroncoTen(), 0.8F);
   }

   protected ResourceLocation getEntityTexture(EntityOroncoTen entity) {
      switch(entity.getSkin()) {
      case 1:
         return TEXTURE01;
      default:
         return TEXTURE00;
      }
   }

   protected void applyRotations(EntityOroncoTen entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
