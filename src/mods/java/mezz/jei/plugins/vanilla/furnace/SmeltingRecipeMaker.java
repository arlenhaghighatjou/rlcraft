package mezz.jei.plugins.vanilla.furnace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public final class SmeltingRecipeMaker {
   private SmeltingRecipeMaker() {
   }

   public static List<SmeltingRecipe> getFurnaceRecipes(IJeiHelpers helpers) {
      IStackHelper stackHelper = helpers.getStackHelper();
      FurnaceRecipes furnaceRecipes = FurnaceRecipes.instance();
      Map<ItemStack, ItemStack> smeltingMap = furnaceRecipes.getSmeltingList();
      List<SmeltingRecipe> recipes = new ArrayList();

      for(Entry<ItemStack, ItemStack> entry : smeltingMap.entrySet()) {
         ItemStack input = (ItemStack)entry.getKey();
         ItemStack output = (ItemStack)entry.getValue();
         List<ItemStack> inputs = stackHelper.getSubtypes(input);
         SmeltingRecipe recipe = new SmeltingRecipe(inputs, output);
         recipes.add(recipe);
      }

      return recipes;
   }
}
