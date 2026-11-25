package com.creativemd.creativecore.common.gui.event.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import java.util.List;

public class GuiToolTipEvent extends GuiControlEvent {
   public List<String> tooltip;

   public GuiToolTipEvent(List<String> tooltip, GuiControl source) {
      super(source);
      this.tooltip = tooltip;
   }

   public boolean isCancelable() {
      return true;
   }
}
