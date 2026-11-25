package mezz.jei.gui;

import mezz.jei.Internal;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.LegacyUtil;

public class Focus<V> implements IFocus<V> {
   private final IFocus.Mode mode;
   private final V value;

   public Focus(IFocus.Mode mode, V value) {
      this.mode = mode;
      IIngredientHelper<V> ingredientHelper = Internal.getIngredientRegistry().getIngredientHelper(value);
      this.value = (V)LegacyUtil.getIngredientCopy(value, ingredientHelper);
      checkInternal(this);
   }

   public V getValue() {
      return this.value;
   }

   public IFocus.Mode getMode() {
      return this.mode;
   }

   public static <V> Focus<V> check(IFocus<V> focus) {
      ErrorUtil.checkNotNull(focus, "focus");
      if (focus instanceof Focus) {
         checkInternal(focus);
         return (Focus)focus;
      } else {
         return new Focus<V>(focus.getMode(), focus.getValue());
      }
   }

   private static void checkInternal(IFocus<?> focus) {
      ErrorUtil.checkNotNull(focus.getMode(), "focus mode");
      Object value = focus.getValue();
      ErrorUtil.checkIsValidIngredient(value, "focus value");
   }
}
