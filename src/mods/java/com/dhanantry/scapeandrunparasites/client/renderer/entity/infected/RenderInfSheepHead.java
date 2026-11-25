package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfSheepHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheepHead;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderInfSheepHead extends RenderLiving<EntityInfSheepHead> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/sheeph.png");

   public RenderInfSheepHead(RenderManager manager) {
      super(manager, new ModelInfSheepHead(), 0.5F);
   }

   protected ResourceLocation getEntityTexture(EntityInfSheepHead entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInfSheepHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
