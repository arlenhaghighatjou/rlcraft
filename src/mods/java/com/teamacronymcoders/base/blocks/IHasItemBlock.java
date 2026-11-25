package com.teamacronymcoders.base.blocks;

import com.teamacronymcoders.base.items.IAmItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

@FunctionalInterface
public interface IHasItemBlock extends IAmItem {
   ItemBlock getItemBlock();

   default Item getItem() {
      return this.getItemBlock();
   }
}
