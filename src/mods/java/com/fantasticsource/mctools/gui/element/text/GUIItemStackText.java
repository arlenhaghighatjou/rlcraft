package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import net.minecraft.item.ItemStack;

public class GUIItemStackText extends GUIText {
   private ItemStack stack;

   public GUIItemStackText(GUIScreen screen, ItemStack stack) {
      this(screen, stack, 1.0D);
   }

   public GUIItemStackText(GUIScreen screen, ItemStack stack, double scale) {
      super(screen, "", scale);
      this.setStack(stack);
   }

   public GUIItemStackText(GUIScreen screen, double x, double y, ItemStack stack) {
      this(screen, x, y, stack, 1.0D);
   }

   public GUIItemStackText(GUIScreen screen, double x, double y, ItemStack stack, double scale) {
      super(screen, x, y, "", scale);
      this.setStack(stack);
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public GUIItemStackText setStack(ItemStack stack) {
      this.stack = stack;
      this.text = stack.isEmpty() ? "(Empty Slot)" : stack.getCount() + " " + stack.getDisplayName();
      this.tooltip = stack;
      return this;
   }
}
