package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockRedstoneInductor;
import vazkii.quark.automation.tile.TileInductor;
import vazkii.quark.base.module.Feature;

public class RedstoneInductor extends Feature {
   public static Block redstone_inductor;

   public RedstoneInductor() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      redstone_inductor = new BlockRedstoneInductor();
      GameRegistry.registerTileEntity(TileInductor.class, new ResourceLocation("quark", "inductor"));
   }

   public void postPreInit() {
      RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(redstone_inductor), new Object[]{"RRM", "SSS", 'M', ProxyRegistry.newStack(Items.MAGMA_CREAM), 'R', "dustRedstone", 'S', "stone"});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
