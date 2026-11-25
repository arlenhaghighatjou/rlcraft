package vazkii.quark.decoration.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.block.BlockNetherBrickFenceGate;

public class NetherBrickFenceGate extends Feature {
   public static Block nether_brick_fence_gate;

   public NetherBrickFenceGate() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      nether_brick_fence_gate = new BlockNetherBrickFenceGate();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(nether_brick_fence_gate, 2), new Object[]{"NBN", "NBN", 'N', ProxyRegistry.newStack(Blocks.NETHER_BRICK_FENCE), 'B', ProxyRegistry.newStack(Blocks.NETHER_BRICK)});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
