package com.github.fnar.minecraft.item.mapper;

import greymerk.roguelike.treasure.loot.Quality;
import net.minecraft.item.Item;

public class EquipmentMapper1_12 {
   public EquipmentMapper1_12() {
   }

   public static Item map(Quality quality, Item wooden, Item stone, Item iron, Item golden, Item diamond) {
      switch(quality) {
      case WOOD:
         return wooden;
      case STONE:
         return stone;
      case IRON:
         return iron;
      case GOLD:
         return golden;
      case DIAMOND:
         return diamond;
      default:
         return null;
      }
   }
}
