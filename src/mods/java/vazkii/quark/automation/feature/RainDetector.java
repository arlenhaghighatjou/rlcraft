package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockRainDetector;
import vazkii.quark.automation.tile.TileRainDetector;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.Biotite;

public class RainDetector extends Feature {
   public static Block rain_detector;

   public RainDetector() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      rain_detector = new BlockRainDetector();
      registerTile(TileRainDetector.class, "rain_detector");
      ModIntegrationHandler.addCharsetCarry(rain_detector);
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(rain_detector), new Object[]{"GGG", "BBB", "PPP", 'G', ProxyRegistry.newStack(Blocks.GLASS), 'B', ModuleLoader.isFeatureEnabled(Biotite.class) ? "gemEnderBiotite" : ProxyRegistry.newStack(Blocks.OBSIDIAN), 'P', ProxyRegistry.newStack(Blocks.PURPUR_SLAB)});
   }
}
