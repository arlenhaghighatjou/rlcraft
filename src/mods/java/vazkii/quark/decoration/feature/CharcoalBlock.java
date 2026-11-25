package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockCharcoal;

public class CharcoalBlock extends Feature {
   public static Block charcoal_block;

   public CharcoalBlock() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      charcoal_block = new BlockCharcoal();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(charcoal_block), new Object[]{"CCC", "CCC", "CCC", 'C', ProxyRegistry.newStack(Items.COAL, 1, 1)});
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(Items.COAL, 9, 1), new Object[]{ProxyRegistry.newStack(charcoal_block)});
      addOreDict("blockCharcoal", charcoal_block);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public String[] getIncompatibleMods() {
      return new String[]{"actuallyadditions", "mekanism"};
   }
}
