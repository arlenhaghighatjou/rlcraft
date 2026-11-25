package mezz.jei.gui.recipes;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import javax.annotation.Nonnegative;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.Focus;
import mezz.jei.gui.ingredients.IngredientLookupState;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;

public class RecipeGuiLogic implements IRecipeGuiLogic {
   private final IRecipeRegistry recipeRegistry;
   private final IRecipeLogicStateListener stateListener;
   private final IngredientRegistry ingredientRegistry;
   private boolean initialState = true;
   private IngredientLookupState state;
   private final Stack<IngredientLookupState> history = new Stack();
   private List<IRecipeWrapper> recipes = Collections.emptyList();

   public RecipeGuiLogic(IRecipeRegistry recipeRegistry, IRecipeLogicStateListener stateListener, IngredientRegistry ingredientRegistry) {
      this.recipeRegistry = recipeRegistry;
      this.stateListener = stateListener;
      this.ingredientRegistry = ingredientRegistry;
      List<IRecipeCategory> recipeCategories = recipeRegistry.getRecipeCategories();
      this.state = new IngredientLookupState((IFocus)null, recipeCategories, 0, 0);
   }

   public <V> boolean setFocus(IFocus<V> focus) {
      IFocus<V> var7 = Focus.check(focus);
      IIngredientHelper<V> ingredientHelper = this.ingredientRegistry.getIngredientHelper(var7.getValue());
      IFocus<?> translatedFocus = ingredientHelper.translateFocus(var7, Focus::new);
      List<IRecipeCategory> recipeCategories = this.recipeRegistry.getRecipeCategories(translatedFocus);
      if (recipeCategories.isEmpty()) {
         return false;
      } else {
         if (!this.initialState) {
            this.history.push(this.state);
         }

         int recipeCategoryIndex = this.getRecipeCategoryIndexToShowFirst(recipeCategories);
         IngredientLookupState state = new IngredientLookupState(translatedFocus, recipeCategories, recipeCategoryIndex, 0);
         this.setState(state);
         return true;
      }
   }

   @Nonnegative
   private int getRecipeCategoryIndexToShowFirst(List<IRecipeCategory> recipeCategories) {
      Minecraft minecraft = Minecraft.getMinecraft();
      EntityPlayerSP player = minecraft.player;
      if (player != null) {
         Container openContainer = player.openContainer;
         if (openContainer != null) {
            for(int i = 0; i < recipeCategories.size(); ++i) {
               IRecipeCategory recipeCategory = (IRecipeCategory)recipeCategories.get(i);
               IRecipeTransferHandler recipeTransferHandler = this.recipeRegistry.getRecipeTransferHandler(openContainer, recipeCategory);
               if (recipeTransferHandler != null) {
                  return i;
               }
            }
         }
      }

      return 0;
   }

   public boolean back() {
      if (this.history.empty()) {
         return false;
      } else {
         IngredientLookupState state = (IngredientLookupState)this.history.pop();
         this.setState(state);
         return true;
      }
   }

   public void clearHistory() {
      while(!this.history.empty()) {
         this.history.pop();
      }

   }

   private void setState(IngredientLookupState state) {
      this.state = state;
      this.initialState = false;
      this.updateRecipes();
      this.stateListener.onStateChange();
   }

   public boolean setCategoryFocus() {
      IRecipeCategory recipeCategory = this.getSelectedRecipeCategory();
      if (!this.initialState) {
         this.history.push(this.state);
      }

      List<IRecipeCategory> recipeCategories = this.recipeRegistry.getRecipeCategories();
      int recipeCategoryIndex = recipeCategories.indexOf(recipeCategory);
      IngredientLookupState state = new IngredientLookupState((IFocus)null, recipeCategories, recipeCategoryIndex, 0);
      this.setState(state);
      return true;
   }

   public boolean setCategoryFocus(List<String> recipeCategoryUids) {
      List<IRecipeCategory> recipeCategories = this.recipeRegistry.getRecipeCategories(recipeCategoryUids);
      if (recipeCategories.isEmpty()) {
         return false;
      } else {
         if (!this.initialState) {
            this.history.push(this.state);
         }

         IngredientLookupState state = new IngredientLookupState((IFocus)null, recipeCategories, 0, 0);
         this.setState(state);
         return true;
      }
   }

   public List<Object> getRecipeCatalysts() {
      IRecipeCategory category = this.getSelectedRecipeCategory();
      return this.getRecipeCatalysts(category);
   }

   public List<Object> getRecipeCatalysts(IRecipeCategory recipeCategory) {
      return this.recipeRegistry.getRecipeCatalysts(recipeCategory);
   }

   public void setRecipesPerPage(int recipesPerPage) {
      if (this.state.getRecipesPerPage() != recipesPerPage) {
         this.state.setRecipesPerPage(recipesPerPage);
         this.updateRecipes();
      }

   }

   private void updateRecipes() {
      IRecipeCategory recipeCategory = this.getSelectedRecipeCategory();
      IFocus<?> focus = this.state.getFocus();
      if (focus != null) {
         this.recipes = this.recipeRegistry.getRecipeWrappers(recipeCategory, focus);
      } else {
         this.recipes = this.recipeRegistry.<IRecipeWrapper>getRecipeWrappers(recipeCategory);
      }

   }

   public IRecipeCategory getSelectedRecipeCategory() {
      return (IRecipeCategory)this.state.getRecipeCategories().get(this.state.getRecipeCategoryIndex());
   }

   public ImmutableList<IRecipeCategory> getRecipeCategories() {
      return this.state.getRecipeCategories();
   }

   public List<RecipeLayout> getRecipeLayouts(int posX, int posY, int spacingY) {
      List<RecipeLayout> recipeLayouts = new ArrayList();
      IRecipeCategory recipeCategory = this.getSelectedRecipeCategory();
      int recipeWidgetIndex = 0;
      int recipePosY = posY;
      int firstRecipeIndex = this.state.getRecipeIndex() - this.state.getRecipeIndex() % this.state.getRecipesPerPage();

      for(int recipeIndex = firstRecipeIndex; recipeIndex < this.recipes.size() && recipeLayouts.size() < this.state.getRecipesPerPage(); ++recipeIndex) {
         IRecipeWrapper recipeWrapper = (IRecipeWrapper)this.recipes.get(recipeIndex);
         RecipeLayout recipeLayout = RecipeLayout.create(recipeWidgetIndex++, recipeCategory, recipeWrapper, this.state.getFocus(), posX, recipePosY);
         if (recipeLayout == null) {
            this.recipes.remove(recipeIndex);
            this.recipeRegistry.hideRecipe(recipeWrapper, recipeCategory.getUid());
            --recipeIndex;
         } else {
            recipeLayouts.add(recipeLayout);
            recipePosY += spacingY;
         }
      }

      return recipeLayouts;
   }

   public void nextRecipeCategory() {
      int recipesTypesCount = this.state.getRecipeCategories().size();
      this.state.setRecipeCategoryIndex((this.state.getRecipeCategoryIndex() + 1) % recipesTypesCount);
      this.state.setRecipeIndex(0);
      this.updateRecipes();
      this.stateListener.onStateChange();
   }

   public void setRecipeCategory(IRecipeCategory category) {
      int index = this.state.getRecipeCategories().indexOf(category);
      if (index >= 0) {
         this.state.setRecipeCategoryIndex(index);
         this.state.setRecipeIndex(0);
         this.updateRecipes();
         this.stateListener.onStateChange();
      }
   }

   public boolean hasMultiplePages() {
      return this.recipes.size() > this.state.getRecipesPerPage();
   }

   public void previousRecipeCategory() {
      int recipesTypesCount = this.state.getRecipeCategories().size();
      this.state.setRecipeCategoryIndex((recipesTypesCount + this.state.getRecipeCategoryIndex() - 1) % recipesTypesCount);
      this.state.setRecipeIndex(0);
      this.updateRecipes();
      this.stateListener.onStateChange();
   }

   public void nextPage() {
      this.state.setRecipeIndex(this.state.getRecipeIndex() + this.state.getRecipesPerPage());
      if (this.state.getRecipeIndex() >= this.recipes.size()) {
         this.state.setRecipeIndex(0);
      }

      this.stateListener.onStateChange();
   }

   public void previousPage() {
      this.state.setRecipeIndex(this.state.getRecipeIndex() - this.state.getRecipesPerPage());
      if (this.state.getRecipeIndex() < 0) {
         int pageCount = this.pageCount(this.state.getRecipesPerPage());
         this.state.setRecipeIndex((pageCount - 1) * this.state.getRecipesPerPage());
      }

      this.stateListener.onStateChange();
   }

   private int pageCount(int recipesPerPage) {
      return this.recipes.size() <= 1 ? 1 : MathUtil.divideCeil(this.recipes.size(), recipesPerPage);
   }

   public String getPageString() {
      int pageIndex = MathUtil.divideCeil(this.state.getRecipeIndex() + 1, this.state.getRecipesPerPage());
      return pageIndex + "/" + this.pageCount(this.state.getRecipesPerPage());
   }

   public boolean hasMultipleCategories() {
      return this.state.getRecipeCategories().size() > 1;
   }

   public boolean hasAllCategories() {
      return this.state.getRecipeCategories().size() == this.recipeRegistry.getRecipeCategories().size();
   }
}
