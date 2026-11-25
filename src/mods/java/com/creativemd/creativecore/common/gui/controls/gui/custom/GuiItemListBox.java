package com.creativemd.creativecore.common.gui.controls.gui.custom;

import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAvatarLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiLabel;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBox;
import java.util.List;
import net.minecraft.item.ItemStack;

public class GuiItemListBox extends GuiListBox {
   public List<ItemStack> stacks;
   public boolean centered;

   public GuiItemListBox(String name, int x, int y, int width, int height, List<ItemStack> stacks, List<String> lines, boolean centered) {
      super(name, x, y, width, height, lines);
      this.stacks = stacks;
      this.centered = centered;
      this.reloadControls();
   }

   public GuiItemListBox(String name, int x, int y, int width, int height, List<ItemStack> stacks, List<String> lines) {
      this(name, x, y, width, height, stacks, lines, true);
   }

   public void clear() {
      this.stacks.clear();
      super.clear();
      this.reloadControls();
   }

   public void add(String input, ItemStack stack) {
      this.stacks.add(stack);
      this.lines.add(input);
      this.reloadControls();
   }

   public ItemStack getSelectedStack() {
      return this.selected >= 0 && this.selected < this.stacks.size() ? (ItemStack)this.stacks.get(this.selected) : null;
   }

   public void reloadControls() {
      this.controls.clear();
      if (this.stacks != null && this.stacks.size() == this.lines.size()) {
         for(final int i = 0; i < this.lines.size(); ++i) {
            int color = 14737632;
            if (i == this.selected) {
               color = 16777000;
            }

            GuiAvatarLabel label = new GuiAvatarLabel((String)this.lines.get(i), 3, 1 + i * 20, color, new AvatarItemStack((ItemStack)this.stacks.get(i))) {
               public void onClicked(int x, int y, int button) {
                  if (GuiItemListBox.this.selected != -1 && GuiItemListBox.this.selected < GuiItemListBox.this.controls.size()) {
                     ((GuiLabel)GuiItemListBox.this.controls.get(GuiItemListBox.this.selected)).color = 14737632;
                  }

                  GuiItemListBox.this.selected = i;
                  ((GuiLabel)GuiItemListBox.this.controls.get(GuiItemListBox.this.selected)).color = 16777000;
                  GuiItemListBox.this.onSelectionChange();
               }
            };
            if (this.centered) {
               label.width = this.width - 20;
            }

            label.height = 20;
            this.controls.add(label);
         }
      }

      this.refreshControls();
   }
}
