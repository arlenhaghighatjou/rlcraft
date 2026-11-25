package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.feature.QuiltedWool;

public class DyeAnyWool extends Feature {
   public static boolean add8WoolRecipe;

   public DyeAnyWool() {
   }

   public void setupConfig() {
      add8WoolRecipe = this.loadPropBool("Add 8 Dyed Wool Recipe", "", true);
   }

   public void postPreInit() {
      for(int i = 0; i < 16; ++i) {
         String dye = (String)LibMisc.OREDICT_DYES.get(15 - i);
         this.addRecipe(Blocks.WOOL, i, dye);
         if (ModuleLoader.isFeatureEnabled(QuiltedWool.class)) {
            this.addRecipe(QuiltedWool.quilted_wool, i, dye);
         }
      }

   }

   private void addRecipe(Block block, int meta, String dye) {
      ItemStack in = ProxyRegistry.newStack(block, 1, 32767);
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(block, 1, meta), new Object[]{in, dye});
      if (add8WoolRecipe) {
         RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(block, 8, meta), new Object[]{dye, in, in, in, in, in, in, in, in});
      }

   }
}
