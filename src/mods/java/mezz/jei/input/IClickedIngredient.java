package mezz.jei.input;

import java.awt.Rectangle;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public interface IClickedIngredient<V> {
   V getValue();

   @Nullable
   Rectangle getArea();

   ItemStack getCheatItemStack();

   void onClickHandled();

   void setOnClickHandler(IClickedIngredient.IOnClickHandler var1);

   @FunctionalInterface
   public interface IOnClickHandler {
      void onClick();
   }
}
