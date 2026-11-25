package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockSturdyStone;

public class SturdyStone extends Feature {
   public static Block sturdy_stone;

   public SturdyStone() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      sturdy_stone = new BlockSturdyStone();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(sturdy_stone, 4), new Object[]{"SCS", "C C", "SCS", 'S', "stone", 'C', "cobblestone"});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(sturdy_stone, 4), new Object[]{"CSC", "S S", "CSC", 'S', "stone", 'C', "cobblestone"});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
