package artifacts.client.renderer;

import artifacts.client.model.entity.ModelHallowStar;
import artifacts.common.entity.EntityHallowStar;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@ParametersAreNonnullByDefault
public class RenderHallowStar extends Render<EntityHallowStar> {
   public static final RenderHallowStar.Factory FACTORY = new RenderHallowStar.Factory();
   private static final ResourceLocation TEXTURES = new ResourceLocation("artifacts", "textures/entity/hallow_star/hallow_star.png");
   private static final ModelBase MODEL = new ModelHallowStar();

   public RenderHallowStar(RenderManager manager) {
      super(manager);
      this.shadowSize = 0.3F;
   }

   public void doRender(EntityHallowStar entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      this.bindEntityTexture(entity);
      GlStateManager.translate((float)x, (float)y, (float)z);
      MODEL.render(entity, 0.0F, 0.0F, ((float)entity.ticksAlive + partialTicks) * 30.0F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   @Nullable
   protected ResourceLocation getEntityTexture(EntityHallowStar entity) {
      return TEXTURES;
   }

   public static class Factory implements IRenderFactory<EntityHallowStar> {
      public Factory() {
      }

      public Render<? super EntityHallowStar> createRenderFor(RenderManager manager) {
         return new RenderHallowStar(manager);
      }
   }
}
