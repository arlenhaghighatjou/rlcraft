package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfPigHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPigHead;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderInfPigHead extends RenderLiving<EntityInfPigHead> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/pigh.png");

   public RenderInfPigHead(RenderManager manager) {
      super(manager, new ModelInfPigHead(), 0.6F);
   }

   protected ResourceLocation getEntityTexture(EntityInfPigHead entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInfPigHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
