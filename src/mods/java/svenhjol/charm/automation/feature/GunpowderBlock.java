package svenhjol.charm.automation.feature;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import svenhjol.charm.automation.block.BlockGunpowder;
import svenhjol.meson.Feature;
import svenhjol.meson.handler.RecipeHandler;
import svenhjol.meson.registry.ProxyRegistry;

public class GunpowderBlock extends Feature {
   public static float hardness;
   public static BlockGunpowder block;

   public GunpowderBlock() {
   }

   public String getDescription() {
      return "A storage block for gunpowder.  Like Quark's sugar block, but dissolves in lava.";
   }

   public void configure() {
      super.configure();
      super.configure();
      hardness = 0.5F;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      block = new BlockGunpowder();
      RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(block, 1), "GGG", "GGG", "GGG", 'G', Items.GUNPOWDER);
      RecipeHandler.addShapelessRecipe(ProxyRegistry.newStack(Items.GUNPOWDER, 9), ProxyRegistry.newStack(block));
   }
}
