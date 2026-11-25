package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockEnderWatcher;
import vazkii.quark.automation.tile.TileEnderWatcher;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.Biotite;

public class EnderWatcher extends Feature {
   public static Block ender_watcher;

   public EnderWatcher() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      ender_watcher = new BlockEnderWatcher();
      registerTile(TileEnderWatcher.class, "ender_watcher");
      ModIntegrationHandler.addCharsetCarry(ender_watcher);
   }

   public void postPreInit() {
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(ender_watcher), new Object[]{"BRB", "RER", "BRB", 'R', ProxyRegistry.newStack(Items.REDSTONE), 'B', ModuleLoader.isFeatureEnabled(Biotite.class) ? ProxyRegistry.newStack(Biotite.biotite_block) : ProxyRegistry.newStack(Blocks.OBSIDIAN), 'E', ProxyRegistry.newStack(Items.ENDER_EYE)});
   }

   public String[] getIncompatibleMods() {
      return new String[]{"botania"};
   }
}
