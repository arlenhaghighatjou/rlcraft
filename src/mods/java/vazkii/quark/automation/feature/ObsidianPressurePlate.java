package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockObsidianPressurePlate;
import vazkii.quark.base.module.Feature;

public class ObsidianPressurePlate extends Feature {
   public static Block obsidian_pressure_plate;

   public ObsidianPressurePlate() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      obsidian_pressure_plate = new BlockObsidianPressurePlate();
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(obsidian_pressure_plate), new Object[]{"OO", 'O', ProxyRegistry.newStack(Blocks.OBSIDIAN)});
   }
}
