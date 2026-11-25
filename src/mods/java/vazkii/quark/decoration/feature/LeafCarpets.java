package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockLeafCarpet;

public class LeafCarpets extends Feature {
   public static Block leaf_carpet;

   public LeafCarpets() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      leaf_carpet = new BlockLeafCarpet();
   }

   public void postPreInit() {
      BlockLeafCarpet.Variants[] variants = BlockLeafCarpet.Variants.values();

      for(int i = 0; i < variants.length; ++i) {
         ItemStack stack = variants[i].getBaseStack();
         if (!stack.isEmpty()) {
            RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(leaf_carpet, 3, i), new Object[]{"LL", 'L', stack.copy()});
         }
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
