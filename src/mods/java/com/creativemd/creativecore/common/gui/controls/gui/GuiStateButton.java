package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;

public class GuiStateButton extends GuiButton {
   public String[] states;

   public GuiStateButton(String name, int index, int x, int y, int width, int height, String... states) {
      super(name, x, y, width, height);
      if (index >= 0 && index < states.length) {
         this.caption = states[index];
      } else {
         this.caption = states[0];
      }

      this.states = states;
   }

   public GuiStateButton(String name, int index, int x, int y, int width, String... states) {
      this(name, index, x, y, width, 14, states);
   }

   public GuiStateButton(String name, int index, int x, int y, String... states) {
      this(name, index, x, y, 15, states);
      this.width = GuiRenderHelper.instance.getStringWidth(this.caption) + this.getContentOffset() * 2;
   }

   public void setState(int index) {
      this.caption = this.states[index];
   }

   public int getState() {
      for(int i = 0; i < this.states.length; ++i) {
         if (this.states[i].equals(this.caption)) {
            return i;
         }
      }

      return -1;
   }

   public void previousState() {
      int state = this.getState();
      --state;
      if (state < 0) {
         state = this.states.length - 1;
      }

      if (state >= this.states.length) {
         state = 0;
      }

      this.setState(state);
      this.raiseEvent(new GuiControlChangedEvent(this));
   }

   public void nextState() {
      int state = this.getState();
      ++state;
      if (state < 0) {
         state = this.states.length - 1;
      }

      if (state >= this.states.length) {
         state = 0;
      }

      this.setState(state);
      this.raiseEvent(new GuiControlChangedEvent(this));
   }

   public void onClicked(int x, int y, int button) {
   }

   public boolean mousePressed(int posX, int posY, int button) {
      if (super.mousePressed(posX, posY, button)) {
         if (button == 1) {
            this.previousState();
         } else {
            this.nextState();
         }

         this.onClicked(posX, posY, button);
         return true;
      } else {
         return false;
      }
   }
}
