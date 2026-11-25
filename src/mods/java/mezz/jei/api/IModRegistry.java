package mezz.jei.api;

import java.util.Collection;
import java.util.List;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.gui.IGhostIngredientHandler;
import mezz.jei.api.gui.IGlobalGuiHandler;
import mezz.jei.api.gui.IGuiScreenHandler;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

public interface IModRegistry {
   IJeiHelpers getJeiHelpers();

   IIngredientRegistry getIngredientRegistry();

   void addRecipes(Collection<?> var1, String var2);

   <T> void handleRecipes(Class<T> var1, IRecipeWrapperFactory<T> var2, String var3);

   void addRecipeClickArea(Class<? extends GuiContainer> var1, int var2, int var3, int var4, int var5, String... var6);

   void addRecipeCatalyst(Object var1, String... var2);

   void addAdvancedGuiHandlers(IAdvancedGuiHandler<?>... var1);

   void addGlobalGuiHandlers(IGlobalGuiHandler... var1);

   <T extends GuiScreen> void addGuiScreenHandler(Class<T> var1, IGuiScreenHandler<T> var2);

   <T extends GuiScreen> void addGhostIngredientHandler(Class<T> var1, IGhostIngredientHandler<T> var2);

   <T> void addIngredientInfo(T var1, IIngredientType<T> var2, String... var3);

   <T> void addIngredientInfo(List<T> var1, IIngredientType<T> var2, String... var3);

   IRecipeTransferRegistry getRecipeTransferRegistry();

   void addRecipeRegistryPlugin(IRecipeRegistryPlugin var1);

   /** @deprecated */
   @Deprecated
   <T> void addIngredientInfo(List<T> var1, Class<? extends T> var2, String... var3);

   /** @deprecated */
   @Deprecated
   <T> void addIngredientInfo(T var1, Class<? extends T> var2, String... var3);

   /** @deprecated */
   @Deprecated
   void addRecipeCategories(IRecipeCategory... var1);

   /** @deprecated */
   @Deprecated
   void addRecipeHandlers(IRecipeHandler... var1);

   /** @deprecated */
   @Deprecated
   void addRecipes(Collection var1);

   /** @deprecated */
   @Deprecated
   void addRecipeCategoryCraftingItem(ItemStack var1, String... var2);

   /** @deprecated */
   @Deprecated
   void addDescription(ItemStack var1, String... var2);

   /** @deprecated */
   @Deprecated
   void addDescription(List<ItemStack> var1, String... var2);

   /** @deprecated */
   @Deprecated
   void addAnvilRecipe(ItemStack var1, List<ItemStack> var2, List<ItemStack> var3);
}
