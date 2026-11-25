package vazkii.quark.world.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderDweller extends RenderZombie {
   private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/dweller.png");
   public static final IRenderFactory<EntityZombie> FACTORY = RenderDweller::new;

   public RenderDweller(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected ResourceLocation getEntityTexture(EntityZombie entity) {
      return TEXTURE;
   }
}
