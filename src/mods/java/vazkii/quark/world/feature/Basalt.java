package vazkii.quark.world.feature;

import java.util.function.Supplier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockBasalt;
import vazkii.quark.world.block.slab.BlockBasicStoneSlab;
import vazkii.quark.world.world.BasaltGenerator;

public class Basalt extends Feature {
   public static BlockMod basalt;
   public static RevampStoneGen.StoneInfo basaltInfo;
   public static boolean enableStairsAndSlabs;
   public static boolean enableWalls;

   public Basalt() {
   }

   public void setupConfig() {
      basaltInfo = RevampStoneGen.loadStoneInfo(this.configCategory, "basalt", 18, 20, 120, 20, true, "-1", Type.NETHER);
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
      enableWalls = this.loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      basalt = new BlockBasalt();
      if (enableStairsAndSlabs) {
         BlockBasicStoneSlab.initSlab(basalt, 0, "stone_basalt_slab");
         BlockModStairs.initStairs(basalt, 0, new BlockQuarkStairs("stone_basalt_stairs", basalt.getDefaultState()));
      }

      VanillaWalls.add("basalt", basalt, 0, enableWalls);
      GameRegistry.registerWorldGenerator(new BasaltGenerator(() -> {
         return basaltInfo;
      }), 0);
      addOreDict("stoneBasalt", ProxyRegistry.newStack(basalt, 1, 0));
      addOreDict("stoneBasaltPolished", ProxyRegistry.newStack(basalt, 1, 1));
   }

   public void postPreInit() {
      Object blackItem = ProxyRegistry.newStack(Items.COAL);
      if (ModuleLoader.isFeatureEnabled(Biotite.class)) {
         blackItem = "gemBiotite";
      }

      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(basalt, 4, 0), new Object[]{"BI", "IB", 'B', "cobblestone", 'I', blackItem});
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(Blocks.STONE, 1, 5), new Object[]{"stoneBasalt", "gemQuartz"});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(basalt, 4, 1), new Object[]{"BB", "BB", 'B', "stoneBasalt"});
   }

   public void init() {
      ModIntegrationHandler.registerChiselVariant("basalt", new ItemStack(basalt, 1, 0));
      ModIntegrationHandler.registerChiselVariant("basalt", new ItemStack(basalt, 1, 1));
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
