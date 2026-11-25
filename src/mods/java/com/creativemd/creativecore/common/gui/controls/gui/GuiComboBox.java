package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import java.util.List;
import net.minecraft.init.SoundEvents;

public class GuiComboBox extends GuiLabel {
   public GuiComboBoxExtension extension;
   public List<String> lines;
   public int index;

   public GuiComboBox(String name, int x, int y, int width, List<String> lines) {
      super(name, x, y, width, 14, -1);
      this.lines = lines;
      if (lines.size() > 0) {
         this.caption = this.getDisplay(0);
         this.index = 0;
      } else {
         this.caption = "";
         this.index = -1;
      }

   }

   public String getDisplay(int index) {
      return (String)this.lines.get(index);
   }

   public boolean select(int index) {
      if (index >= 0 && index < this.lines.size()) {
         this.caption = this.getDisplay(index);
         this.index = index;
         this.raiseEvent(new GuiControlChangedEvent(this));
         return true;
      } else {
         return false;
      }
   }

   public boolean select(String line) {
      this.index = this.lines.indexOf(line);
      if (this.index != -1) {
         this.caption = (String)this.lines.get(this.index);
         this.raiseEvent(new GuiControlChangedEvent(this));
         return true;
      } else {
         return false;
      }
   }

   public boolean hasBorder() {
      return true;
   }

   public boolean hasBackground() {
      return true;
   }

   public boolean mousePressed(int posX, int posY, int button) {
      if (this.extension == null) {
         this.openBox();
      } else {
         this.closeBox();
      }

      playSound(SoundEvents.UI_BUTTON_CLICK);
      return true;
   }

   public void openBox() {
      this.extension = this.createBox();
      this.getGui().controls.add(this.extension);
      this.extension.parent = this.getGui();
      this.extension.moveControlToTop();
      this.extension.onOpened();
      this.getGui().refreshControls();
      this.extension.rotation = this.rotation;
      this.extension.posX = this.getPixelOffsetX() - this.getGui().getPixelOffsetX() - this.getContentOffset();
      this.extension.posY = this.getPixelOffsetY() - this.getGui().getPixelOffsetY() - this.getContentOffset() + this.height;
      if (this.extension.posY + this.extension.height > this.getParent().height && this.posY >= this.extension.height) {
         this.extension.posY -= this.height + this.extension.height;
      }

   }

   protected GuiComboBoxExtension createBox() {
      return new GuiComboBoxExtension(this.name + "extension", this, this.posX, this.posY + this.height, this.width - this.getContentOffset() * 2, 100, this.lines);
   }

   public void closeBox() {
      if (this.extension != null) {
         this.getGui().controls.remove(this.extension);
         this.extension = null;
      }

   }
}
