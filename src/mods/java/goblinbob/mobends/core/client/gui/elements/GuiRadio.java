package goblinbob.mobends.core.client.gui.elements;

import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.core.client.gui.IChangeListener;
import goblinbob.mobends.core.client.gui.IObservable;
import goblinbob.mobends.core.util.Draw;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;

public class GuiRadio implements IObservable {
   private int x;
   private int y;
   private int buttonX;
   private int buttonY;
   private int buttonWidth;
   private int buttonHeight;
   public int elementX;
   public int elementY;
   public int elementWidth;
   public int elementHeight;
   public int offsetX;
   public int offsetY;
   public int padding;
   public int elementOffset;
   public int bgX;
   public int bgY;
   public int bgWidth;
   public int bgHeight;
   public int numberOfElements = 5;
   public int selectedId;
   public int hoveredId;
   public boolean enabled;
   private List<IChangeListener> changeListeners = new LinkedList();

   public List<IChangeListener> getChangeListeners() {
      return this.changeListeners;
   }

   public GuiRadio() {
      this.x = this.y = 0;
      this.enabled = true;
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void display() {
      if (this.isEnabled()) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiBendsMenu.ICONS_TEXTURE);
         Draw.texturedModalRect(this.x, this.y, this.bgX, this.bgY, this.bgWidth, this.bgHeight);

         for(int i = 0; i < this.numberOfElements; ++i) {
            if (i == this.selectedId) {
               Draw.texturedModalRect(this.x + this.offsetX + i * this.buttonWidth, this.y + this.offsetY, this.buttonX + this.buttonWidth * 2, this.buttonY, this.buttonWidth, this.buttonHeight);
            } else if (i == this.hoveredId) {
               Draw.texturedModalRect(this.x + this.offsetX + i * this.buttonWidth, this.y + this.offsetY, this.buttonX + this.buttonWidth, this.buttonY, this.buttonWidth, this.buttonHeight);
            } else {
               Draw.texturedModalRect(this.x + this.offsetX + i * this.buttonWidth, this.y + this.offsetY, this.buttonX, this.buttonY, this.buttonWidth, this.buttonHeight);
            }

            Draw.texturedModalRect(this.x + i * this.buttonWidth + this.elementOffset, this.y + this.elementOffset, this.elementX + i * this.elementWidth, this.elementY, this.elementWidth, this.elementHeight);
         }

      }
   }

   public void update(int mouseX, int mouseY) {
      if (this.isEnabled()) {
         if (this.areCoordinatesInBounds(mouseX, mouseY)) {
            this.hoveredId = Math.min((mouseX - (this.x + 2)) / this.buttonWidth, this.numberOfElements - 1);
         } else {
            this.hoveredId = -1;
         }

      }
   }

   public boolean onMousePressed(int mouseX, int mouseY, int event) {
      if (!this.isEnabled()) {
         return false;
      } else {
         int newSelectedId = this.selectedId;
         if (this.hoveredId != -1) {
            newSelectedId = this.hoveredId;
         }

         if (newSelectedId != this.selectedId) {
            this.selectedId = newSelectedId;
            this.notifyChanged();
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean areCoordinatesInBounds(int cX, int cY) {
      return cX >= this.x + this.offsetX && cX < this.x + this.bgWidth && cY >= this.y + this.offsetY && cY < this.y + this.bgHeight;
   }

   public void choose(int id) {
      this.selectedId = id;
   }

   public void enable() {
      this.enabled = true;
   }

   public void disable() {
      this.enabled = false;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public GuiRadio setButton(int x, int y, int width, int height) {
      this.buttonX = x;
      this.buttonY = y;
      this.buttonWidth = width;
      this.buttonHeight = height;
      return this;
   }

   public GuiRadio setElement(int x, int y, int width, int height) {
      this.elementX = x;
      this.elementY = y;
      this.elementWidth = width;
      this.elementHeight = height;
      return this;
   }

   public GuiRadio setNumberOfElements(int a) {
      this.numberOfElements = a;
      return this;
   }

   public GuiRadio setBackground(int x, int y, int width, int height) {
      this.bgX = x;
      this.bgY = y;
      this.bgWidth = width;
      this.bgHeight = height;
      return this;
   }

   public GuiRadio setOffset(int x, int y) {
      this.offsetX = x;
      this.offsetY = y;
      return this;
   }

   public GuiRadio setPadding(int padding, int offset) {
      this.padding = padding;
      this.elementOffset = offset;
      return this;
   }
}
