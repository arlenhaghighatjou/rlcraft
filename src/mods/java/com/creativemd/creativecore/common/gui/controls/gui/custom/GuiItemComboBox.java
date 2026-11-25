package com.creativemd.creativecore.common.gui.controls.gui.custom;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAvatarLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBoxExtension;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import java.util.List;
import net.minecraft.item.ItemStack;

public class GuiItemComboBox extends GuiComboBox {
   public List<ItemStack> stacks;

   public GuiItemComboBox(String name, int x, int y, int width, List<String> lines, List<ItemStack> stacks) {
      super(name, x, y, width, lines);
      this.stacks = stacks;
   }

   protected GuiComboBoxExtension createBox() {
      return new GuiItemComboBox.GuiItemComboBoxExtension(this.name + "extension", this, this.posX, this.posY + this.height, this.width - this.getContentOffset() * 2, 100, this.lines, this.stacks);
   }

   public static class GuiItemComboBoxExtension extends GuiComboBoxExtension {
      public List<ItemStack> stacks;

      public GuiItemComboBoxExtension(String name, GuiItemComboBox comboBox, int x, int y, int width, int height, List<String> lines, List<ItemStack> stacks) {
         super(name, comboBox, x, y, width, height, lines);
         this.stacks = stacks;
         this.reloadControls();
      }

      public void reloadControls() {
         this.controls.clear();
         if (this.stacks != null && this.stacks.size() == this.lines.size()) {
            for(final int i = 0; i < this.lines.size(); ++i) {
               int color = 14737632;
               if (i == this.selected) {
                  color = 16777000;
               }

               GuiAvatarLabel label = new GuiAvatarLabel((String)this.lines.get(i), (String)this.lines.get(i), 3, 1 + i * 20, color, new AvatarItemStack((ItemStack)this.stacks.get(i)), false) {
                  public void onClicked(int x, int y, int button) {
                     GuiItemComboBoxExtension.this.selected = i;
                     GuiItemComboBoxExtension.this.onSelectionChange();
                  }

                  public int getColor() {
                     return GuiItemComboBoxExtension.this.selected != i && this.isMouseOver() ? ColorUtils.RGBAToInt(255, 255, 100, 255) : this.color;
                  }
               };
               label.width = this.width - 10;
               label.height = 20;
               this.controls.add(label);
            }
         }

         this.refreshControls();
      }
   }
}
