package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfHorseHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHorseHead;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderInfHorseHead extends RenderLiving<EntityInfHorseHead> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/horseh.png");

   public RenderInfHorseHead(RenderManager manager) {
      super(manager, new ModelInfHorseHead(), 0.6F);
   }

   protected ResourceLocation getEntityTexture(EntityInfHorseHead entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInfHorseHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
