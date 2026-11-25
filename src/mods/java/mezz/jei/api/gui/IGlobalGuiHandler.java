package mezz.jei.api.gui;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;

public interface IGlobalGuiHandler {
   default Collection<Rectangle> getGuiExtraAreas() {
      return Collections.emptyList();
   }

   @Nullable
   default Object getIngredientUnderMouse(int mouseX, int mouseY) {
      return null;
   }
}
