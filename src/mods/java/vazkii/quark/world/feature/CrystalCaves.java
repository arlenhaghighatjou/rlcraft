package vazkii.quark.world.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.misc.feature.ColorRunes;
import vazkii.quark.world.block.BlockCrystal;
import vazkii.quark.world.block.BlockCrystalPane;
import vazkii.quark.world.world.CrystalCaveGenerator;

public class CrystalCaves extends Feature {
   public static Block crystal;
   public static Block crystalPane;
   public static DimensionConfig dims;
   public static double crystalCaveRarity;
   public static double oreChance;
   public static double crystalRate;
   public static boolean enablePanes;

   public CrystalCaves() {
   }

   public void setupConfig() {
      crystalCaveRarity = this.loadLegacyPropChance("Crystal Cave Percentage Chance", "Crystal Cave Rarity", "The percentage chance crystal caves will spawn in a given chunk", 0.006666666666666667D);
      oreChance = this.loadPropChance("Ore Chance", "Chance that, if a crystal location doesn't spawn a crystal, it will spawn an ore", 0.5D);
      crystalRate = this.loadPropChance("Crystal Chance", "Chance that a given crystal location will actually spawn a crystal", 0.3D);
      enablePanes = this.loadPropBool("Enable Panes", "", true);
      dims = new DimensionConfig(this.configCategory);
   }

   public void preInit(FMLPreInitializationEvent event) {
      crystal = new BlockCrystal();
      if (enablePanes) {
         crystalPane = new BlockCrystalPane();
      }

      GameRegistry.registerWorldGenerator(new CrystalCaveGenerator(dims), 1);
      ModIntegrationHandler.allowChiselAndBitsChiseling(crystal);
   }

   public void postPreInit() {
      if (ModuleLoader.isFeatureEnabled(ColorRunes.class)) {
         this.addRuneRecipe(0, 0);
         this.addRuneRecipe(1, 14);
         this.addRuneRecipe(2, 1);
         this.addRuneRecipe(3, 4);
         this.addRuneRecipe(4, 5);
         this.addRuneRecipe(5, 3);
         this.addRuneRecipe(6, 11);
         this.addRuneRecipe(7, 2);
         this.addRuneRecipe(8, 15);
      }

      if (enablePanes) {
         for(int i = 0; i < BlockCrystal.Variants.values().length; ++i) {
            RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(crystalPane, 16, i), new Object[]{"CCC", "CCC", 'C', ProxyRegistry.newStack(crystal, 1, i)});
         }
      }

   }

   private void addRuneRecipe(int crystalMeta, int runeMeta) {
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(ColorRunes.rune, 1, runeMeta), new Object[]{"CCC", "CSC", "CCC", 'C', ProxyRegistry.newStack(crystal, 1, crystalMeta), 'S', "stone"});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
