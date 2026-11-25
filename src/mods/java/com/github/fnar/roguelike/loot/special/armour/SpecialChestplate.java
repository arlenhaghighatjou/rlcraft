package com.github.fnar.roguelike.loot.special.armour;

import com.github.fnar.minecraft.item.ArmourType;
import com.github.fnar.minecraft.item.RldItem;
import greymerk.roguelike.treasure.loot.Quality;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class SpecialChestplate extends SpecialArmour {
   public SpecialChestplate(EaglercraftRandom random, Quality quality) {
      this.withQuality(quality);
      this.withRldItem(this.getItem());
      this.withName(this.getChestplateName());
      this.withChestplateEnchantments(random);
      this.withCommonEnchantments(random);
      this.withArmourPrefix();
      this.withRandomColour(random);
   }

   private void withChestplateEnchantments(EaglercraftRandom random) {
      this.withArmorEnchantments(random);
   }

   private RldItem getItem() {
      return ArmourType.CHESTPLATE.asItem().withQuality(this.getQuality());
   }

   private String getChestplateName() {
      switch(this.getQuality()) {
      case DIAMOND:
         return "Plate";
      case GOLD:
      case IRON:
         return "Cuirass";
      case STONE:
         return "Hauberk";
      case WOOD:
      default:
         return "Tunic";
      }
   }
}
