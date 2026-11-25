package betterquesting.client.renderer;

import betterquesting.api.placeholders.EntityPlaceholder;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;

public class EntityPlaceholderRenderer extends Render<EntityPlaceholder> {
   protected EntityPlaceholderRenderer(RenderManager renderManager) {
      super(renderManager);
   }

   public void doRender(EntityPlaceholder entity, double x, double y, double z, float yaw, float partialTick) {
      EntityItem item = entity.GetItemEntity();
      this.renderManager.renderEntity(item, x, y + 1.0D, z, yaw, partialTick, false);
   }

   protected ResourceLocation getEntityTexture(EntityPlaceholder entity) {
      return null;
   }
}
