package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfWolfHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolfHead;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderInfWolfHead extends RenderLiving<EntityInfWolfHead> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/wolfh.png");

   public RenderInfWolfHead(RenderManager manager) {
      super(manager, new ModelInfWolfHead(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityInfWolfHead entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInfWolfHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
