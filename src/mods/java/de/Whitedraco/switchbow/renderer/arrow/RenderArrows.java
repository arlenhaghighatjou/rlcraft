package de.Whitedraco.switchbow.renderer.arrow;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;

public class RenderArrows<T extends EntityArrow> extends RenderArrow<T> {
   public ResourceLocation RES_ARROW;

   public RenderArrows(RenderManager manager, ResourceLocation texture) {
      super(manager);
      this.RES_ARROW = texture;
   }

   protected ResourceLocation getEntityTexture(T entity) {
      return this.RES_ARROW;
   }
}
