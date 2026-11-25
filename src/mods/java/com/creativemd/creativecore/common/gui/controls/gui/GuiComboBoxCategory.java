package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.creativecore.common.utils.type.PairList;
import net.minecraft.init.SoundEvents;

public class GuiComboBoxCategory<T> extends GuiLabel {
   public GuiComboBoxExtensionCategory<T> extension;
   public PairList<String, PairList<String, T>> elements;
   public int index;

   public GuiComboBoxCategory(String name, int x, int y, int width, PairList<String, PairList<String, T>> elements) {
      super(name, x, y, width, 14, -1);
      this.elements = elements;
      if (!this.select(0)) {
         this.caption = "";
         this.index = -1;
      }

   }

   public Pair<String, T> getSelected() {
      if (this.index == -1) {
         return null;
      } else {
         int currentIndex = 0;

         for(Pair<String, PairList<String, T>> pair : this.elements) {
            if (this.index < currentIndex + ((PairList)pair.value).size()) {
               return (Pair)((PairList)pair.value).get(this.index - currentIndex);
            }

            currentIndex += ((PairList)pair.value).size();
         }

         return null;
      }
   }

   public boolean select(int index) {
      int currentIndex = 0;
      if (index == -1) {
         return false;
      } else {
         for(Pair<String, PairList<String, T>> pair : this.elements) {
            if (index < currentIndex + ((PairList)pair.value).size()) {
               this.caption = translate((String)((Pair)((PairList)pair.value).get(index - currentIndex)).key);
               this.index = index;
               this.raiseEvent(new GuiControlChangedEvent(this));
               return true;
            }

            currentIndex += ((PairList)pair.value).size();
         }

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

   protected GuiComboBoxExtensionCategory<T> createBox() {
      return new GuiComboBoxExtensionCategory<T>(this.name + "extension", this, this.posX, this.posY + this.height, this.width - this.getContentOffset() * 2, 100);
   }

   public void closeBox() {
      if (this.extension != null) {
         this.getGui().controls.remove(this.extension);
         this.extension = null;
      }

   }
}
