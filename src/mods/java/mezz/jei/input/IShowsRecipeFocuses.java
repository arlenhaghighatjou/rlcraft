package mezz.jei.input;

import javax.annotation.Nullable;

public interface IShowsRecipeFocuses {
   @Nullable
   IClickedIngredient<?> getIngredientUnderMouse(int var1, int var2);

   boolean canSetFocusWithMouse();
}
