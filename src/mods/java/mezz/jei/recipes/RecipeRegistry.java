package mezz.jei.recipes;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableListMultimap.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.collect.ListMultiMap;
import mezz.jei.collect.SetMultiMap;
import mezz.jei.collect.Table;
import mezz.jei.gui.Focus;
import mezz.jei.gui.recipes.RecipeClickableArea;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.ingredients.Ingredients;
import mezz.jei.plugins.vanilla.furnace.SmeltingRecipe;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;

public class RecipeRegistry implements IRecipeRegistry {
   private final IngredientRegistry ingredientRegistry;
   /** @deprecated */
   @Deprecated
   private final ImmutableList<IRecipeHandler> unsortedRecipeHandlers;
   private final ImmutableMultimap<String, IRecipeHandler> recipeHandlers;
   private final ImmutableList<IRecipeCategory> recipeCategories;
   private final Set<String> hiddenRecipeCategoryUids = new HashSet();
   private final List<IRecipeCategory> recipeCategoriesVisibleCache = new ArrayList();
   private final ImmutableTable<Class, String, IRecipeTransferHandler> recipeTransferHandlers;
   private final ImmutableMultimap<Class<? extends GuiContainer>, RecipeClickableArea> recipeClickableAreasMap;
   private final ImmutableListMultimap<IRecipeCategory, Object> recipeCatalysts;
   private final ImmutableMap<String, IRecipeCategory> recipeCategoriesMap;
   private final RecipeCategoryComparator recipeCategoryComparator;
   private final Table<String, Object, IRecipeWrapper> wrapperMaps = new Table<String, Object, IRecipeWrapper>(new HashMap(), IdentityHashMap::new);
   private final ListMultiMap<IRecipeCategory, IRecipeWrapper> recipeWrappersForCategories = new ListMultiMap<IRecipeCategory, IRecipeWrapper>();
   private final RecipeMap recipeInputMap;
   private final RecipeMap recipeOutputMap;
   private final List<RecipeRegistryPluginSafeWrapper> plugins = new ArrayList();
   private final SetMultiMap<String, IRecipeWrapper> hiddenRecipes = new SetMultiMap<String, IRecipeWrapper>(() -> {
      return Collections.newSetFromMap(new IdentityHashMap());
   });

   public RecipeRegistry(List<IRecipeCategory> recipeCategories, List<IRecipeHandler> unsortedRecipeHandlers, ListMultiMap<String, IRecipeHandler> recipeHandlers, ImmutableTable<Class, String, IRecipeTransferHandler> recipeTransferHandlers, List<Object> unsortedRecipes, ListMultiMap<String, Object> recipes, ListMultiMap<Class<? extends GuiContainer>, RecipeClickableArea> recipeClickableAreasMap, ListMultiMap<String, Object> recipeCatalysts, IngredientRegistry ingredientRegistry, List<IRecipeRegistryPlugin> plugins) {
      this.ingredientRegistry = ingredientRegistry;
      this.recipeTransferHandlers = recipeTransferHandlers;
      this.recipeHandlers = recipeHandlers.toImmutable();
      this.unsortedRecipeHandlers = buildRecipeHandlersList(unsortedRecipeHandlers);
      this.recipeClickableAreasMap = recipeClickableAreasMap.toImmutable();
      this.recipeCategories = ImmutableList.copyOf(recipeCategories);
      this.recipeCategoryComparator = new RecipeCategoryComparator(recipeCategories);
      this.recipeCategoriesMap = buildRecipeCategoriesMap(recipeCategories);
      this.recipeInputMap = new RecipeMap(this.recipeCategoryComparator, ingredientRegistry);
      this.recipeOutputMap = new RecipeMap(this.recipeCategoryComparator, ingredientRegistry);
      this.addRecipes(unsortedRecipes, recipes);
      Builder<IRecipeCategory, Object> recipeCatalystsBuilder = ImmutableListMultimap.builder();
      com.google.common.collect.ImmutableMultimap.Builder<String, String> categoriesForRecipeCatalystKeysBuilder = ImmutableMultimap.builder();

      for(Entry<String, List<Object>> recipeCatalystEntry : recipeCatalysts.entrySet()) {
         String recipeCategoryUid = (String)recipeCatalystEntry.getKey();
         IRecipeCategory recipeCategory = (IRecipeCategory)this.recipeCategoriesMap.get(recipeCategoryUid);
         if (recipeCategory != null) {
            Collection<Object> catalystIngredients = (Collection)recipeCatalystEntry.getValue();
            recipeCatalystsBuilder.putAll(recipeCategory, catalystIngredients);

            for(Object catalystIngredient : catalystIngredients) {
               IIngredientType ingredientType = ingredientRegistry.getIngredientType(catalystIngredient);
               IIngredientHelper ingredientHelper = ingredientRegistry.getIngredientHelper(ingredientType);
               this.recipeInputMap.addRecipeCategory(recipeCategory, catalystIngredient, ingredientHelper);
               String catalystIngredientKey = this.getUniqueId(catalystIngredient);
               categoriesForRecipeCatalystKeysBuilder.put(catalystIngredientKey, recipeCategoryUid);
            }
         }
      }

      this.recipeCatalysts = recipeCatalystsBuilder.build();
      ImmutableMultimap<String, String> categoriesForRecipeCatalystKeys = categoriesForRecipeCatalystKeysBuilder.build();
      IRecipeRegistryPlugin internalRecipeRegistryPlugin = new InternalRecipeRegistryPlugin(this, categoriesForRecipeCatalystKeys, ingredientRegistry, this.recipeCategoriesMap, this.recipeInputMap, this.recipeOutputMap, this.recipeWrappersForCategories);
      this.plugins.add(new RecipeRegistryPluginSafeWrapper(internalRecipeRegistryPlugin));

      for(IRecipeRegistryPlugin plugin : plugins) {
         this.plugins.add(new RecipeRegistryPluginSafeWrapper(plugin));
      }

   }

   private <T> String getUniqueId(T ingredient) {
      IIngredientHelper<T> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredient);
      return ingredientHelper.getUniqueId(ingredient);
   }

   private static ImmutableMap<String, IRecipeCategory> buildRecipeCategoriesMap(List<IRecipeCategory> recipeCategories) {
      com.google.common.collect.ImmutableMap.Builder<String, IRecipeCategory> mapBuilder = ImmutableMap.builder();

      for(IRecipeCategory recipeCategory : recipeCategories) {
         mapBuilder.put(recipeCategory.getUid(), recipeCategory);
      }

      return mapBuilder.build();
   }

   private static ImmutableList<IRecipeHandler> buildRecipeHandlersList(List<IRecipeHandler> recipeHandlers) {
      com.google.common.collect.ImmutableList.Builder<IRecipeHandler> listBuilder = ImmutableList.builder();
      Set<Class> recipeHandlerClasses = new HashSet();

      for(IRecipeHandler recipeHandler : recipeHandlers) {
         if (recipeHandler != null) {
            Class recipeClass = recipeHandler.getRecipeClass();
            if (recipeHandlerClasses.contains(recipeClass)) {
               Log.get().error("A Recipe Handler has already been registered for this recipe class: {}", recipeClass.getName());
            } else {
               recipeHandlerClasses.add(recipeClass);
               listBuilder.add(recipeHandler);
            }
         }
      }

      return listBuilder.build();
   }

   private void addRecipes(List<Object> unsortedRecipes, ListMultiMap<String, Object> recipes) {
      Collection<Entry<String, List<Object>>> entries = recipes.entrySet();
      ProgressBar progressBar = ProgressManager.push("Loading recipes", recipes.getTotalSize() + unsortedRecipes.size());

      for(Entry<String, List<Object>> entry : entries) {
         String recipeCategoryUid = (String)entry.getKey();

         for(Object recipe : (List)entry.getValue()) {
            progressBar.step("");
            this.addRecipe(recipe, recipe.getClass(), recipeCategoryUid);
         }
      }

      for(Object recipe : unsortedRecipes) {
         progressBar.step("");
         this.addRecipe(recipe, recipe.getClass(), (String)null);
      }

      ProgressManager.pop(progressBar);
   }

   public <V> IFocus<V> createFocus(IFocus.Mode mode, V ingredient) {
      return new Focus<V>(mode, ingredient);
   }

   /** @deprecated */
   @Deprecated
   public void addRecipe(Object recipe) {
      ErrorUtil.checkNotNull(recipe, "recipe");
      ErrorUtil.assertMainThread();
      this.addRecipe(recipe, recipe.getClass(), (String)null);
   }

   /** @deprecated */
   @Deprecated
   public void addRecipe(IRecipeWrapper recipe, String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipe, "recipe");
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      ErrorUtil.assertMainThread();
      IRecipeCategory recipeCategory = this.getRecipeCategory(recipeCategoryUid);
      if (recipeCategory == null) {
         Log.get().error("No recipe category registered for recipeCategoryUid: {}", recipeCategoryUid);
      } else {
         if (this.hiddenRecipes.contains(recipeCategoryUid, recipe)) {
            this.unhideRecipe(recipe, recipeCategoryUid);
         } else {
            this.addRecipe(recipe, recipe, recipeCategory);
         }

      }
   }

   private <T> void addRecipe(T recipe, Class<? extends T> recipeClass, @Nullable String recipeCategoryUid) {
      if (recipeCategoryUid == null) {
         IRecipeHandler<T> recipeHandler = this.<T>getRecipeHandler(recipeClass, (String)null);
         if (recipeHandler == null) {
            Log.get().error("Could not determine recipe category for recipe: {}", recipeClass);
            return;
         }

         recipeCategoryUid = recipeHandler.getRecipeCategoryUid(recipe);
      }

      IRecipeWrapper recipeWrapper = this.getRecipeWrapper(recipe, recipeClass, recipeCategoryUid);
      if (recipeWrapper != null) {
         IRecipeCategory recipeCategory = this.getRecipeCategory(recipeCategoryUid);
         if (recipeCategory == null) {
            Log.get().error("No recipe category registered for recipeCategoryUid: {}", recipeCategoryUid);
            return;
         }

         this.addRecipe(recipe, recipeWrapper, recipeCategory);
      } else {
         Log.get().debug("No recipe wrapper for recipe: {}", ErrorUtil.getNameForRecipe(recipe));
      }

   }

   @Nullable
   public IRecipeCategory getRecipeCategory(String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      return (IRecipeCategory)this.recipeCategoriesMap.get(recipeCategoryUid);
   }

   private static <T> void logBrokenRecipeHandler(T recipe, IRecipeHandler<T> recipeHandler, Throwable e) {
      StringBuilder recipeInfoBuilder = new StringBuilder();

      try {
         recipeInfoBuilder.append(recipe);
      } catch (RuntimeException var5) {
         Log.get().error("Failed recipe.toString", var5);
         recipeInfoBuilder.append(recipe.getClass());
      }

      recipeInfoBuilder.append("\nRecipe Handler failed to create recipe wrapper\n");
      recipeInfoBuilder.append(recipeHandler.getClass());
      Log.get().error("{}", recipeInfoBuilder.toString(), e);
   }

   private <T> void addRecipe(T recipe, IRecipeWrapper recipeWrapper, IRecipeCategory recipeCategory) {
      try {
         this.addRecipeUnchecked(recipe, recipeWrapper, recipeCategory);
      } catch (BrokenCraftingRecipeException var6) {
         Log.get().error("Found a broken crafting recipe.", var6);
      } catch (LinkageError | RuntimeException var7) {
         String recipeInfo = ErrorUtil.getInfoFromRecipe(recipe, recipeWrapper);
         Log.get().error("Found a broken recipe: {}\n", recipeInfo, var7);
      }

   }

   private <T> void addRecipeUnchecked(T recipe, IRecipeWrapper recipeWrapper, IRecipeCategory recipeCategory) {
      this.wrapperMaps.put(recipeCategory.getUid(), recipe, recipeWrapper);
      Ingredients ingredients = this.getIngredients(recipeWrapper);
      this.recipeInputMap.addRecipe(recipeWrapper, recipeCategory, ingredients.getInputIngredients());
      this.recipeOutputMap.addRecipe(recipeWrapper, recipeCategory, ingredients.getOutputIngredients());
      this.recipeWrappersForCategories.put(recipeCategory, recipeWrapper);
      this.unhideRecipe(recipeWrapper, recipeCategory.getUid());
      this.recipeCategoriesVisibleCache.clear();
   }

   public Ingredients getIngredients(IRecipeWrapper recipeWrapper) {
      Ingredients ingredients = new Ingredients();
      recipeWrapper.getIngredients(ingredients);
      return ingredients;
   }

   /** @deprecated */
   @Deprecated
   public void removeRecipe(Object recipe) {
      ErrorUtil.checkNotNull(recipe, "recipe");
      ErrorUtil.assertMainThread();

      for(IRecipeHandler<Object> recipeHandler : this.getRecipeHandlers(recipe.getClass())) {
         String recipeCategoryUid = recipeHandler.getRecipeCategoryUid(recipe);
         this.removeRecipe(recipe, recipeCategoryUid);
      }

   }

   private <T> void removeRecipe(T recipe, String recipeCategoryUid) {
      IRecipeCategory recipeCategory = (IRecipeCategory)this.recipeCategoriesMap.get(recipeCategoryUid);
      if (recipeCategory == null) {
         Log.get().error("No recipe category registered for recipeCategoryUid: {}", recipeCategoryUid);
      } else {
         try {
            IRecipeWrapper recipeWrapper = this.getRecipeWrapper(recipe, recipeCategory.getUid());
            if (recipeWrapper != null) {
               this.hideRecipe(recipeWrapper, recipeCategoryUid);
            }
         } catch (BrokenCraftingRecipeException var5) {
            Log.get().error("Found a broken crafting recipe.", var5);
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public void removeRecipe(IRecipeWrapper recipe, String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipe, "recipe");
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      ErrorUtil.assertMainThread();
      this.hideRecipe(recipe, recipeCategoryUid);
   }

   /** @deprecated */
   @Deprecated
   public void addSmeltingRecipe(List<ItemStack> inputs, ItemStack output) {
      ErrorUtil.checkNotEmpty(inputs, "inputs");
      ErrorUtil.checkNotEmpty(output, "output");
      SmeltingRecipe smeltingRecipe = new SmeltingRecipe(inputs, output);
      this.addRecipe(smeltingRecipe);
   }

   /** @deprecated */
   @Deprecated
   public IRecipeWrapper createSmeltingRecipe(List<ItemStack> inputs, ItemStack output) {
      return Internal.getHelpers().getVanillaRecipeFactory().createSmeltingRecipe(inputs, output);
   }

   /** @deprecated */
   @Deprecated
   public IRecipeWrapper createAnvilRecipe(ItemStack leftInput, List<ItemStack> rightInputs, List<ItemStack> outputs) {
      return Internal.getHelpers().getVanillaRecipeFactory().createAnvilRecipe(leftInput, rightInputs, outputs);
   }

   /** @deprecated */
   @Deprecated
   public IRecipeWrapper createBrewingRecipe(List<ItemStack> ingredients, ItemStack potionInput, ItemStack potionOutput) {
      return Internal.getHelpers().getVanillaRecipeFactory().createBrewingRecipe(ingredients, potionInput, potionOutput);
   }

   public List<IRecipeCategory> getRecipeCategories() {
      if (this.recipeCategoriesVisibleCache.isEmpty()) {
         UnmodifiableIterator var1 = this.recipeCategories.iterator();

         while(var1.hasNext()) {
            IRecipeCategory<?> recipeCategory = (IRecipeCategory)var1.next();
            if (this.isCategoryVisible(recipeCategory)) {
               this.recipeCategoriesVisibleCache.add(recipeCategory);
            }
         }
      }

      return this.recipeCategoriesVisibleCache;
   }

   private boolean isCategoryVisible(IRecipeCategory<?> recipeCategory) {
      if (this.hiddenRecipeCategoryUids.contains(recipeCategory.getUid())) {
         return false;
      } else {
         List<Object> allCatalysts = this.getRecipeCatalysts(recipeCategory, true);
         if (!allCatalysts.isEmpty()) {
            List<Object> visibleCatalysts = this.getRecipeCatalysts(recipeCategory, false);
            if (visibleCatalysts.isEmpty()) {
               return false;
            }
         }

         return !this.getRecipeWrappers(recipeCategory).isEmpty();
      }
   }

   public List<IRecipeCategory> getRecipeCategories(List<String> recipeCategoryUids) {
      ErrorUtil.checkNotNull(recipeCategoryUids, "recipeCategoryUids");
      List<IRecipeCategory> categories = new ArrayList();

      for(String recipeCategoryUid : recipeCategoryUids) {
         IRecipeCategory<?> recipeCategory = (IRecipeCategory)this.recipeCategoriesMap.get(recipeCategoryUid);
         if (recipeCategory != null && this.getRecipeCategories().contains(recipeCategory)) {
            categories.add(recipeCategory);
         }
      }

      Comparator<IRecipeCategory> comparator = Comparator.comparing(IRecipeCategory::getUid, this.recipeCategoryComparator);
      categories.sort(comparator);
      return Collections.unmodifiableList(categories);
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public <T> IRecipeHandler<T> getRecipeHandler(Class<? extends T> recipeClass) {
      return this.<T>getRecipeHandler(recipeClass, (String)null);
   }

   @Nullable
   public IRecipeWrapper getRecipeWrapper(Object recipe, String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipe, "recipe");
      ErrorUtil.checkNotEmpty(recipeCategoryUid, "recipeCategoryUid");
      return this.getRecipeWrapper(recipe, recipe.getClass(), recipeCategoryUid);
   }

   @Nullable
   private <T> IRecipeWrapper getRecipeWrapper(T recipe, Class<? extends T> recipeClass, String recipeCategoryUid) {
      IRecipeWrapper recipeWrapper = this.wrapperMaps.get(recipeCategoryUid, recipe);
      if (recipeWrapper != null) {
         return recipeWrapper;
      } else {
         IRecipeHandler<T> recipeHandler = this.<T>getRecipeHandler(recipeClass, recipeCategoryUid);
         if (recipeHandler == null) {
            return recipe instanceof IRecipeWrapper ? (IRecipeWrapper)recipe : null;
         } else {
            try {
               if (!recipeHandler.isRecipeValid(recipe)) {
                  return null;
               }
            } catch (LinkageError | RuntimeException var8) {
               Log.get().error("Recipe check crashed", var8);
               return null;
            }

            try {
               recipeWrapper = recipeHandler.getRecipeWrapper(recipe);
               this.wrapperMaps.put(recipeCategoryUid, recipe, recipeWrapper);
               return recipeWrapper;
            } catch (LinkageError | RuntimeException var7) {
               logBrokenRecipeHandler(recipe, recipeHandler, var7);
               return null;
            }
         }
      }
   }

   @Nullable
   private <T> IRecipeHandler<T> getRecipeHandler(Class<? extends T> recipeClass, @Nullable String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipeClass, "recipeClass");
      ImmutableCollection<IRecipeHandler> recipeHandlers;
      if (recipeCategoryUid != null) {
         recipeHandlers = this.recipeHandlers.get(recipeCategoryUid);
      } else {
         recipeHandlers = this.recipeHandlers.values();
      }

      UnmodifiableIterator var4 = recipeHandlers.iterator();

      while(var4.hasNext()) {
         IRecipeHandler<?> recipeHandler = (IRecipeHandler)var4.next();
         if (recipeHandler.getRecipeClass().equals(recipeClass)) {
            return recipeHandler;
         }
      }

      var4 = this.unsortedRecipeHandlers.iterator();

      while(var4.hasNext()) {
         IRecipeHandler<?> recipeHandler = (IRecipeHandler)var4.next();
         if (recipeHandler.getRecipeClass().equals(recipeClass)) {
            return recipeHandler;
         }
      }

      var4 = recipeHandlers.iterator();

      while(var4.hasNext()) {
         IRecipeHandler<?> recipeHandler = (IRecipeHandler)var4.next();
         if (recipeHandler.getRecipeClass().isAssignableFrom(recipeClass)) {
            return recipeHandler;
         }
      }

      var4 = this.unsortedRecipeHandlers.iterator();

      while(var4.hasNext()) {
         IRecipeHandler<?> recipeHandler = (IRecipeHandler)var4.next();
         if (recipeHandler.getRecipeClass().isAssignableFrom(recipeClass)) {
            return recipeHandler;
         }
      }

      return null;
   }

   /** @deprecated */
   @Deprecated
   private <T> List<IRecipeHandler<T>> getRecipeHandlers(Class<? extends T> recipeClass) {
      ErrorUtil.checkNotNull(recipeClass, "recipeClass");
      List<IRecipeHandler<T>> recipeHandlers = new ArrayList();
      ImmutableCollection<IRecipeHandler> allRecipeHandlers = this.recipeHandlers.values();
      UnmodifiableIterator var4 = allRecipeHandlers.iterator();

      while(var4.hasNext()) {
         IRecipeHandler<?> recipeHandler = (IRecipeHandler)var4.next();
         if (recipeHandler.getRecipeClass().equals(recipeClass)) {
            recipeHandlers.add(recipeHandler);
         }
      }

      var4 = this.unsortedRecipeHandlers.iterator();

      while(var4.hasNext()) {
         IRecipeHandler<?> recipeHandler = (IRecipeHandler)var4.next();
         if (recipeHandler.getRecipeClass().equals(recipeClass)) {
            recipeHandlers.add(recipeHandler);
         }
      }

      var4 = allRecipeHandlers.iterator();

      while(var4.hasNext()) {
         IRecipeHandler<?> recipeHandler = (IRecipeHandler)var4.next();
         if (recipeHandler.getRecipeClass().isAssignableFrom(recipeClass)) {
            recipeHandlers.add(recipeHandler);
         }
      }

      var4 = this.unsortedRecipeHandlers.iterator();

      while(var4.hasNext()) {
         IRecipeHandler<?> recipeHandler = (IRecipeHandler)var4.next();
         if (recipeHandler.getRecipeClass().isAssignableFrom(recipeClass)) {
            recipeHandlers.add(recipeHandler);
         }
      }

      return recipeHandlers;
   }

   @Nullable
   public RecipeClickableArea getRecipeClickableArea(GuiContainer gui, int mouseX, int mouseY) {
      ImmutableCollection<RecipeClickableArea> recipeClickableAreas = this.recipeClickableAreasMap.get(gui.getClass());
      UnmodifiableIterator var5 = recipeClickableAreas.iterator();

      while(var5.hasNext()) {
         RecipeClickableArea recipeClickableArea = (RecipeClickableArea)var5.next();
         if (recipeClickableArea.checkHover(mouseX, mouseY)) {
            return recipeClickableArea;
         }
      }

      return null;
   }

   public <V> List<IRecipeCategory> getRecipeCategories(IFocus<V> focus) {
      IFocus<V> var10 = Focus.check(focus);
      List<String> allRecipeCategoryUids = new ArrayList();

      for(IRecipeRegistryPlugin plugin : this.plugins) {
         for(String recipeCategoryUid : plugin.getRecipeCategoryUids(var10)) {
            if (!allRecipeCategoryUids.contains(recipeCategoryUid)) {
               if (this.hiddenRecipes.containsKey(recipeCategoryUid)) {
                  IRecipeCategory<?> recipeCategory = this.getRecipeCategory(recipeCategoryUid);
                  if (recipeCategory != null) {
                     List<?> recipeWrappers = this.getRecipeWrappers(recipeCategory, var10);
                     if (!recipeWrappers.isEmpty()) {
                        allRecipeCategoryUids.add(recipeCategoryUid);
                     }
                  }
               } else {
                  allRecipeCategoryUids.add(recipeCategoryUid);
               }
            }
         }
      }

      return this.getRecipeCategories(allRecipeCategoryUids);
   }

   public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
      ErrorUtil.checkNotNull(recipeCategory, "recipeCategory");
      IFocus<V> var7 = Focus.check(focus);
      List<T> allRecipeWrappers = new ArrayList();

      for(IRecipeRegistryPlugin plugin : this.plugins) {
         List<T> recipeWrappers = plugin.getRecipeWrappers(recipeCategory, var7);
         allRecipeWrappers.addAll(recipeWrappers);
      }

      Set<T> hidden = (Set)this.hiddenRecipes.get(recipeCategory.getUid());
      allRecipeWrappers.removeAll(hidden);
      return allRecipeWrappers;
   }

   public <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory) {
      ErrorUtil.checkNotNull(recipeCategory, "recipeCategory");
      List<T> allRecipeWrappers = new ArrayList();

      for(IRecipeRegistryPlugin plugin : this.plugins) {
         List<T> recipeWrappers = plugin.<T>getRecipeWrappers(recipeCategory);
         allRecipeWrappers.addAll(recipeWrappers);
      }

      Set<T> hidden = (Set)this.hiddenRecipes.get(recipeCategory.getUid());
      allRecipeWrappers.removeAll(hidden);
      return allRecipeWrappers;
   }

   /** @deprecated */
   @Deprecated
   public List<ItemStack> getCraftingItems(IRecipeCategory recipeCategory, @Nullable IFocus focus) {
      if (focus != null) {
         focus = Focus.check(focus);
      }

      List<ItemStack> craftingItems = this.getCraftingItems(recipeCategory);
      if (focus != null && focus.getMode() == IFocus.Mode.INPUT) {
         Object ingredient = focus.getValue();
         if (ingredient instanceof ItemStack) {
            ItemStack itemStack = (ItemStack)ingredient;
            IIngredientHelper<ItemStack> ingredientHelper = this.ingredientRegistry.getIngredientHelper(VanillaTypes.ITEM);
            ItemStack matchingStack = ingredientHelper.getMatch(craftingItems, itemStack);
            if (matchingStack != null) {
               return Collections.singletonList(matchingStack);
            }
         }
      }

      return craftingItems;
   }

   /** @deprecated */
   @Deprecated
   public List<ItemStack> getCraftingItems(IRecipeCategory recipeCategory) {
      ErrorUtil.checkNotNull(recipeCategory, "recipeCategory");
      List<Object> objects = this.getRecipeCatalysts(recipeCategory);
      List<ItemStack> itemStacks = new ArrayList();

      for(Object object : objects) {
         if (object instanceof ItemStack) {
            itemStacks.add((ItemStack)object);
         }
      }

      return Collections.unmodifiableList(itemStacks);
   }

   public List<Object> getRecipeCatalysts(IRecipeCategory recipeCategory, boolean includeHidden) {
      ErrorUtil.checkNotNull(recipeCategory, "recipeCategory");
      ImmutableList<Object> catalysts = this.recipeCatalysts.get(recipeCategory);
      if (includeHidden) {
         return catalysts;
      } else {
         List<Object> visibleCatalysts = new ArrayList();
         IngredientFilter ingredientFilter = Internal.getIngredientFilter();
         UnmodifiableIterator var6 = catalysts.iterator();

         while(var6.hasNext()) {
            Object catalyst = var6.next();
            if (this.ingredientRegistry.isIngredientVisible(catalyst, ingredientFilter)) {
               visibleCatalysts.add(catalyst);
            }
         }

         return visibleCatalysts;
      }
   }

   public List<Object> getRecipeCatalysts(IRecipeCategory recipeCategory) {
      return this.getRecipeCatalysts(recipeCategory, false);
   }

   @Nullable
   public IRecipeTransferHandler getRecipeTransferHandler(Container container, IRecipeCategory recipeCategory) {
      ErrorUtil.checkNotNull(container, "container");
      ErrorUtil.checkNotNull(recipeCategory, "recipeCategory");
      Class<? extends Container> containerClass = container.getClass();
      IRecipeTransferHandler recipeTransferHandler = (IRecipeTransferHandler)this.recipeTransferHandlers.get(containerClass, recipeCategory.getUid());
      return recipeTransferHandler != null ? recipeTransferHandler : (IRecipeTransferHandler)this.recipeTransferHandlers.get(containerClass, "universal recipe transfer handler");
   }

   public <T extends IRecipeWrapper> IRecipeLayoutDrawable createRecipeLayoutDrawable(IRecipeCategory<T> recipeCategory, T recipeWrapper, IFocus<?> focus) {
      IFocus<?> var5 = Focus.check(focus);
      RecipeLayout recipeLayout = RecipeLayout.create(-1, recipeCategory, recipeWrapper, var5, 0, 0);
      Preconditions.checkNotNull(recipeLayout, "Recipe layout crashed during creation, see log.");
      return recipeLayout;
   }

   public void hideRecipe(IRecipeWrapper recipe, String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipe, "recipe");
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      ErrorUtil.assertMainThread();
      this.hiddenRecipes.put(recipeCategoryUid, recipe);
      this.recipeCategoriesVisibleCache.clear();
   }

   public void unhideRecipe(IRecipeWrapper recipe, String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipe, "recipe");
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      ErrorUtil.assertMainThread();
      this.hiddenRecipes.remove(recipeCategoryUid, recipe);
      this.recipeCategoriesVisibleCache.clear();
   }

   public void hideRecipeCategory(String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      ErrorUtil.assertMainThread();
      IRecipeCategory recipeCategory = (IRecipeCategory)this.recipeCategoriesMap.get(recipeCategoryUid);
      if (recipeCategory == null) {
         throw new IllegalArgumentException("Unknown recipe category: " + recipeCategoryUid);
      } else {
         this.hiddenRecipeCategoryUids.add(recipeCategoryUid);
         this.recipeCategoriesVisibleCache.remove(recipeCategory);
      }
   }

   public void unhideRecipeCategory(String recipeCategoryUid) {
      ErrorUtil.checkNotNull(recipeCategoryUid, "recipeCategoryUid");
      ErrorUtil.assertMainThread();
      if (!this.recipeCategoriesMap.containsKey(recipeCategoryUid)) {
         throw new IllegalArgumentException("Unknown recipe category: " + recipeCategoryUid);
      } else {
         this.hiddenRecipeCategoryUids.remove(recipeCategoryUid);
         this.recipeCategoriesVisibleCache.clear();
      }
   }

   /** @deprecated */
   @Deprecated
   public void hideRecipe(IRecipeWrapper recipe) {
      this.hideRecipe(recipe, "minecraft.crafting");
   }

   /** @deprecated */
   @Deprecated
   public void unhideRecipe(IRecipeWrapper recipe) {
      this.unhideRecipe(recipe, "minecraft.crafting");
   }
}
