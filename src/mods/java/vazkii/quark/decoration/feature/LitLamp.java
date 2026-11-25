package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockLitLamp;

public class LitLamp extends Feature {
   public static Block lit_lamp;

   public LitLamp() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      lit_lamp = new BlockLitLamp();
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(lit_lamp), new Object[]{ProxyRegistry.newStack(Blocks.REDSTONE_LAMP), ProxyRegistry.newStack(Blocks.REDSTONE_TORCH)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
