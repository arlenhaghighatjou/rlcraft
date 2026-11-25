package vazkii.quark.misc.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderParrot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderParrotKoto extends RenderParrot {
   private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/kotobirb.png");

   public RenderParrotKoto(RenderManager manager) {
      super(manager);
   }

   protected ResourceLocation getEntityTexture(EntityParrot entity) {
      int color = entity.getVariant();
      return color == 4 && entity.getUniqueID().getLeastSignificantBits() % 20L == 0L ? TEXTURE : super.getEntityTexture(entity);
   }

   public static IRenderFactory<EntityParrot> factory() {
      return RenderParrotKoto::new;
   }
}
