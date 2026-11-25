package mezz.jei.api;

import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public interface IRecipeRegistry {
   List<IRecipeCategory> getRecipeCategories();

   List<IRecipeCategory> getRecipeCategories(List<String> var1);

   @Nullable
   IRecipeCategory getRecipeCategory(String var1);

   <V> IFocus<V> createFocus(IFocus.Mode var1, V var2);

   <V> List<IRecipeCategory> getRecipeCategories(IFocus<V> var1);

   <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> var1, IFocus<V> var2);

   <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> var1);

   @Nullable
   IRecipeWrapper getRecipeWrapper(Object var1, String var2);

   List<Object> getRecipeCatalysts(IRecipeCategory var1);

   @Nullable
   IRecipeTransferHandler getRecipeTransferHandler(Container var1, IRecipeCategory var2);

   @Nullable
   <T extends IRecipeWrapper> IRecipeLayoutDrawable createRecipeLayoutDrawable(IRecipeCategory<T> var1, T var2, IFocus<?> var3);

   void hideRecipe(IRecipeWrapper var1, String var2);

   void unhideRecipe(IRecipeWrapper var1, String var2);

   void hideRecipeCategory(String var1);

   void unhideRecipeCategory(String var1);

   /** @deprecated */
   @Deprecated
   void hideRecipe(IRecipeWrapper var1);

   /** @deprecated */
   @Deprecated
   void unhideRecipe(IRecipeWrapper var1);

   /** @deprecated */
   @Deprecated
   void addRecipe(IRecipeWrapper var1, String var2);

   /** @deprecated */
   @Deprecated
   void removeRecipe(IRecipeWrapper var1, String var2);

   /** @deprecated */
   @Deprecated
   @Nullable
   <T> IRecipeHandler<T> getRecipeHandler(Class<? extends T> var1);

   /** @deprecated */
   @Deprecated
   void addSmeltingRecipe(List<ItemStack> var1, ItemStack var2);

   /** @deprecated */
   @Deprecated
   IRecipeWrapper createSmeltingRecipe(List<ItemStack> var1, ItemStack var2);

   /** @deprecated */
   @Deprecated
   IRecipeWrapper createAnvilRecipe(ItemStack var1, List<ItemStack> var2, List<ItemStack> var3);

   /** @deprecated */
   @Deprecated
   IRecipeWrapper createBrewingRecipe(List<ItemStack> var1, ItemStack var2, ItemStack var3);

   /** @deprecated */
   @Deprecated
   void addRecipe(Object var1);

   /** @deprecated */
   @Deprecated
   void removeRecipe(Object var1);

   /** @deprecated */
   @Deprecated
   List<ItemStack> getCraftingItems(IRecipeCategory var1, @Nullable IFocus var2);

   /** @deprecated */
   @Deprecated
   List<ItemStack> getCraftingItems(IRecipeCategory var1);
}
