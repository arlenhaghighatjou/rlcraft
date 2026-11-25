package mezz.jei.api.recipe;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface IVanillaRecipeFactory {
   IRecipeWrapper createAnvilRecipe(ItemStack var1, List<ItemStack> var2, List<ItemStack> var3);

   IRecipeWrapper createAnvilRecipe(List<ItemStack> var1, List<ItemStack> var2, List<ItemStack> var3);

   IRecipeWrapper createSmeltingRecipe(List<ItemStack> var1, ItemStack var2);

   IRecipeWrapper createBrewingRecipe(List<ItemStack> var1, ItemStack var2, ItemStack var3);
}
