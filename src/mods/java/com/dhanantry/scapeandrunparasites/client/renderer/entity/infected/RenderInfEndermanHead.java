package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfEndermanHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEndermanHead;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderInfEndermanHead extends RenderLiving<EntityInfEndermanHead> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/endermanh.png");

   public RenderInfEndermanHead(RenderManager manager) {
      super(manager, new ModelInfEndermanHead(), 0.6F);
   }

   protected ResourceLocation getEntityTexture(EntityInfEndermanHead entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInfEndermanHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
