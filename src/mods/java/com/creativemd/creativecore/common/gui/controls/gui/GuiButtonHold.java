package com.creativemd.creativecore.common.gui.controls.gui;

import org.lwjgl.input.Mouse;

public abstract class GuiButtonHold extends GuiButton {
   public static final int toWait = 500;
   public long wait = 0L;

   public GuiButtonHold(String caption, int x, int y) {
      super(caption, x, y);
   }

   public GuiButtonHold(String caption, int x, int y, int width) {
      super(caption, caption, x, y, width);
   }

   public GuiButtonHold(String name, String caption, int x, int y, int width) {
      super(name, caption, x, y, width);
   }

   public GuiButtonHold(String name, String caption, int x, int y, int width, int height) {
      super(name, caption, x, y, width, height);
   }

   public GuiButtonHold(String caption, int x, int y, int width, int height) {
      super(caption, caption, x, y, width, height);
   }

   public boolean mousePressed(int x, int y, int button) {
      this.wait = System.currentTimeMillis();
      return super.mousePressed(x, y, button);
   }

   public void mouseMove(int x, int y, int button) {
      if (this.isMouseOver(x, y) && this.wait > 0L) {
         if (Mouse.isButtonDown(button)) {
            if (System.currentTimeMillis() - this.wait >= 500L) {
               this.onClicked(x, y, button);
            }
         } else {
            this.wait = 0L;
         }
      } else {
         this.wait = 0L;
      }

   }
}
