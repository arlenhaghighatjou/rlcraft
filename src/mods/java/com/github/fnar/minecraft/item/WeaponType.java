package com.github.fnar.minecraft.item;

import greymerk.roguelike.treasure.loot.Equipment;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public enum WeaponType {
   BOW,
   SWORD;

   private WeaponType() {
   }

   public static WeaponType random(EaglercraftRandom random) {
      int choice = random.nextInt(values().length);
      return values()[choice];
   }

   public Weapon asItem() {
      return new Weapon(this);
   }

   public Equipment asEquipment() {
      switch(this) {
      case BOW:
         return Equipment.BOW;
      case SWORD:
         return Equipment.SWORD;
      default:
         throw new IllegalArgumentException("Unexpected WeaponType: " + this);
      }
   }
}
