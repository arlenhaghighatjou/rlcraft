package c4.champions.client.renderer;

import c4.champions.common.entity.EntityCinderSpark;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderCinderSpark extends AbstractRenderSpark<EntityCinderSpark> {
   public static final RenderCinderSpark.Factory FACTORY = new RenderCinderSpark.Factory();
   private static final ResourceLocation SPARK_TEXTURE = new ResourceLocation("champions", "textures/entity/cinderspark.png");

   public RenderCinderSpark(RenderManager manager) {
      super(manager);
   }

   protected ResourceLocation getEntityTexture(@Nonnull EntityCinderSpark entity) {
      return SPARK_TEXTURE;
   }

   public static class Factory implements IRenderFactory<EntityCinderSpark> {
      public Factory() {
      }

      public Render<? super EntityCinderSpark> createRenderFor(RenderManager manager) {
         return new RenderCinderSpark(manager);
      }
   }
}
