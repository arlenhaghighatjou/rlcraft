package familiarfauna.core;

import familiarfauna.entities.EntityButterfly;
import familiarfauna.entities.EntityDeer;
import familiarfauna.entities.EntityDragonfly;
import familiarfauna.entities.EntityPixie;
import familiarfauna.entities.EntitySnail;
import familiarfauna.entities.EntityTurkey;
import familiarfauna.entities.render.RenderButterfly;
import familiarfauna.entities.render.RenderDeer;
import familiarfauna.entities.render.RenderDragonfly;
import familiarfauna.entities.render.RenderPixie;
import familiarfauna.entities.render.RenderSnail;
import familiarfauna.entities.render.RenderTurkey;
import familiarfauna.particle.EntityBluePixieTrailFX;
import familiarfauna.particle.EntityPinkPixieTrailFX;
import familiarfauna.particle.EntityPurplePixieTrailFX;
import familiarfauna.particle.FFParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
   public static ResourceLocation particleTexturesLocation = new ResourceLocation("familiarfauna:textures/particles/particles.png");

   public ClientProxy() {
   }

   public void registerRenderers() {
      RenderingRegistry.registerEntityRenderingHandler(EntityButterfly.class, RenderButterfly::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityDragonfly.class, RenderDragonfly::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityPixie.class, RenderPixie::new);
      RenderingRegistry.registerEntityRenderingHandler(EntitySnail.class, RenderSnail::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityDeer.class, RenderDeer::new);
      RenderingRegistry.registerEntityRenderingHandler(EntityTurkey.class, RenderTurkey::new);
   }

   public void registerItemVariantModel(Item item, String name, int metadata) {
      if (item != null) {
         ModelBakery.registerItemVariants(item, new ResourceLocation[]{new ResourceLocation("familiarfauna:" + name)});
         ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation("familiarfauna:" + name, "inventory"));
      }

   }

   public void spawnParticle(FFParticleTypes type, World parWorld, double x, double y, double z, Object... info) {
      Minecraft minecraft = Minecraft.getMinecraft();
      Particle entityFx = null;
      switch(type) {
      case PINK_PIXIE_TRAIL:
         entityFx = new EntityPinkPixieTrailFX(parWorld, x, y, z, MathHelper.nextDouble(parWorld.rand, -0.03D, 0.03D), -0.02D, MathHelper.nextDouble(parWorld.rand, -0.03D, 0.03D));
         break;
      case BLUE_PIXIE_TRAIL:
         entityFx = new EntityBluePixieTrailFX(parWorld, x, y, z, MathHelper.nextDouble(parWorld.rand, -0.03D, 0.03D), -0.02D, MathHelper.nextDouble(parWorld.rand, -0.03D, 0.03D));
         break;
      case PURPLE_PIXIE_TRAIL:
         entityFx = new EntityPurplePixieTrailFX(parWorld, x, y, z, MathHelper.nextDouble(parWorld.rand, -0.03D, 0.03D), -0.02D, MathHelper.nextDouble(parWorld.rand, -0.03D, 0.03D));
      }

      if (entityFx != null) {
         minecraft.effectRenderer.addEffect(entityFx);
      }

   }
}
