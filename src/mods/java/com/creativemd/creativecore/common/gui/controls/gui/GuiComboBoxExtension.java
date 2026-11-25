package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import java.util.List;
import net.minecraft.util.math.Vec3d;

public class GuiComboBoxExtension extends GuiListBox {
   public GuiComboBox comboBox;

   public GuiComboBoxExtension(String name, GuiComboBox comboBox, int x, int y, int width, int height, List<String> lines) {
      super(name, x, y, width, height, lines);
      this.comboBox = comboBox;
      this.selected = comboBox.index;
      this.reloadControls();
   }

   public Vec3d getCenterOffset() {
      return new Vec3d((double)(this.width / 2), (double)(-this.comboBox.height / 2), 0.0D);
   }

   public void onLoseFocus() {
      if (!this.comboBox.isMouseOver() && !this.isMouseOver()) {
         this.comboBox.closeBox();
      }

   }

   public void onSelectionChange() {
      if (this.selected != -1 && this.selected < this.lines.size()) {
         this.comboBox.caption = this.comboBox.getDisplay(this.selected);
         this.comboBox.index = this.selected;
         this.comboBox.raiseEvent(new GuiControlChangedEvent(this.comboBox));
      }

      this.comboBox.closeBox();
   }

   public boolean canOverlap() {
      return true;
   }
}
