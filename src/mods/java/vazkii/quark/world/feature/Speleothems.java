package vazkii.quark.world.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.block.BlockSpeleothem;
import vazkii.quark.world.world.SpeleothemGenerator;

public class Speleothems extends Feature {
   public static Block stone_speleothem;
   public static Block granite_speleothem;
   public static Block diorite_speleothem;
   public static Block andesite_speleothem;
   public static Block basalt_speleothem;
   public static Block marble_speleothem;
   public static Block limestone_speleothem;
   public static Block netherrack_speleothem;
   public static Block jasper_speleothem;
   public static Block slate_speleothem;
   public static int tries;
   public static int clusterCount;
   public static int netherTries;
   public static int netherClusterCount;
   public static int maxHeight;
   public static DimensionConfig dimensionConfig;

   public Speleothems() {
   }

   public void setupConfig() {
      tries = this.loadPropInt("Cluster Attempts Per Chunk", "", 60);
      clusterCount = this.loadPropInt("Speleothems Per Cluster", "", 12);
      netherTries = this.loadPropInt("Cluster Attempts Per Chunk (Nether)", "", 4);
      netherClusterCount = this.loadPropInt("Speleothems Per Cluster (Nether)", "", 12);
      maxHeight = this.loadPropInt("Highest Y Level", "", 55);
      dimensionConfig = new DimensionConfig(this.configCategory, "0,-1");
   }

   public void preInit(FMLPreInitializationEvent event) {
      stone_speleothem = new BlockSpeleothem("stone");
      granite_speleothem = new BlockSpeleothem("granite");
      diorite_speleothem = new BlockSpeleothem("diorite");
      andesite_speleothem = new BlockSpeleothem("andesite");
      netherrack_speleothem = (new BlockSpeleothem("netherrack")).setNetherrack();
      if (ModuleLoader.isFeatureEnabled(Basalt.class)) {
         basalt_speleothem = new BlockSpeleothem("basalt");
      }

      if (ModuleLoader.isFeatureEnabled(RevampStoneGen.class)) {
         if (RevampStoneGen.enableMarble) {
            marble_speleothem = new BlockSpeleothem("marble");
         }

         if (RevampStoneGen.enableLimestone) {
            limestone_speleothem = new BlockSpeleothem("limestone");
         }

         if (RevampStoneGen.enableJasper) {
            jasper_speleothem = new BlockSpeleothem("jasper");
         }

         if (RevampStoneGen.enableSlate) {
            slate_speleothem = new BlockSpeleothem("slate");
         }
      }

      GameRegistry.registerWorldGenerator(new SpeleothemGenerator(), 1000);
   }

   public void postPreInit() {
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(stone_speleothem, 6), new Object[]{"S", "S", "S", 'S', "stone"});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(granite_speleothem, 6), new Object[]{"S", "S", "S", 'S', "stoneGranite"});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(diorite_speleothem, 6), new Object[]{"S", "S", "S", 'S', "stoneDiorite"});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(andesite_speleothem, 6), new Object[]{"S", "S", "S", 'S', "stoneAndesite"});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(netherrack_speleothem, 6), new Object[]{"S", "S", "S", 'S', "netherrack"});
      if (basalt_speleothem != null) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(basalt_speleothem, 6), new Object[]{"S", "S", "S", 'S', "stoneBasalt"});
      }

      if (marble_speleothem != null) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(marble_speleothem, 6), new Object[]{"S", "S", "S", 'S', "stoneMarble"});
      }

      if (limestone_speleothem != null) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(limestone_speleothem, 6), new Object[]{"S", "S", "S", 'S', "stoneLimestone"});
      }

      if (jasper_speleothem != null) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(jasper_speleothem, 6), new Object[]{"S", "S", "S", 'S', "stoneJasper"});
      }

      if (slate_speleothem != null) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(slate_speleothem, 6), new Object[]{"S", "S", "S", 'S', "stoneSlate"});
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
