package de.impelon.disenchanter.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public interface IDisenchantmentItemHandler extends IItemHandler {
   void setSourceStack(ItemStack var1);

   void setReceiverStack(ItemStack var1);

   void setOutputStack(ItemStack var1);

   ItemStack getSourceStack();

   ItemStack getReceiverStack();

   ItemStack getOutputStack();
}
