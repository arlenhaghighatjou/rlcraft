package vazkii.quark.client.render.random;

import net.minecraft.client.renderer.entity.RenderCow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.client.feature.RandomAnimalTextures;

public class RenderCowRandom extends RenderCow {
   public RenderCowRandom(RenderManager manager) {
      super(manager);
   }

   protected ResourceLocation getEntityTexture(EntityCow entity) {
      return RandomAnimalTextures.getRandomTexture(entity, RandomAnimalTextures.RandomTextureType.COW);
   }

   public static IRenderFactory<EntityCow> factory() {
      return RenderCowRandom::new;
   }
}
