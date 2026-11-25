package vazkii.quark.building.feature;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockMod;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockHardenedClayTiles;
import vazkii.quark.building.block.BlockStainedClayTiles;
import vazkii.quark.building.block.slab.BlockHardenedClayTilesSlab;
import vazkii.quark.building.block.slab.BlockStainedClayTilesSlab;
import vazkii.quark.building.block.stairs.BlockHardenedClayTilesStairs;
import vazkii.quark.building.block.stairs.BlockStainedClayTilesStairs;

public class HardenedClayTiles extends Feature {
   public static BlockMod hardened_clay_tiles;
   public static BlockMod stained_clay_tiles;
   public static boolean enableStainedClay;
   public static boolean enableStairsAndSlabs;

   public HardenedClayTiles() {
   }

   public void setupConfig() {
      enableStainedClay = this.loadPropBool("Enable stained tiles", "", true) && GlobalConfig.enableVariants;
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      hardened_clay_tiles = new BlockHardenedClayTiles();
      if (enableStairsAndSlabs) {
         BlockModStairs.initStairs(hardened_clay_tiles, 0, new BlockHardenedClayTilesStairs());
         BlockModSlab.initSlab(hardened_clay_tiles, 0, new BlockHardenedClayTilesSlab(false), new BlockHardenedClayTilesSlab(true));
      }

      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(hardened_clay_tiles, 4, 0), new Object[]{"BB", "BB", 'B', ProxyRegistry.newStack(Blocks.HARDENED_CLAY)});
      if (enableStainedClay) {
         stained_clay_tiles = new BlockStainedClayTiles();
         if (enableStairsAndSlabs) {
            for(BlockStainedClayTiles.Variants variant : BlockStainedClayTiles.Variants.values()) {
               BlockModStairs.initStairs(stained_clay_tiles, variant.ordinal(), new BlockStainedClayTilesStairs(variant));
            }

            for(BlockStainedClayTiles.Variants variant : BlockStainedClayTiles.Variants.values()) {
               BlockModSlab.initSlab(stained_clay_tiles, variant.ordinal(), new BlockStainedClayTilesSlab(variant, false), new BlockStainedClayTilesSlab(variant, true));
            }
         }

         for(int i = 0; i < 16; ++i) {
            RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(stained_clay_tiles, 4, i), new Object[]{"BB", "BB", 'B', ProxyRegistry.newStack(Blocks.STAINED_HARDENED_CLAY, 1, i)});
            RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(stained_clay_tiles, 8, i), new Object[]{"BBB", "BDB", "BBB", 'B', ProxyRegistry.newStack(hardened_clay_tiles, 1), 'D', LibMisc.OREDICT_DYES.get(15 - i)});
         }
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
