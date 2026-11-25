package com.blamejared.ctgui.api;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CheckButtonRecipe extends CheckButton {
   private String propertyKey;

   public CheckButtonRecipe(GuiBase parent, int id, int xPos, int yPos, String displayString, boolean isChecked, String propertyKey) {
      super(parent, id, xPos, yPos, displayString, isChecked);
      this.propertyKey = propertyKey;
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
         this.setIsChecked(!this.isChecked());
         this.parent.selectedSlot.getPropertyMap().put(this.getPropertyKey(), Boolean.valueOf(this.isChecked()));
         if (this.isChecked()) {
            this.updateSlider();
         } else {
            for(Slider slider : this.parent.getMenu().getSliders(this)) {
               this.parent.getButtonList().remove(slider);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void updateSlider() {
      for(Slider slider : this.parent.getMenu().getSliders(this)) {
         if (GuiBase.isBlock(this.parent.selectedSlot.getStack())) {
            Block b = Block.getBlockFromItem(this.parent.selectedSlot.getStack().getItem());
            NonNullList<ItemStack> list = NonNullList.create();
            b.getSubBlocks(CreativeTabs.SEARCH, list);
            slider.setValue((double)this.parent.selectedSlot.getProperty(slider.getPropertyKey()));
            slider.maxValue = (double)(list.size() - 1);
            slider.updateSlider();
         } else if (this.parent.selectedSlot.getStack().getItem().getHasSubtypes()) {
            NonNullList<ItemStack> list = NonNullList.create();
            this.parent.selectedSlot.getStack().getItem().getSubItems(CreativeTabs.SEARCH, list);
            slider.setValue((double)this.parent.selectedSlot.getProperty(slider.getPropertyKey()));
            slider.maxValue = (double)(list.size() - 1);
            slider.updateSlider();
         } else {
            slider.setValue((double)this.parent.selectedSlot.getProperty(slider.getPropertyKey()));
            slider.maxValue = (double)this.parent.selectedSlot.getStack().getMaxDamage();
            slider.updateSlider();
         }

         this.parent.getButtonList().add(slider);
      }

   }

   public void setIsChecked(boolean isChecked) {
      super.setIsChecked(isChecked);
      if (this.isChecked()) {
         this.updateSlider();

         for(GuiButton but : this.incompatible) {
            but.enabled = false;
         }
      } else {
         for(Slider slider : this.parent.getMenu().getSliders(this)) {
            this.parent.getButtonList().remove(slider);
         }

         for(GuiButton but : this.incompatible) {
            but.enabled = true;
         }
      }

   }

   public String getPropertyKey() {
      return this.propertyKey;
   }
}
