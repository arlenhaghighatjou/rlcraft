package goblinbob.mobends.core.client.gui.elements;

import java.util.Iterator;
import java.util.LinkedList;

public interface IGuiElementsContainer extends IGuiPositioned {
   LinkedList<IGuiElement> getElements();

   default void addElement(IGuiElement element) {
      this.getElements().add(element);
      element.initGui();
   }

   default void drawChildren(float partialTicks) {
      for(IGuiElement element : this.getElements()) {
         element.draw(partialTicks);
      }

   }

   default void updateChildren(int mouseX, int mouseY) {
      for(IGuiElement element : this.getElements()) {
         element.update(mouseX - this.getX(), mouseY - this.getY());
      }

   }

   default boolean handleMouseClickedChildren(int mouseX, int mouseY, int button) {
      Iterator<IGuiElement> it = this.getElements().descendingIterator();

      while(it.hasNext()) {
         if (((IGuiElement)it.next()).handleMouseClicked(mouseX, mouseY, button)) {
            return true;
         }
      }

      return false;
   }

   default boolean handleMouseReleasedChildren(int mouseX, int mouseY, int button) {
      Iterator<IGuiElement> it = this.getElements().descendingIterator();

      while(it.hasNext()) {
         if (((IGuiElement)it.next()).handleMouseClicked(mouseX, mouseY, button)) {
            return true;
         }
      }

      return false;
   }
}
