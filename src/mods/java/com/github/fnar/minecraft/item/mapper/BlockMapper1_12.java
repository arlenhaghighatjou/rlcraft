package com.github.fnar.minecraft.item.mapper;

import com.github.fnar.minecraft.item.BlockItem;
import greymerk.roguelike.worldgen.MetaBlock1_12;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockMapper1_12 extends BaseItemMapper1_12<BlockItem> {
   public BlockMapper1_12() {
   }

   public Class<BlockItem> getClazz() {
      return BlockItem.class;
   }

   public ItemStack map(BlockItem block) {
      MetaBlock1_12 metaBlock = com.github.fnar.minecraft.block.BlockMapper1_12.map(block.getBlockType());
      Item item = Item.getItemFromBlock(metaBlock.getBlock());
      return new ItemStack(item);
   }
}
