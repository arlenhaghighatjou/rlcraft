package com.creativemd.creativecore.common.gui.premade;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import net.minecraft.nbt.NBTTagCompound;

public class SubGuiDialog extends SubGui {
   public String[] buttons;
   public String[] lines;

   public SubGuiDialog(String[] lines, String[] buttons) {
      super("dialog", Math.max(GuiRenderHelper.instance.getStringWidth(lines) + 20, buttons.length * 30 + 20), lines.length * 20 + 40);
      this.lines = lines;
      this.buttons = buttons;
   }

   public void createControls() {
      int height = 5;

      for(int i = 0; i < this.lines.length; ++i) {
         this.controls.add(new GuiLabel(this.lines[i], 0, height, this.width - this.getContentOffset() * 2, GuiRenderHelper.instance.getFontHeight(), -1));
         height += 20;
      }

      int buttonWidth = 0;
      GuiButton[] createdButtons = new GuiButton[this.buttons.length];

      for(int i = 0; i < this.buttons.length; ++i) {
         GuiButton button = new GuiButton(this.buttons[i], 0, height + 5) {
            public void onClicked(int x, int y, int button) {
               NBTTagCompound nbt = new NBTTagCompound();
               nbt.setString("clicked", this.caption);
               SubGuiDialog.this.closeLayer(nbt);
            }
         };
         buttonWidth += button.width + 2;
         createdButtons[i] = button;
         this.controls.add(button);
      }

      int x = this.width / 2 - buttonWidth / 2 + 1;

      for(int i = 0; i < createdButtons.length; ++i) {
         createdButtons[i].posX = x;
         x += createdButtons[i].width + 2;
      }

   }

   public void saveData(NBTTagCompound nbt) {
      nbt.setBoolean("dialog", true);
      nbt.setString("text", String.join("\n", this.lines));
      nbt.setInteger("count", this.buttons.length);

      for(int i = 0; i < this.buttons.length; ++i) {
         nbt.setString("b" + i, this.buttons[i]);
      }

   }

   public void closeLayer(NBTTagCompound nbt, boolean isPacket) {
      this.saveData(nbt);
      super.closeLayer(nbt, isPacket);
   }
}
