package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class Enchantmentflametier extends EnchantmentBase {
   private static final String[] DAMAGE_NAMES = new String[]{"lfl", "afl", "sfl"};
   private static final int[] MIN_COST = new int[]{10, 35, 160};
   private static final int[] LEVEL_COST = new int[]{0, 0, 0};
   private static final int[] LEVEL_COST_SPAN = new int[]{30, 70, 140};
   public final int damageType;

   public Enchantmentflametier(Rarity rarityIn, EnumEnchantmentType type, int damageTypeIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.WEAPON, slots);
      this.damageType = damageTypeIn;
      this.type = type;
   }

   public boolean isConfigEnabled() {
      switch(this.damageType) {
      case 0:
         return ModConfig.enabled.LesserFlame;
      case 1:
         return ModConfig.enabled.AdvancedFlame;
      case 2:
         return ModConfig.enabled.SupremeFlame;
      default:
         return false;
      }
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return MIN_COST[this.damageType] + (enchantmentLevel - 1) * LEVEL_COST[this.damageType];
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + LEVEL_COST_SPAN[this.damageType];
   }

   public String getName() {
      return "enchantment." + DAMAGE_NAMES[this.damageType];
   }

   public boolean canApplyTogether(Enchantment ench) {
      return !(ench instanceof Enchantmentflametier);
   }

   public boolean isTreasureEnchantment() {
      switch(this.damageType) {
      case 0:
         return false;
      case 1:
         return false;
      case 2:
         return true;
      default:
         return false;
      }
   }
}
