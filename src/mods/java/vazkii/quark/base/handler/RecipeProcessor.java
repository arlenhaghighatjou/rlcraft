package vazkii.quark.base.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.util.ProxyRegistry;

public class RecipeProcessor {
   private static Function<ItemStack, Integer> compositeFunction = (stack) -> {
      return Integer.valueOf(0);
   };
   private static final List<Consumer<IRecipe>> recipeConsumers = new ArrayList();

   public RecipeProcessor() {
   }

   public static void addReplacementFunction(Function<ItemStack, Integer> f) {
      Function<ItemStack, Integer> curr = compositeFunction;
      compositeFunction = (stack) -> {
         int res = ((Integer)f.apply(stack)).intValue();
         return (void)(res == 0 ? (Integer)curr.apply(stack) : res);
      };
   }

   public static void addWoodReplacements(int size, Block... blocks) {
      for(Block b : blocks) {
         addReplacementFunction((stack) -> {
            return stack.getItem() == Item.getItemFromBlock(b) ? size : 0;
         });
      }

   }

   public static void addWoodReplacements(Block... blocks) {
      addWoodReplacements(1, blocks);
   }

   public static void addConsumer(Consumer<IRecipe> consumer) {
      recipeConsumers.add(consumer);
   }

   public static void runConsumers() {
      for(ResourceLocation res : new ArrayList(CraftingManager.REGISTRY.getKeys())) {
         IRecipe recipe = (IRecipe)CraftingManager.REGISTRY.getObject(res);

         for(Consumer<IRecipe> consumer : recipeConsumers) {
            consumer.accept(recipe);
         }
      }

   }

   private static void executeWoodReplacementsOverRecipe(IRecipe recipe) {
      ItemStack out = recipe.getRecipeOutput();
      if (recipe instanceof ShapedRecipes && !out.isEmpty()) {
         int finalSize = ((Integer)compositeFunction.apply(out)).intValue();
         if (finalSize > 0) {
            ShapedRecipes shaped = (ShapedRecipes)recipe;
            NonNullList<Ingredient> ingredients = shaped.recipeItems;

            for(int i = 0; i < ingredients.size(); ++i) {
               Ingredient ingredient = (Ingredient)ingredients.get(i);
               if (ingredient.apply(ProxyRegistry.newStack(Blocks.PLANKS))) {
                  ingredients.set(i, Ingredient.fromStacks(new ItemStack[]{ProxyRegistry.newStack(Blocks.PLANKS, 1, 0)}));
               }
            }

            out.setCount(finalSize);
         }
      }

   }

   static {
      addConsumer(RecipeProcessor::executeWoodReplacementsOverRecipe);
   }
}
