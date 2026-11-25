package mezz.jei.api;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;

public interface IIngredientListOverlay {
   @Nullable
   Object getIngredientUnderMouse();

   boolean hasKeyboardFocus();

   ImmutableList<Object> getVisibleIngredients();
}
