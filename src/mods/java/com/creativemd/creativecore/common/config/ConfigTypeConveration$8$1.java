package com.creativemd.creativecore.common.config;

import com.creativemd.creativecore.common.config.gui.GuiConfigSubControl;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBoxBase;
import java.util.List;

class ConfigTypeConveration$8$1 extends GuiButton {
   ConfigTypeConveration$8$1(Object this$0, String caption, int x, int y, int var5, ConfigTypeConveration var6, Class var7, GuiListBoxBase var8) {
      super(caption, x, y);
      this.this$0 = this$0;
      this.val$parentWidth = var5;
      this.val$converation = var6;
      this.val$subClass = var7;
      this.val$listBox = var8;
   }

   public void onClicked(int x, int y, int button) {
      GuiConfigSubControl control = new GuiConfigSubControl("0", 2, 0, this.val$parentWidth, 14);
      this.val$converation.createControls(control, (ConfigKey.ConfigKeyField)null, this.val$subClass, Math.min(100, control.width));
      this.val$listBox.add(control);
   }
}
