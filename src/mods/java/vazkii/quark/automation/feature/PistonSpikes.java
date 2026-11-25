package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockIronRod;
import vazkii.quark.base.module.Feature;

public class PistonSpikes extends Feature {
   public static Block iron_rod;
   public static boolean ezRecipe;

   public PistonSpikes() {
   }

   public void setupConfig() {
      ezRecipe = this.loadPropBool("Enable Easy Recipe", "Replace the End Rod in the recipe with an Iron Ingot", false);
   }

   public void preInit(FMLPreInitializationEvent event) {
      iron_rod = new BlockIronRod();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(iron_rod), new Object[]{"I", "I", "R", 'I', "ingotIron", 'R', ezRecipe ? "ingotIron" : Blocks.END_ROD});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
