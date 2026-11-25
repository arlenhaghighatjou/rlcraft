package net.ilexiconn.llibrary.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.property.IBooleanProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CheckboxElement<T extends IElementGUI> extends Element<T> {
   public final IBooleanProperty selected;

   public CheckboxElement(T gui, float posX, float posY, boolean selected) {
      this(gui, posX, posY, new IBooleanProperty.WithState(selected));
   }

   public CheckboxElement(T gui, float posX, float posY, IBooleanProperty selected) {
      super(gui, posX, posY, 12, 12);
      this.selected = selected;
   }

   public void render(float mouseX, float mouseY, float partialTicks) {
      if (this.isEnabled()) {
         this.drawRectangle((double)(this.getPosX() + 1.0F), (double)(this.getPosY() + 1.0F), (double)(this.getWidth() - 1), (double)(this.getHeight() - 1), LLibrary.CONFIG.getSecondaryColor());
      } else {
         this.drawRectangle((double)(this.getPosX() + 1.0F), (double)(this.getPosY() + 1.0F), (double)(this.getWidth() - 1), (double)(this.getHeight() - 1), LLibrary.CONFIG.getTertiaryColor());
      }

      if (this.selected.getBoolean()) {
         this.drawRectangle((double)(this.getPosX() + 3.0F), (double)(this.getPosY() + 3.0F), (double)(this.getWidth() - 5), (double)(this.getHeight() - 5), LLibrary.CONFIG.getTextColor());
      }

   }

   public boolean mouseClicked(float mouseX, float mouseY, int button) {
      if (button == 0 && super.isSelected(mouseX, mouseY)) {
         if (this.selected.trySetBoolean(!this.selected.getBoolean())) {
            this.gui.playClickSound();
         }

         return true;
      } else {
         return false;
      }
   }

   public CheckboxElement<T> withSelection(boolean selected) {
      this.selected.trySetBoolean(selected);
      return this;
   }

   public boolean isSelected() {
      return this.selected.getBoolean();
   }
}
