package com.creativemd.creativecore;

import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;

class CreativeCore$1$1$1 extends GuiButton {
   CreativeCore$1$1$1(Object this$2, String caption, int x, int y) {
      super(caption, x, y);
      this.this$2 = this$2;
   }

   public void onClicked(int x, int y, int button) {
      this.this$2.openYesNoDialog("Really?");
   }
}
