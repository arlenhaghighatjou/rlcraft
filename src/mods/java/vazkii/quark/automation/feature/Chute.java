package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockChute;
import vazkii.quark.automation.tile.TileChute;
import vazkii.quark.base.handler.ModIntegrationHandler;
import vazkii.quark.base.module.Feature;

public class Chute extends Feature {
   public static Block chute;

   public Chute() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      chute = new BlockChute();
      registerTile(TileChute.class, "chute");
      ModIntegrationHandler.addCharsetCarry(chute);
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(chute), new Object[]{"WWW", "SWS", " S ", 'W', "plankWood", 'S', "stickWood"});
   }
}
