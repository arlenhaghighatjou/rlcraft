package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfCowHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCowHead;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderInfCowHead extends RenderLiving<EntityInfCowHead> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/cowh.png");

   public RenderInfCowHead(RenderManager manager) {
      super(manager, new ModelInfCowHead(), 0.6F);
   }

   protected ResourceLocation getEntityTexture(EntityInfCowHead entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInfCowHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
