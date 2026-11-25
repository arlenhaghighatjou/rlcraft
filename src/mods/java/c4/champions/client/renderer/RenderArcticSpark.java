package c4.champions.client.renderer;

import c4.champions.common.entity.EntityArcticSpark;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderArcticSpark extends AbstractRenderSpark<EntityArcticSpark> {
   public static final RenderArcticSpark.Factory FACTORY = new RenderArcticSpark.Factory();
   private static final ResourceLocation SPARK_TEXTURE = new ResourceLocation("champions", "textures/entity/arcticspark.png");

   public RenderArcticSpark(RenderManager manager) {
      super(manager);
   }

   protected ResourceLocation getEntityTexture(@Nonnull EntityArcticSpark entity) {
      return SPARK_TEXTURE;
   }

   public static class Factory implements IRenderFactory<EntityArcticSpark> {
      public Factory() {
      }

      public Render<? super EntityArcticSpark> createRenderFor(RenderManager manager) {
         return new RenderArcticSpark(manager);
      }
   }
}
