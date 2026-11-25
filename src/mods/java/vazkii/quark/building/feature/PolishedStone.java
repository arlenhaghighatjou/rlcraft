package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.block.BlockPolishedStone;

public class PolishedStone extends Feature {
   public static Block polished_stone;

   public PolishedStone() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      polished_stone = new BlockPolishedStone();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(polished_stone, 2), new Object[]{"SS", "SS", 'S', ProxyRegistry.newStack(Blocks.STONE_SLAB)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.STONE_SLAB, 6), new Object[]{"SSS", 'S', ProxyRegistry.newStack(polished_stone)});
      addOreDict("stonePolished", polished_stone);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
