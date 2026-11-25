package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfPlayerHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayerHead;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderInfPlayerHead extends RenderLiving<EntityInfPlayerHead> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/playerh.png");

   public RenderInfPlayerHead(RenderManager manager) {
      super(manager, new ModelInfPlayerHead(), 0.6F);
   }

   protected ResourceLocation getEntityTexture(EntityInfPlayerHead entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInfPlayerHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
