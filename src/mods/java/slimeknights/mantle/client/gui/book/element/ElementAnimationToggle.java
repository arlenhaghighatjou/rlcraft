package slimeknights.mantle.client.gui.book.element;

import slimeknights.mantle.client.gui.book.GuiArrow;

public class ElementAnimationToggle extends ElementArrow {
   private final int arrowColorActive;
   private final int arrowColorInactive;
   private boolean toggled = false;

   public ElementAnimationToggle(int id, IButtonClickHandler handler, int x, int y, GuiArrow.ArrowType arrowType, int arrowColor, int arrowColorHover, int arrowColorActive) {
      super(id, handler, x, y, arrowType, arrowColor, arrowColorHover);
      this.arrowColorInactive = arrowColor;
      this.arrowColorActive = arrowColorActive;
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.callback != null && this.isHovered(mouseX, mouseY) && this.callback.onButtonClick(this.buttonId, this)) {
         this.toggled = !this.toggled;
         this.updateColor();
      }

   }

   protected void updateColor() {
      this.button.color = this.toggled ? this.arrowColorActive : this.arrowColorInactive;
   }
}
