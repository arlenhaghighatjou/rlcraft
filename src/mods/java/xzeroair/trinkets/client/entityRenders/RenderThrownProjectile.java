package xzeroair.trinkets.client.entityRenders;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xzeroair.trinkets.entity.MovingThrownProjectile;

public class RenderThrownProjectile<T extends Entity> extends Render<T> {
   private static final ResourceLocation DRAGON_FIREBALL_TEXTURE = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");
   private static final ResourceLocation TEXTURES = new ResourceLocation("xat", "textures/particle/dragon_breath.png");
   public static final RenderThrownProjectile.Factory FACTORY = new RenderThrownProjectile.Factory();

   protected RenderThrownProjectile(RenderManager renderManager) {
      super(renderManager);
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
   }

   public ResourceLocation getEntityTexture(T entity) {
      return null;
   }

   public static class Factory implements IRenderFactory<MovingThrownProjectile> {
      public Factory() {
      }

      public Render<? super MovingThrownProjectile> createRenderFor(RenderManager manager) {
         return new RenderThrownProjectile<MovingThrownProjectile>(manager);
      }
   }
}
