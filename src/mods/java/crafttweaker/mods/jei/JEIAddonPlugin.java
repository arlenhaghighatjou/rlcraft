package crafttweaker.mods.jei;

import java.util.Collections;
import java.util.List;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEIAddonPlugin implements IModPlugin {
   public static IJeiHelpers jeiHelpers;
   public static IIngredientRegistry itemRegistry;
   public static IRecipeRegistry recipeRegistry;
   public static IIngredientHelper<ItemStack> ingredientHelper;
   public static IModRegistry modRegistry;
   public static IJeiRuntime jeiRuntime;

   public JEIAddonPlugin() {
   }

   public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
   }

   public void registerIngredients(IModIngredientRegistration registry) {
   }

   public void registerCategories(IRecipeCategoryRegistration registry) {
   }

   public void register(IModRegistry registry) {
      jeiHelpers = registry.getJeiHelpers();
      itemRegistry = registry.getIngredientRegistry();
      ingredientHelper = itemRegistry.getIngredientHelper(VanillaTypes.ITEM);
      modRegistry = registry;
      JEIMod.onRegistered();
   }

   public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
      recipeRegistry = iJeiRuntime.getRecipeRegistry();
      jeiRuntime = iJeiRuntime;
      JEIMod.applyActions();
   }

   public static List<ItemStack> getSubTypes(ItemStack stack) {
      return ingredientHelper == null ? Collections.singletonList(stack) : ingredientHelper.expandSubtypes(Collections.singletonList(stack));
   }
}
