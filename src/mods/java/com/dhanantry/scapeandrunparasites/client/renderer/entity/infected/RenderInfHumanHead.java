package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfHumanHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHumanHead;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderInfHumanHead extends RenderLiving<EntityInfHumanHead> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("srparasites:textures/entity/monster/humanh.png");
   public static final ResourceLocation TEXTURE1 = new ResourceLocation("srparasites:textures/entity/monster/humanh1.png");
   public static final ResourceLocation TEXTURE2 = new ResourceLocation("srparasites:textures/entity/monster/humanh2.png");
   public static final ResourceLocation TEXTURE3 = new ResourceLocation("srparasites:textures/entity/monster/humanhnocturn.png");

   public RenderInfHumanHead(RenderManager manager) {
      super(manager, new ModelInfHumanHead(), 0.6F);
   }

   protected ResourceLocation getEntityTexture(EntityInfHumanHead entity) {
      switch(entity.getSkin()) {
      case 1:
         return TEXTURE1;
      case 2:
         return TEXTURE;
      case 10:
         return TEXTURE3;
      default:
         return TEXTURE;
      }
   }

   protected void applyRotations(EntityInfHumanHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
