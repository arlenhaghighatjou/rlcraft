package vazkii.quark.automation.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.automation.block.BlockColorSlime;
import vazkii.quark.base.module.Feature;

public class ColorSlime extends Feature {
   public static Block color_slime;
   public static boolean renameVanillaSlime;

   public ColorSlime() {
   }

   public void setupConfig() {
      renameVanillaSlime = this.loadPropBool("Rename Vanilla Slime", "Set to false to not rename vanilla slime to Green Slime Block", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      color_slime = new BlockColorSlime();
   }

   public void postPreInit() {
      if (renameVanillaSlime) {
         Blocks.SLIME_BLOCK.setUnlocalizedName("green_slime_block");
      }

      addOreDict("blockSlime", ProxyRegistry.newStack(color_slime, 1, 32767));
      addOreDict("blockSlimeGreen", ProxyRegistry.newStack(Blocks.SLIME_BLOCK));
      addOreDict("blockSlimeRed", ProxyRegistry.newStack(color_slime, 1, 0));
      addOreDict("blockSlimeBlue", ProxyRegistry.newStack(color_slime, 1, 1));
      addOreDict("blockSlimeCyan", ProxyRegistry.newStack(color_slime, 1, 2));
      addOreDict("blockSlimeMagenta", ProxyRegistry.newStack(color_slime, 1, 3));
      addOreDict("blockSlimeYellow", ProxyRegistry.newStack(color_slime, 1, 4));
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(Blocks.SLIME_BLOCK), new Object[]{"blockSlime", "dyeLime"});
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(color_slime, 1, 0), new Object[]{"blockSlime", "dyeRed"});
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(color_slime, 1, 1), new Object[]{"blockSlime", "dyeBlue"});
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(color_slime, 2, 2), new Object[]{"blockSlimeGreen", "blockSlimeBlue"});
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(color_slime, 2, 3), new Object[]{"blockSlimeBlue", "blockSlimeRed"});
      RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(color_slime, 2, 4), new Object[]{"blockSlimeRed", "blockSlimeGreen"});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
