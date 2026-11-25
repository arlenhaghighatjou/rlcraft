package vazkii.quark.client.render.random;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.client.feature.RandomAnimalTextures;

public class RenderPigRandom extends RenderPig {
   public RenderPigRandom(RenderManager manager) {
      super(manager);
   }

   protected ResourceLocation getEntityTexture(EntityPig entity) {
      return RandomAnimalTextures.getRandomTexture(entity, RandomAnimalTextures.RandomTextureType.PIG);
   }

   public static IRenderFactory<EntityPig> factory() {
      return RenderPigRandom::new;
   }
}
