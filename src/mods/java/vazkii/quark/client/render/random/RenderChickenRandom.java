package vazkii.quark.client.render.random;

import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.client.feature.RandomAnimalTextures;

public class RenderChickenRandom extends RenderChicken {
   public RenderChickenRandom(RenderManager manager) {
      super(manager);
   }

   protected ResourceLocation getEntityTexture(EntityChicken entity) {
      return RandomAnimalTextures.enableChick && entity.isChild() ? RandomAnimalTextures.getRandomTexture(entity, RandomAnimalTextures.RandomTextureType.CHICK) : RandomAnimalTextures.getRandomTexture(entity, RandomAnimalTextures.RandomTextureType.CHICKEN, RandomAnimalTextures.enableChicken);
   }

   public static IRenderFactory<EntityChicken> factory() {
      return RenderChickenRandom::new;
   }
}
