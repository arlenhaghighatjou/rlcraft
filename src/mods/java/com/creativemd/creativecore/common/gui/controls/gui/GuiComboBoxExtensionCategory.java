package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.creativecore.common.utils.type.PairList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.Color;

public class GuiComboBoxExtensionCategory<T> extends GuiScrollBox {
   public GuiComboBoxCategory<T> comboBox;
   public Pair<String, T> selected;

   public GuiComboBoxExtensionCategory(String name, GuiComboBoxCategory<T> comboBox, int x, int y, int width, int height) {
      super(name, x, y, width, height);
      this.comboBox = comboBox;
      this.selected = comboBox.getSelected();
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

   public void reloadControls() {
      this.controls.clear();
      final int index = 0;
      int height = 0;

      for(Pair<String, PairList<String, T>> category : this.comboBox.elements) {
         if (category.key != null && !((String)category.key).isEmpty()) {
            this.controls.add(new GuiLabel((String)category.key, TextFormatting.UNDERLINE + "" + TextFormatting.ITALIC + translate((String)category.key), 0, height));
            height += 15;
         }

         for(Pair<String, T> pair : (PairList)category.value) {
            int color = 14737632;
            if (pair == this.selected) {
               color = 16777000;
            }

            GuiClickableLabel label = new GuiClickableLabel((String)pair.key, translate((String)pair.key), 0, height, this.width - this.scrollbarWidth - 2 - this.getContentOffset() * 2, 8, color) {
               public int getColor() {
                  return this.isMouseOver() && this.color != 16777000 ? ColorUtils.RGBAToInt(new Color(255, 255, 100)) : this.color;
               }

               public void onClicked(int x, int y, int button) {
                  GuiComboBoxExtensionCategory.this.onSelectionChange(index);
               }
            };
            this.controls.add(label);
            ++index;
            height += 15;
         }
      }

      this.refreshControls();
   }

   public void onSelectionChange(int index) {
      if (index != -1) {
         this.comboBox.select(index);
      }

      this.comboBox.closeBox();
   }

   public boolean canOverlap() {
      return true;
   }
}
