package mezz.jei.api.gui;

import java.awt.Rectangle;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.inventory.GuiContainer;

public interface IAdvancedGuiHandler<T extends GuiContainer> {
   Class<T> getGuiContainerClass();

   @Nullable
   default List<Rectangle> getGuiExtraAreas(T guiContainer) {
      return null;
   }

   @Nullable
   default Object getIngredientUnderMouse(T guiContainer, int mouseX, int mouseY) {
      return null;
   }
}
