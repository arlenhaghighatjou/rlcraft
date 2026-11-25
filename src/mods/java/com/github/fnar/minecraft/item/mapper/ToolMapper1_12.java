package com.github.fnar.minecraft.item.mapper;

import com.github.fnar.minecraft.item.CouldNotMapItemException;
import com.github.fnar.minecraft.item.Tool;
import com.github.fnar.minecraft.item.ToolType;
import greymerk.roguelike.treasure.loot.Quality;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ToolMapper1_12 extends RldBaseItemMapper1_12<Tool> {
   public ToolMapper1_12() {
   }

   public Class<Tool> getClazz() {
      return Tool.class;
   }

   public ItemStack map(Tool rldItem) {
      Item item = this.map(rldItem, rldItem.getToolType(), rldItem.getQuality());
      return this.map(rldItem, item);
   }

   private Item map(Tool tool, ToolType toolType, Quality quality) {
      switch(toolType) {
      case AXE:
         return EquipmentMapper1_12.map(quality, Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE);
      case FISHING_ROD:
         return Items.FISHING_ROD;
      case FLINT_AND_STEEL:
         return Items.FLINT_AND_STEEL;
      case HOE:
         return EquipmentMapper1_12.map(quality, Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.GOLDEN_HOE, Items.DIAMOND_HOE);
      case PICKAXE:
         return EquipmentMapper1_12.map(quality, Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE);
      case SHEARS:
         return Items.SHEARS;
      case SHOVEL:
         return EquipmentMapper1_12.map(quality, Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL);
      default:
         throw new CouldNotMapItemException(tool);
      }
   }
}
