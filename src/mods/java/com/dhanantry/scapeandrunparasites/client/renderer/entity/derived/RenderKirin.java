package com.dhanantry.scapeandrunparasites.client.renderer.entity.derived;

import com.dhanantry.scapeandrunparasites.client.model.entity.derived.ModelKirin;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderCosmical;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderKirin extends RenderCosmical<EntityKirin> {
   public static final ResourceLocation TEXTURES = new ResourceLocation("srparasites:textures/entity/monster/test.png");
   public static final ResourceLocation TEXTURESEC = new ResourceLocation("srparasites:textures/entity/monster/testb.png");

   public RenderKirin(RenderManager manager) {
      super(manager, new ModelKirin(), 1.3F);
   }

   protected ResourceLocation getEntityTexture(EntityKirin entity) {
      return TEXTURESEC;
   }

   protected ResourceLocation getEntityTextureCosmical(EntityKirin entity) {
      return TEXTURESEC;
   }

   protected void applyRotations(EntityKirin entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
