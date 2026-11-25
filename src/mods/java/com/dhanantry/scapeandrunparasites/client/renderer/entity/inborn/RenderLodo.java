package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelLodo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderLodo extends RenderLiving<EntityLodo> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/lodo.png");

   public RenderLodo(RenderManager manager) {
      super(manager, new ModelLodo(), 0.2F);
   }

   protected ResourceLocation getEntityTexture(EntityLodo entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityLodo entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
