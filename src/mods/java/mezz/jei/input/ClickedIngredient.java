package mezz.jei.input;

import com.arlenh7.eaglercraft.awt.Rectangle;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.item.ItemStack;

public class ClickedIngredient<V> implements IClickedIngredient<V> {
   private final V value;
   @Nullable
   private final Rectangle area;
   @Nullable
   private IClickedIngredient.IOnClickHandler onClickHandler;
   private boolean allowsCheating;

   @Nullable
   public static <V> ClickedIngredient<V> create(V value, @Nullable Rectangle area) {
      ErrorUtil.checkNotNull(value, "value");
      IngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
      IIngredientHelper<V> ingredientHelper = ingredientRegistry.getIngredientHelper(value);

      try {
         if (ingredientHelper.isValidIngredient(value)) {
            return new ClickedIngredient<V>(value, area);
         }

         String ingredientInfo = ingredientHelper.getErrorInfo(value);
         Log.get().error("Clicked invalid ingredient. Ingredient Info: {}", ingredientInfo);
      } catch (RuntimeException var6) {
         String ingredientInfo = ingredientHelper.getErrorInfo(value);
         Log.get().error("Clicked invalid ingredient. Ingredient Info: {}", ingredientInfo, var6);
      }

      return null;
   }

   private ClickedIngredient(V value, @Nullable Rectangle area) {
      this.value = value;
      this.area = area;
   }

   public V getValue() {
      return this.value;
   }

   @Nullable
   public Rectangle getArea() {
      return this.area;
   }

   public void setAllowsCheating() {
      this.allowsCheating = true;
   }

   public void setOnClickHandler(IClickedIngredient.IOnClickHandler onClickHandler) {
      this.onClickHandler = onClickHandler;
   }

   public ItemStack getCheatItemStack() {
      if (this.allowsCheating) {
         IIngredientRegistry ingredientRegistry = Internal.getIngredientRegistry();
         IIngredientHelper<V> ingredientHelper = ingredientRegistry.getIngredientHelper(this.value);
         return ingredientHelper.getCheatItemStack(this.value);
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void onClickHandled() {
      if (this.onClickHandler != null) {
         this.onClickHandler.onClick();
      }

   }
}
