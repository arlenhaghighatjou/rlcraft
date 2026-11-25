package svenhjol.charm.automation.feature;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import svenhjol.charm.automation.block.BlockRedstoneSand;
import svenhjol.meson.Feature;
import svenhjol.meson.handler.RecipeHandler;
import svenhjol.meson.registry.ProxyRegistry;

public class RedstoneSand extends Feature {
   public static float hardness;
   public static BlockRedstoneSand block;

   public RedstoneSand() {
   }

   public String getDescription() {
      return "A block that acts like sand but is powered like a block of redstone.";
   }

   public void configure() {
      super.configure();
      hardness = 0.5F;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      block = new BlockRedstoneSand();
      RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(block, 1), "RRR", "RSR", "RRR", 'R', Items.REDSTONE, 'S', "sand");
   }
}
