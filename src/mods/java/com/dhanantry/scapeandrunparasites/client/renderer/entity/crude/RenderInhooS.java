package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderInhooS extends RenderLiving<EntityInhooS> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/inhoos.png");

   public RenderInhooS(RenderManager manager) {
      super(manager, new ModelInhooS(), 0.5F);
   }

   protected ResourceLocation getEntityTexture(EntityInhooS entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityInhooS entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
