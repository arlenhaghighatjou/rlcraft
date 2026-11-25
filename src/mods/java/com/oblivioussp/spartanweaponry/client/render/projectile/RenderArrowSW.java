package com.oblivioussp.spartanweaponry.client.render.projectile;

import com.oblivioussp.spartanweaponry.entity.projectile.EntityArrowBase;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderArrowSW extends RenderArrow<EntityArrowBase> {
   public static final ResourceLocation RES_ARROW_WOOD = new ResourceLocation("spartanweaponry:textures/entity/projectile/arrow_wood.png");
   public static final ResourceLocation RES_ARROW_IRON = new ResourceLocation("spartanweaponry:textures/entity/projectile/arrow_iron.png");
   public static final ResourceLocation RES_ARROW_DIAMOND = new ResourceLocation("spartanweaponry:textures/entity/projectile/arrow_diamond.png");
   public static final ResourceLocation RES_ARROW_EXPLOSIVE = new ResourceLocation("spartanweaponry:textures/entity/projectile/arrow_explosive.png");

   public RenderArrowSW(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected ResourceLocation getEntityTexture(EntityArrowBase entity) {
      EntityArrowBase.ArrowType type = entity.getArrowType();
      switch(type) {
      case WOOD:
         return RES_ARROW_WOOD;
      case IRON:
         return RES_ARROW_IRON;
      case DIAMOND:
         return RES_ARROW_DIAMOND;
      case EXPLOSIVE:
         return RES_ARROW_EXPLOSIVE;
      default:
         return null;
      }
   }
}
