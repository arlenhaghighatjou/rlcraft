package goblinbob.mobends.standard.client.renderer.entity.mutated;

import goblinbob.mobends.core.data.EntityData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class PlayerRenderer extends BipedRenderer<AbstractClientPlayer> {
   public PlayerRenderer() {
   }

   protected void transformLocally(AbstractClientPlayer entity, EntityData<?> data, float partialTicks) {
      if (entity.isSneaking()) {
         if (entity.capabilities.isFlying) {
            GlStateManager.translate(0.0F, 0.25F, 0.0F);
         } else {
            GlStateManager.translate(0.0F, 0.3125F, 0.0F);
         }
      }

   }
}
