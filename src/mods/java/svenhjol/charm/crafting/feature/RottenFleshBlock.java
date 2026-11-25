package svenhjol.charm.crafting.feature;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import svenhjol.charm.crafting.block.BlockRottenFlesh;
import svenhjol.meson.Feature;
import svenhjol.meson.handler.RecipeHandler;
import svenhjol.meson.registry.ProxyRegistry;

public class RottenFleshBlock extends Feature {
   public static float hardness;
   public static float resistance;
   public static int harvestLevel;
   public static boolean showParticles;
   public static boolean transformToPodzol;
   public static boolean transformToMycelium;
   public static boolean transformToSoil;
   public static BlockRottenFlesh block;

   public RottenFleshBlock() {
   }

   public String getDescription() {
      return "A storage block for rotten flesh.  If the rotten flesh block has water on any of its sides, it has a chance to turn into dirt.\nIf there is a soil block above the rotten flesh block, it has a chance to turn into podzol.";
   }

   public void configure() {
      super.configure();
      hardness = 1.0F;
      resistance = 3.0F;
      harvestLevel = 1;
      transformToMycelium = true;
      transformToPodzol = true;
      transformToSoil = true;
      showParticles = true;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      block = new BlockRottenFlesh();
      RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(block, 1), "RRR", "RRR", "RRR", 'R', Items.ROTTEN_FLESH);
      RecipeHandler.addShapelessRecipe(ProxyRegistry.newStack(Items.ROTTEN_FLESH, 9), ProxyRegistry.newStack(block));
   }
}
