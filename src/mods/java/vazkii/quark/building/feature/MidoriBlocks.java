package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.BlockMidori;
import vazkii.quark.building.block.BlockMidoriPillar;
import vazkii.quark.building.block.slab.BlockMidoriSlab;
import vazkii.quark.building.block.stairs.BlockMidoriStairs;

public class MidoriBlocks extends Feature {
   public static Block midori_block;
   public static Block midori_pillar;
   public static boolean enableStairsAndSlabs;
   public static boolean enableWalls;

   public MidoriBlocks() {
   }

   public void setupConfig() {
      enableStairsAndSlabs = this.loadPropBool("Enable stairs and slabs", "", true) && GlobalConfig.enableVariants;
      enableWalls = this.loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
   }

   public void preInit(FMLPreInitializationEvent event) {
      midori_block = new BlockMidori();
      midori_pillar = new BlockMidoriPillar();
      if (enableStairsAndSlabs) {
         BlockModSlab slab = new BlockMidoriSlab(false);
         BlockModStairs.initStairs(midori_block, 0, new BlockMidoriStairs());
         BlockModSlab.initSlab(midori_block, 0, slab, new BlockMidoriSlab(true));
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(midori_pillar), new Object[]{"S", "S", 'S', ProxyRegistry.newStack(slab)});
      } else {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(midori_pillar, 2), new Object[]{"M", "M", 'M', ProxyRegistry.newStack(midori_block)});
      }

      VanillaWalls.add("midori_block", midori_block, 0, enableWalls);
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(midori_block, 4), new Object[]{"GG", "GG", 'G', ProxyRegistry.newStack(Items.DYE, 1, 2)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
