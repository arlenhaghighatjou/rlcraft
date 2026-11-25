package vazkii.quark.oddities.feature;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.module.Feature;
import vazkii.quark.decoration.feature.MoreBanners;
import vazkii.quark.oddities.block.BlockTinyPotato;
import vazkii.quark.oddities.client.render.RenderTileTinyPotato;
import vazkii.quark.oddities.tile.TileTinyPotato;

public class TinyPotato extends Feature {
   public static boolean enableBannerVariant;
   public static BlockTinyPotato tiny_potato;

   public TinyPotato() {
   }

   public void setupConfig() {
      enableBannerVariant = this.loadPropBool("Enable Banner Variant", "Should the potato get a banner variant?", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      tiny_potato = new BlockTinyPotato();
      GameRegistry.registerTileEntity(TileTinyPotato.class, new ResourceLocation("quark", "tiny_potato"));
      RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(tiny_potato), new Object[]{"D", "P", 'D', "gemDiamond", 'P', "cropPotato"});
      RecipeHandler.addShapedRecipe(ProxyRegistry.newStack(tiny_potato), new Object[]{"D", "P", 'D', "gemEmerald", 'P', "cropPotato"});
   }

   public void init() {
      MoreBanners.addPattern(enableBannerVariant, "tiny_potato", "tp", ProxyRegistry.newStack(tiny_potato));
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      ClientRegistry.bindTileEntitySpecialRenderer(TileTinyPotato.class, new RenderTileTinyPotato());
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public String[] getIncompatibleMods() {
      return new String[]{"botania"};
   }
}
