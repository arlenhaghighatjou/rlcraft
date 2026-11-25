package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderMudo extends RenderLiving<EntityMudo> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/mudo.png");

   public RenderMudo(RenderManager manager) {
      super(manager, new ModelMudo(), 0.5F);
   }

   protected ResourceLocation getEntityTexture(EntityMudo entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityMudo entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
