package vazkii.quark.decoration.feature;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.BlacklistOreIngredient;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkTrapdoor;
import vazkii.quark.base.handler.RecipeProcessor;
import vazkii.quark.base.module.Feature;

public class VariedTrapdoors extends Feature {
   public static Block spruce_trapdoor;
   public static Block birch_trapdoor;
   public static Block jungle_trapdoor;
   public static Block acacia_trapdoor;
   public static Block dark_oak_trapdoor;
   public static boolean renameVanillaTrapdoor;
   public static int recipeOutput;

   public VariedTrapdoors() {
   }

   public void setupConfig() {
      renameVanillaTrapdoor = this.loadPropBool("Rename vanilla trapdoor to Oak Trapdoor", "", true);
      recipeOutput = this.loadPropInt("Amount of trapdoors crafted (vanilla is 2)", "", 6);
   }

   public void preInit(FMLPreInitializationEvent event) {
      spruce_trapdoor = new BlockQuarkTrapdoor("spruce_trapdoor");
      birch_trapdoor = new BlockQuarkTrapdoor("birch_trapdoor");
      jungle_trapdoor = new BlockQuarkTrapdoor("jungle_trapdoor");
      acacia_trapdoor = new BlockQuarkTrapdoor("acacia_trapdoor");
      dark_oak_trapdoor = new BlockQuarkTrapdoor("dark_oak_trapdoor");
      this.addOreDict();
   }

   public void postPreInit() {
      RecipeProcessor.addWoodReplacements(recipeOutput, Blocks.TRAPDOOR);
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(spruce_trapdoor, recipeOutput), new Object[]{"WWW", "WWW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 1)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(birch_trapdoor, recipeOutput), new Object[]{"WWW", "WWW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 2)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(jungle_trapdoor, recipeOutput), new Object[]{"WWW", "WWW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 3)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(acacia_trapdoor, recipeOutput), new Object[]{"WWW", "WWW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 4)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(dark_oak_trapdoor, recipeOutput), new Object[]{"WWW", "WWW", 'W', ProxyRegistry.newStack(Blocks.PLANKS, 1, 5)});
      if (renameVanillaTrapdoor) {
         Blocks.TRAPDOOR.setUnlocalizedName("oak_trapdoor");
      }

      Ingredient wood = new BlacklistOreIngredient("plankWood", (stack) -> {
         return stack.getItem() == Item.getItemFromBlock(Blocks.PLANKS);
      });
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(Blocks.TRAPDOOR, recipeOutput), new Object[]{"WWW", "WWW", 'W', wood});
   }

   private void addOreDict() {
      addOreDict("trapdoorWood", Blocks.TRAPDOOR);
      addOreDict("trapdoorWood", spruce_trapdoor);
      addOreDict("trapdoorWood", birch_trapdoor);
      addOreDict("trapdoorWood", jungle_trapdoor);
      addOreDict("trapdoorWood", acacia_trapdoor);
      addOreDict("trapdoorWood", dark_oak_trapdoor);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
