package mezz.jei.startup;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.gui.IGhostIngredientHandler;
import mezz.jei.api.gui.IGlobalGuiHandler;
import mezz.jei.api.gui.IGuiScreenHandler;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.collect.ListMultiMap;
import mezz.jei.collect.SetMultiMap;
import mezz.jei.gui.recipes.RecipeClickableArea;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.plugins.jei.info.IngredientInfoRecipe;
import mezz.jei.plugins.vanilla.anvil.AnvilRecipeWrapper;
import mezz.jei.recipes.RecipeRegistry;
import mezz.jei.recipes.RecipeTransferRegistry;
import mezz.jei.runtime.JeiHelpers;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;

public class ModRegistry implements IModRegistry, IRecipeCategoryRegistration {
   private final IJeiHelpers jeiHelpers;
   private final IIngredientRegistry ingredientRegistry;
   private final List<IRecipeCategory> recipeCategories = new ArrayList();
   private final Set<String> recipeCategoryUids = new HashSet();
   /** @deprecated */
   @Deprecated
   private final List<IRecipeHandler> unsortedRecipeHandlers = new ArrayList();
   private final ListMultiMap<String, IRecipeHandler> recipeHandlers = new ListMultiMap<String, IRecipeHandler>();
   private final SetMultiMap<String, Class> recipeHandlerClasses = new SetMultiMap<String, Class>();
   private final List<IAdvancedGuiHandler<?>> advancedGuiHandlers = new ArrayList();
   private final List<IGlobalGuiHandler> globalGuiHandlers = new ArrayList();
   private final Map<Class, IGuiScreenHandler> guiScreenHandlers = new HashMap();
   private final Map<Class, IGhostIngredientHandler> ghostIngredientHandlers = new HashMap();
   /** @deprecated */
   @Deprecated
   private final List<Object> unsortedRecipes = new ArrayList();
   private final ListMultiMap<String, Object> recipes = new ListMultiMap<String, Object>();
   private final RecipeTransferRegistry recipeTransferRegistry;
   private final ListMultiMap<Class<? extends GuiContainer>, RecipeClickableArea> recipeClickableAreas = new ListMultiMap<Class<? extends GuiContainer>, RecipeClickableArea>();
   private final ListMultiMap<String, Object> recipeCatalysts = new ListMultiMap<String, Object>();
   private final List<IRecipeRegistryPlugin> recipeRegistryPlugins = new ArrayList();
   private static final List<Class<? extends GuiScreen>> ghostIngredientGuiBlacklist = ImmutableList.of(GuiScreen.class, GuiInventory.class, GuiContainerCreative.class);

   public ModRegistry(JeiHelpers jeiHelpers, IIngredientRegistry ingredientRegistry) {
      this.jeiHelpers = jeiHelpers;
      this.ingredientRegistry = ingredientRegistry;
      this.recipeTransferRegistry = new RecipeTransferRegistry(jeiHelpers.getStackHelper(), jeiHelpers.recipeTransferHandlerHelper());
   }

   public IJeiHelpers getJeiHelpers() {
      return this.jeiHelpers;
   }

   public IIngredientRegistry getIngredientRegistry() {
      return this.ingredientRegistry;
   }

   /** @deprecated */
   @Deprecated
   public void addRecipeCategories(IRecipeCategory... recipeCategories) {
      ErrorUtil.checkNotEmpty(recipeCategories, "recipeCategories");

      for(IRecipeCategory recipeCategory : recipeCategories) {
         String uid = recipeCategory.getUid();
         Preconditions.checkNotNull(uid, "Recipe category UID cannot be null %s", recipeCategory);
         if (!this.recipeCategoryUids.add(uid)) {
            throw new IllegalArgumentException("A RecipeCategory with UID \"" + uid + "\" has already been registered.");
         }
      }

      Collections.addAll(this.recipeCategories, recipeCategories);
   }

   /** @deprecated */
   @Deprecated
   public void addRecipeHandlers(IRecipeHandler... recipeHandlers) {
      ErrorUtil.checkNotEmpty(recipeHandlers, "recipeHandlers");

      for(IRecipeHandler recipeHandler : recipeHandlers) {
         Class recipeClass = recipeHandler.getRecipeClass();
         Preconditions.checkNotNull(recipeClass);
         Preconditions.checkArgument(!recipeClass.equals(Object.class), "Recipe handlers must handle a specific class, not Object.class");
         this.unsortedRecipeHandlers.add(recipeHandler);
      }

   }

   /** @deprecated */
   @Deprecated
   public void addRecipes(Collection recipes) {
      ErrorUtil.checkNotNull(recipes, "recipes");
      this.unsortedRecipes.addAll(recipes);
   }

   public void addRecipes(Collection<?> recipes, String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipes, "recipes");
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      if (!this.recipeCategoryUids.contains(recipeCategoryUid)) {
         Log.get().warn("No recipe category has been registered for recipeCategoryUid {}", recipeCategoryUid);
      }

      for(Object recipe : recipes) {
         ErrorUtil.checkNotNull(recipe, "recipe");
         this.recipes.put(recipeCategoryUid, recipe);
      }

   }

   public <T> void handleRecipes(final Class<T> recipeClass, final IRecipeWrapperFactory<T> recipeWrapperFactory, final String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipeClass, "recipeClass");
      Preconditions.checkArgument(!recipeClass.equals(Object.class), "Recipe handlers must handle a specific class, not Object.class");
      ErrorUtil.checkNotNull(recipeWrapperFactory, "recipeWrapperFactory");
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      IRecipeHandler<T> recipeHandler = new IRecipeHandler<T>() {
         public Class<T> getRecipeClass() {
            return recipeClass;
         }

         public String getRecipeCategoryUid(T recipe) {
            return recipeCategoryUid;
         }

         public IRecipeWrapper getRecipeWrapper(T recipe) {
            return recipeWrapperFactory.getRecipeWrapper(recipe);
         }

         public boolean isRecipeValid(T recipe) {
            return true;
         }
      };
      if (this.recipeHandlerClasses.contains(recipeCategoryUid, recipeClass)) {
         Log.get().error("A Recipe Handler has already been registered for '{}': {}", recipeCategoryUid, recipeClass.getName());
      } else {
         this.recipeHandlerClasses.put(recipeCategoryUid, recipeClass);
         this.recipeHandlers.put(recipeCategoryUid, recipeHandler);
      }

   }

   public void addRecipeClickArea(Class<? extends GuiContainer> guiContainerClass, int xPos, int yPos, int width, int height, String... recipeCategoryUids) {
      ErrorUtil.checkNotNull(guiContainerClass, "guiContainerClass");
      ErrorUtil.checkNotEmpty((Object[])recipeCategoryUids, "recipeCategoryUids");
      RecipeClickableArea recipeClickableArea = new RecipeClickableArea(yPos, yPos + height, xPos, xPos + width, recipeCategoryUids);
      this.recipeClickableAreas.put(guiContainerClass, recipeClickableArea);
   }

   public void addRecipeCatalyst(Object catalystIngredient, String... recipeCategoryUids) {
      ErrorUtil.checkIsValidIngredient(catalystIngredient, "catalystIngredient");
      ErrorUtil.checkNotEmpty((Object[])recipeCategoryUids, "recipeCategoryUids");

      for(String recipeCategoryUid : recipeCategoryUids) {
         ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
         this.recipeCatalysts.put(recipeCategoryUid, catalystIngredient);
      }

   }

   /** @deprecated */
   @Deprecated
   public void addRecipeCategoryCraftingItem(ItemStack craftingItem, String... recipeCategoryUids) {
      this.addRecipeCatalyst(craftingItem, recipeCategoryUids);
   }

   public void addAdvancedGuiHandlers(IAdvancedGuiHandler<?>... advancedGuiHandlers) {
      ErrorUtil.checkNotEmpty(advancedGuiHandlers, "advancedGuiHandlers");
      Collections.addAll(this.advancedGuiHandlers, advancedGuiHandlers);
   }

   public void addGlobalGuiHandlers(IGlobalGuiHandler... globalGuiHandlers) {
      ErrorUtil.checkNotEmpty(globalGuiHandlers, "globalGuiHandlers");
      Collections.addAll(this.globalGuiHandlers, globalGuiHandlers);
   }

   public <T extends GuiScreen> void addGuiScreenHandler(Class<T> guiClass, IGuiScreenHandler<T> handler) {
      ErrorUtil.checkNotNull(guiClass, "guiClass");
      Preconditions.checkArgument(GuiScreen.class.isAssignableFrom(guiClass), "guiClass must inherit from GuiScreen");
      Preconditions.checkArgument(!GuiScreen.class.equals(guiClass), "you cannot add a handler for GuiScreen, only a subclass.");
      ErrorUtil.checkNotNull(handler, "guiScreenHandler");
      this.guiScreenHandlers.put(guiClass, handler);
   }

   public <T extends GuiScreen> void addGhostIngredientHandler(Class<T> guiClass, IGhostIngredientHandler<T> handler) {
      ErrorUtil.checkNotNull(guiClass, "guiClass");
      Preconditions.checkArgument(GuiScreen.class.isAssignableFrom(guiClass), "guiClass must inherit from GuiScreen");
      Preconditions.checkArgument(!ghostIngredientGuiBlacklist.contains(guiClass), "you cannot add a ghost ingredient handler for the following Guis, it would interfere with using JEI: %s", ghostIngredientGuiBlacklist);
      ErrorUtil.checkNotNull(handler, "handler");
      this.ghostIngredientHandlers.put(guiClass, handler);
   }

   /** @deprecated */
   @Deprecated
   public void addDescription(List<ItemStack> itemStacks, String... descriptionKeys) {
      this.addIngredientInfo(itemStacks, VanillaTypes.ITEM, descriptionKeys);
   }

   /** @deprecated */
   @Deprecated
   public void addDescription(ItemStack itemStack, String... descriptionKeys) {
      this.addIngredientInfo(itemStack, VanillaTypes.ITEM, descriptionKeys);
   }

   public <T> void addIngredientInfo(T ingredient, IIngredientType<T> ingredientType, String... descriptionKeys) {
      ErrorUtil.checkIsValidIngredient(ingredient, "ingredient");
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      ErrorUtil.checkNotEmpty((Object[])descriptionKeys, "descriptionKeys");
      this.addIngredientInfo(Collections.singletonList(ingredient), ingredientType, descriptionKeys);
   }

   /** @deprecated */
   @Deprecated
   public <T> void addIngredientInfo(T ingredient, Class<? extends T> ingredientClass, String... descriptionKeys) {
      IIngredientType<T> ingredientType = this.ingredientRegistry.getIngredientType(ingredientClass);
      this.addIngredientInfo(ingredient, ingredientType, descriptionKeys);
   }

   public <T> void addIngredientInfo(List<T> ingredients, IIngredientType<T> ingredientType, String... descriptionKeys) {
      ErrorUtil.checkNotEmpty(ingredients, "ingredients");

      for(Object ingredient : ingredients) {
         ErrorUtil.checkIsValidIngredient(ingredient, "ingredient");
      }

      ErrorUtil.checkNotEmpty((Object[])descriptionKeys, "descriptionKeys");
      IGuiHelper guiHelper = this.jeiHelpers.getGuiHelper();
      List<IngredientInfoRecipe<T>> recipes = IngredientInfoRecipe.create(guiHelper, ingredients, ingredientType, descriptionKeys);
      this.addRecipes(recipes, "jei.information");
   }

   /** @deprecated */
   @Deprecated
   public <T> void addIngredientInfo(List<T> ingredients, Class<? extends T> ingredientClass, String... descriptionKeys) {
      IIngredientType<T> ingredientType = this.ingredientRegistry.getIngredientType(ingredientClass);
      this.addIngredientInfo(ingredients, ingredientType, descriptionKeys);
   }

   /** @deprecated */
   @Deprecated
   public void addAnvilRecipe(ItemStack leftInput, List<ItemStack> rightInputs, List<ItemStack> outputs) {
      ErrorUtil.checkNotEmpty(leftInput, "leftInput");
      ErrorUtil.checkNotEmpty(rightInputs, "rightInputs");
      ErrorUtil.checkNotEmpty(outputs, "outputs");
      Preconditions.checkArgument(rightInputs.size() == outputs.size(), "Input and output sizes must match.");
      AnvilRecipeWrapper anvilRecipeWrapper = new AnvilRecipeWrapper(Collections.singletonList(leftInput), rightInputs, outputs);
      this.addRecipes(Collections.singletonList(anvilRecipeWrapper), "minecraft.anvil");
   }

   public IRecipeTransferRegistry getRecipeTransferRegistry() {
      return this.recipeTransferRegistry;
   }

   public void addRecipeRegistryPlugin(IRecipeRegistryPlugin recipeRegistryPlugin) {
      ErrorUtil.checkNotNull(recipeRegistryPlugin, "recipeRegistryPlugin");
      Log.get().info("Added recipe registry plugin: {}", recipeRegistryPlugin.getClass());
      this.recipeRegistryPlugins.add(recipeRegistryPlugin);
   }

   public List<IAdvancedGuiHandler<?>> getAdvancedGuiHandlers() {
      return this.advancedGuiHandlers;
   }

   public List<IGlobalGuiHandler> getGlobalGuiHandlers() {
      return this.globalGuiHandlers;
   }

   public Map<Class, IGuiScreenHandler> getGuiScreenHandlers() {
      return this.guiScreenHandlers;
   }

   public Map<Class, IGhostIngredientHandler> getGhostIngredientHandlers() {
      return this.ghostIngredientHandlers;
   }

   public RecipeRegistry createRecipeRegistry(IngredientRegistry ingredientRegistry) {
      ImmutableTable<Class, String, IRecipeTransferHandler> recipeTransferHandlers = this.recipeTransferRegistry.getRecipeTransferHandlers();
      return new RecipeRegistry(this.recipeCategories, this.unsortedRecipeHandlers, this.recipeHandlers, recipeTransferHandlers, this.unsortedRecipes, this.recipes, this.recipeClickableAreas, this.recipeCatalysts, ingredientRegistry, this.recipeRegistryPlugins);
   }
}
