package com.teamacronymcoders.base.items;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface IHasSubItems extends IAmItem {
   default List<ItemStack> getAllSubItems(List<ItemStack> itemStacks) {
      itemStacks.add(new ItemStack(this.getItem(), 1, 0));
      return itemStacks;
   }
}
