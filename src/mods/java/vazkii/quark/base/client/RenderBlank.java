package vazkii.quark.base.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderBlank<T extends Entity> extends Render<T> {
   public RenderBlank(RenderManager renderManager) {
      super(renderManager);
   }

   @Nullable
   protected ResourceLocation getEntityTexture(@Nonnull T entity) {
      return null;
   }
}
