package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderInhooM extends RenderLiving<EntityInhooM> {
   public static final ResourceLocation TEXTUREM = new ResourceLocation("srparasites:textures/entity/monster/inhoom.png");

   public RenderInhooM(RenderManager manager) {
      super(manager, new ModelInhooM(), 0.5F);
   }

   protected ResourceLocation getEntityTexture(EntityInhooM entity) {
      return TEXTUREM;
   }

   protected void applyRotations(EntityInhooM entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
