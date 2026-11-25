package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelHost;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderHost extends RenderLiving<EntityHost> {
   public static final ResourceLocation TEXTUREM = new ResourceLocation("srparasites:textures/entity/monster/host.png");

   public RenderHost(RenderManager manager) {
      super(manager, new ModelHost(), 0.0F);
   }

   protected ResourceLocation getEntityTexture(EntityHost entity) {
      return TEXTUREM;
   }

   protected void applyRotations(EntityHost entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
