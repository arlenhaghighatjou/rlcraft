package svenhjol.charm.enchanting.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import svenhjol.charm.enchanting.feature.Salvage;
import svenhjol.meson.MesonEnchantment;

public class EnchantmentSalvage extends MesonEnchantment {
   public EnchantmentSalvage() {
      super("salvage", Rarity.COMMON, EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.MAINHAND);
   }

   public String getModId() {
      return "charm";
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return Salvage.minEnchantability;
   }

   public int getMaxLevel() {
      return 1;
   }
}
