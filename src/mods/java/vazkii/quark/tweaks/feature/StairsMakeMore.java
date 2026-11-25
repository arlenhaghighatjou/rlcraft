package vazkii.quark.tweaks.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import vazkii.arl.recipe.MultiRecipe;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.quark.base.module.Feature;

public class StairsMakeMore extends Feature {
   public static final Map<IBlockState, ItemStack> stairs = new HashMap();
   public static int targetSize;
   public static int originalSize;
   public static boolean reversionRecipe;
   public static boolean enableSlabToStair;
   private MultiRecipe slabMultiRecipe;
   private MultiRecipe returnMultiRecipe;

   public StairsMakeMore() {
   }

   public void setupConfig() {
      targetSize = this.loadPropInt("Target stack size (must be a divisor of 24 if 'Reversion recipe' is enabled)", "", 8);
      originalSize = this.loadPropInt("Vanilla stack size", "The stack size for the vanilla stair recipe, used for automatically detecting stair recipes", 4);
      reversionRecipe = this.loadPropBool("Add stairs to blocks recipe", "", true);
      enableSlabToStair = this.loadPropBool("Enable Slab to Stairs Recipe", "This recipe can only be enabled if the \"Slabs to blocks recipe\" feature is.", true);
   }

   public void postPreInit() {
      if (enableSlabToStair) {
         this.slabMultiRecipe = new MultiRecipe(new ResourceLocation("quark", "slabs_to_stairs"));
      }

      if (reversionRecipe) {
         this.returnMultiRecipe = new MultiRecipe(new ResourceLocation("quark", "stairs_to_blocks"));
      }

   }

   public static ItemStack findResult(NonNullList<Ingredient> ingredients, int expected) {
      ItemStack outStack = ItemStack.EMPTY;
      int inputItems = 0;

      for(Ingredient ingredient : ingredients) {
         ItemStack recipeItem = ItemStack.EMPTY;
         ItemStack[] matches = ingredient.getMatchingStacks();
         if (matches.length > 0) {
            recipeItem = matches[0];
         }

         if (recipeItem != null && !recipeItem.isEmpty()) {
            if (outStack.isEmpty()) {
               outStack = recipeItem;
            }

            if (!ItemStack.areItemsEqual(outStack, recipeItem)) {
               outStack = ItemStack.EMPTY;
               break;
            }

            ++inputItems;
         }
      }

      return inputItems != expected ? ItemStack.EMPTY : outStack;
   }

   public void postInit() {
      for(ResourceLocation res : new ArrayList(CraftingManager.REGISTRY.getKeys())) {
         IRecipe recipe = (IRecipe)Objects.requireNonNull(CraftingManager.REGISTRY.getObject(res));
         ItemStack output = recipe.getRecipeOutput();
         if (!output.isEmpty() && output.getCount() == originalSize) {
            Item outputItem = output.getItem();
            Block outputBlock = Block.getBlockFromItem(outputItem);
            if (outputBlock instanceof BlockStairs) {
               output.setCount(targetSize);
               if (recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe) {
                  NonNullList<Ingredient> recipeItems;
                  if (recipe instanceof ShapedRecipes) {
                     recipeItems = ((ShapedRecipes)recipe).recipeItems;
                  } else {
                     recipeItems = recipe.getIngredients();
                  }

                  ItemStack outStack = findResult(recipeItems, 6);
                  if (!outStack.isEmpty()) {
                     ItemStack outCopy = outStack.copy();
                     if (outCopy.getItemDamage() == 32767) {
                        outCopy.setItemDamage(0);
                     }

                     outCopy.setCount(24 / targetSize);
                     ItemStack in = output.copy();
                     in.setCount(1);
                     if (in.getItem() instanceof ItemBlock && outCopy.getItem() instanceof ItemBlock) {
                        Block block = Block.getBlockFromItem(outCopy.getItem());
                        stairs.put(block.getStateFromMeta(outCopy.getItemDamage()), in);
                     }

                     if (reversionRecipe) {
                        RecipeHandler.addShapelessOreDictRecipe(this.returnMultiRecipe, outCopy, new Object[]{in, in, in, in});
                     }
                  }
               }
            }
         }
      }

   }

   public void finalInit() {
      if (enableSlabToStair && !stairs.isEmpty() && !SlabsToBlocks.slabs.isEmpty()) {
         for(IBlockState state : stairs.keySet()) {
            if (SlabsToBlocks.slabs.containsKey(state)) {
               ItemStack stair = ((ItemStack)stairs.get(state)).copy();
               if (!stair.isEmpty()) {
                  stair.setCount(targetSize / 2);
                  ItemStack slab = (ItemStack)SlabsToBlocks.slabs.get(state);
                  RecipeHandler.addOreDictRecipe(this.slabMultiRecipe, stair, new Object[]{"S  ", "SS ", "SSS", 'S', slab});
               }
            }
         }
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
