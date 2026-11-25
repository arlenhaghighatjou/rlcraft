package de.Whitedraco.switchbow.recipe;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RecipeHelper {
   public RecipeHelper() {
   }

   public static void addShapedRecipe(String name, ItemStack stack, Object... recipeComponents) {
      name = "switchbow".toLowerCase() + ":" + name;
      String s = "";
      int i = 0;
      int j = 0;
      int k = 0;
      if (recipeComponents[i] instanceof String[]) {
         String[] astring = (String[])recipeComponents[i++];

         for(String s2 : astring) {
            ++k;
            j = s2.length();
            s = s + s2;
         }
      } else {
         while(recipeComponents[i] instanceof String) {
            String s1 = (String)recipeComponents[i++];
            ++k;
            j = s1.length();
            s = s + s1;
         }
      }

      Map<Character, ItemStack> map;
      for(map = Maps.newHashMap(); i < recipeComponents.length; i += 2) {
         Character character = (Character)recipeComponents[i];
         ItemStack itemstack = ItemStack.EMPTY;
         if (recipeComponents[i + 1] instanceof Item) {
            itemstack = new ItemStack((Item)recipeComponents[i + 1]);
         } else if (recipeComponents[i + 1] instanceof Block) {
            itemstack = new ItemStack((Block)recipeComponents[i + 1], 1, 32767);
         } else if (recipeComponents[i + 1] instanceof ItemStack) {
            itemstack = (ItemStack)recipeComponents[i + 1];
         }

         map.put(character, itemstack);
      }

      NonNullList<Ingredient> aitemstack = NonNullList.withSize(j * k, Ingredient.EMPTY);

      for(int l = 0; l < j * k; ++l) {
         char c0 = s.charAt(l);
         if (map.containsKey(Character.valueOf(c0))) {
            aitemstack.set(l, Ingredient.fromStacks(new ItemStack[]{((ItemStack)map.get(Character.valueOf(c0))).copy()}));
         }
      }

      ShapedRecipes shapedrecipes = new ShapedRecipes(name, j, k, aitemstack, stack);
      shapedrecipes.setRegistryName(name);
      ForgeRegistries.RECIPES.register(shapedrecipes);
   }

   public static void addShapelessRecipe(String name, ItemStack stack, Object... recipeComponents) {
      name = "switchbow".toLowerCase() + ":" + name;
      NonNullList<Ingredient> list = NonNullList.create();

      for(Object object : recipeComponents) {
         if (object instanceof ItemStack) {
            list.add(Ingredient.fromStacks(new ItemStack[]{((ItemStack)object).copy()}));
         } else if (object instanceof Item) {
            list.add(Ingredient.fromStacks(new ItemStack[]{new ItemStack((Item)object)}));
         } else {
            if (!(object instanceof Block)) {
               throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
            }

            list.add(Ingredient.fromStacks(new ItemStack[]{new ItemStack((Block)object)}));
         }
      }

      ShapelessRecipes shapelessRecipes = new ShapelessRecipes(name, stack, list);
      shapelessRecipes.setRegistryName(name);
      ForgeRegistries.RECIPES.register(shapelessRecipes);
   }
}
