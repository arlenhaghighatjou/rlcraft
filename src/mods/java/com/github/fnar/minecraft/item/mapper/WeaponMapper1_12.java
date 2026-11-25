package com.github.fnar.minecraft.item.mapper;

import com.github.fnar.minecraft.item.CouldNotMapItemException;
import com.github.fnar.minecraft.item.Weapon;
import com.github.fnar.minecraft.item.WeaponType;
import greymerk.roguelike.treasure.loot.Quality;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WeaponMapper1_12 extends RldBaseItemMapper1_12<Weapon> {
   public WeaponMapper1_12() {
   }

   public Class<Weapon> getClazz() {
      return Weapon.class;
   }

   public ItemStack map(Weapon rldItem) {
      Item item = this.map(rldItem, rldItem.getWeaponType(), rldItem.getQuality());
      return this.map(rldItem, item);
   }

   private Item map(Weapon item, WeaponType weaponType, Quality quality) {
      switch(weaponType) {
      case BOW:
         return EquipmentMapper1_12.map(quality, Items.BOW, Items.BOW, Items.BOW, Items.BOW, Items.BOW);
      case SWORD:
         return EquipmentMapper1_12.map(quality, Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD);
      default:
         throw new CouldNotMapItemException(item);
      }
   }
}
