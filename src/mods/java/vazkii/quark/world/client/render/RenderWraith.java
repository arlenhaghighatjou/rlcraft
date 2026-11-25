package vazkii.quark.world.client.render;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.world.client.model.ModelWraith;
import vazkii.quark.world.entity.EntityWraith;

public class RenderWraith extends RenderLiving<EntityWraith> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/wraith.png");
   public static final IRenderFactory<EntityWraith> FACTORY = RenderWraith::new;

   public RenderWraith(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelWraith(), 0.0F);
   }

   protected ResourceLocation getEntityTexture(@Nonnull EntityWraith entity) {
      return TEXTURE;
   }

   public void doRenderShadowAndFire(@Nonnull Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
   }
}
