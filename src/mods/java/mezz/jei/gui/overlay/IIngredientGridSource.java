package mezz.jei.gui.overlay;

import java.util.List;
import mezz.jei.gui.ingredients.IIngredientListElement;

public interface IIngredientGridSource {
   List<IIngredientListElement> getIngredientList();

   int size();

   void addListener(IIngredientGridSource.Listener var1);

   public interface Listener {
      void onChange();
   }
}
