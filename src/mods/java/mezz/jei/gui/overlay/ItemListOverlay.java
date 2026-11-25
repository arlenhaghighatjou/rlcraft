package mezz.jei.gui.overlay;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.IIngredientFilter;
import mezz.jei.api.IItemListOverlay;
import mezz.jei.config.Config;
import mezz.jei.util.ErrorUtil;
import net.minecraft.item.ItemStack;

/** @deprecated */
@Deprecated
public class ItemListOverlay implements IItemListOverlay {
   private final IngredientListOverlay ingredientListOverlay;
   private final IIngredientFilter ingredientFilter;

   public ItemListOverlay(IngredientListOverlay ingredientListOverlay, IIngredientFilter ingredientFilter) {
      this.ingredientListOverlay = ingredientListOverlay;
      this.ingredientFilter = ingredientFilter;
   }

   public boolean hasKeyboardFocus() {
      return this.ingredientListOverlay.hasKeyboardFocus();
   }

   @Nullable
   public ItemStack getStackUnderMouse() {
      Object ingredient = this.ingredientListOverlay.getIngredientUnderMouse();
      return ingredient instanceof ItemStack ? (ItemStack)ingredient : null;
   }

   public void setFilterText(String filterText) {
      ErrorUtil.checkNotNull(filterText, "filterText");
      if (Config.setFilterText(filterText)) {
         this.ingredientListOverlay.onSetFilterText(filterText);
      }

   }

   public String getFilterText() {
      return Config.getFilterText();
   }

   public ImmutableList<ItemStack> getFilteredStacks() {
      List<Object> ingredients = this.ingredientFilter.getFilteredIngredients();
      Builder<ItemStack> builder = ImmutableList.builder();

      for(Object ingredient : ingredients) {
         if (ingredient instanceof ItemStack) {
            builder.add((ItemStack)ingredient);
         }
      }

      return builder.build();
   }

   public ImmutableList<ItemStack> getVisibleStacks() {
      if (this.ingredientListOverlay.isListDisplayed()) {
         Builder<ItemStack> visibleStacks = ImmutableList.builder();

         for(Object ingredient : this.ingredientListOverlay.getVisibleIngredients()) {
            if (ingredient instanceof ItemStack) {
               visibleStacks.add((ItemStack)ingredient);
            }
         }

         return visibleStacks.build();
      } else {
         return ImmutableList.of();
      }
   }

   public void highlightStacks(Collection<ItemStack> stacks) {
   }
}
