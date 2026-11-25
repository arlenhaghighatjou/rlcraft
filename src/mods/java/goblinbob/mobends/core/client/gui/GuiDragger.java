package goblinbob.mobends.core.client.gui;

public class GuiDragger<T extends IGuiDraggable> {
   private T draggedElement = (T)null;

   public GuiDragger() {
   }

   public void setDraggedElement(T draggedElement) {
      if (this.draggedElement != null) {
         this.draggedElement.setDragged(false);
      }

      this.draggedElement = draggedElement;
      this.draggedElement.setDragged(true);
   }

   public void stopDragging() {
      if (this.draggedElement != null) {
         this.draggedElement.setDragged(false);
      }

      this.draggedElement = null;
   }

   public T getDraggedElement() {
      return this.draggedElement;
   }

   public void update(int mouseX, int mouseY) {
      if (this.draggedElement != null) {
         this.draggedElement.dragTo(mouseX, mouseY);
      }

   }
}
