package com.creativemd.creativecore.common.config.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfigSubControl extends GuiParent {
   public GuiTextfield nameField;
   public GuiLabel nameLabel;

   public GuiConfigSubControl(String name, int x, int y, int width, int height) {
      super(name, x, y, width, height);
      this.borderWidth = this.marginWidth = 0;
   }

   public String getName() {
      if (this.nameLabel != null) {
         return this.nameLabel.getCaption();
      } else {
         return this.nameField != null ? this.nameField.text : "";
      }
   }

   public void addNameUnmodifieable(String name) {
      this.nameLabel = new GuiLabel("label", 0, 0);
      this.nameLabel.setCaption(name);
      int offsetY = this.nameLabel.height + 2;

      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl var10000 = (GuiControl)this.controls.get(i);
         var10000.posY += offsetY;
      }

      this.height += offsetY;
      this.addControl(this.nameLabel);
   }

   public void addNameTextfield(String name) {
      this.nameField = new GuiTextfield(name, 0, 0, this.width - 50);
      int offsetY = this.nameField.height + 2;

      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl var10000 = (GuiControl)this.controls.get(i);
         var10000.posY += offsetY;
      }

      this.height += offsetY;
      this.addControl(this.nameField);
   }

   public boolean hasBorder() {
      return false;
   }

   public boolean hasBackground() {
      return false;
   }

   public boolean hasMouseOverEffect() {
      return false;
   }
}
