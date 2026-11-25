package vazkii.quark.world.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.world.client.layer.LayerPirateClothes;

public class RenderPirate extends RenderSkeleton {
   private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/pirate.png");
   public static final IRenderFactory<EntitySkeleton> FACTORY = RenderPirate::new;

   public RenderPirate(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.addLayer(new LayerPirateClothes(this));
   }

   protected ResourceLocation getEntityTexture(AbstractSkeleton entity) {
      return TEXTURE;
   }
}
