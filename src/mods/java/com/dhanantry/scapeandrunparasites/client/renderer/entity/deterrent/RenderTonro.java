package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent;

import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.ModelTonro;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityTonro;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderTonro extends RenderMalleable<EntityTonro> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/tonro.png");

   public RenderTonro(RenderManager manager) {
      super(manager, new ModelTonro(), 0.4F);
   }

   protected ResourceLocation getEntityTexture(EntityTonro entity) {
      return TEXTURES;
   }

   protected void applyRotations(EntityTonro entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
