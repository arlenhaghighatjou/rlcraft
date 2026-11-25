package com.creativemd.creativecore.common.gui.event.gui;

import com.creativemd.creativecore.common.gui.GuiControl;

public class GuiControlChangedEvent extends GuiControlEvent {
   public GuiControlChangedEvent(GuiControl source) {
      super(source);
   }

   public boolean isCancelable() {
      return false;
   }
}
