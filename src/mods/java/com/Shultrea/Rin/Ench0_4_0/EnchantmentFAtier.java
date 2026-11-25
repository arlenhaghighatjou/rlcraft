package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentFAtier extends EnchantmentBase {
   private static final String[] DAMAGE_NAMES = new String[]{"lfa", "afa", "sfa"};
   private static final int[] MIN_COST = new int[]{5, 20, 160};
   private static final int[] LEVEL_COST = new int[]{4, 10, 60};
   private static final int[] LEVEL_COST_SPAN = new int[]{15, 30, 140};
   public final int damageType;

   public EnchantmentFAtier(Rarity rarityIn, EnumEnchantmentType type, int damageTypeIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.WEAPON, slots);
      this.damageType = damageTypeIn;
      this.type = type;
   }

   public boolean isConfigEnabled() {
      switch(this.damageType) {
      case 0:
         return ModConfig.enabled.LesserFireAspect;
      case 1:
         return ModConfig.enabled.AdvancedFireAspect;
      case 2:
         return ModConfig.enabled.SupremeFireAspect;
      default:
         return false;
      }
   }

   public int getMaxLevel() {
      switch(this.damageType) {
      case 0:
         return ModConfig.level.LesserFireAspect;
      case 1:
         return ModConfig.level.AdvancedFireAspect;
      case 2:
         return ModConfig.level.SupremeFireAspect;
      default:
         return 2;
      }
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return MIN_COST[this.damageType] + (enchantmentLevel - 1) * LEVEL_COST[this.damageType];
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + LEVEL_COST_SPAN[this.damageType];
   }

   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      return 0.0F;
   }

   public String getName() {
      return "enchantment." + DAMAGE_NAMES[this.damageType];
   }

   public boolean canApplyTogether(Enchantment ench) {
      return !(ench instanceof EnchantmentFAtier) && ench != Smc_010.FieryEdge && ench != Smc_010.WaterAspect;
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

   public void onEntityDamagedAlt(EntityLivingBase user, Entity target, ItemStack stack, int level) {
      if (this.isEnabled()) {
         if (target instanceof EntityLivingBase) {
            if (this.damageType == 2) {
               if (level > 0) {
                  target.setFire(16 * level);
               }
            } else if (this.damageType == 1) {
               if (level > 0) {
                  target.setFire(8 * level);
               }
            } else if (this.damageType == 0 && level > 0) {
               target.setFire(2 * level);
            }
         }

      }
   }
}
