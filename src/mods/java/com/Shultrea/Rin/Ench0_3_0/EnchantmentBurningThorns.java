package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

public class EnchantmentBurningThorns extends EnchantmentBase {
   public EnchantmentBurningThorns() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET});
      this.setName("BurningThorns");
      this.setRegistryName("BurningThorns");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.BurningThorns;
   }

   public int getMaxLevel() {
      return ModConfig.level.BurningThorns;
   }

   public int getMinEnchantability(int par1) {
      return 12 + (par1 - 1) * 9;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest != Smc_030.AdvancedThorns && fTest != Enchantments.THORNS ? super.canApplyTogether(fTest) : false;
   }

   public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {
      if (user != null) {
         if (attacker != null && !attacker.isDead) {
            EaglercraftRandom random = user.getRNG();
            ItemStack itemstack = EnchantmentHelper.getEnchantedItem(Enchantments.THORNS, user);
            if (shouldHit(level, random)) {
               if (attacker != null) {
                  attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), (float)getDamage(level, random));
                  attacker.setFire(1 * level + 2);
               }

               if (!itemstack.isEmpty()) {
                  this.damageArmor(itemstack, 5, user);
               }
            } else if (!itemstack.isEmpty()) {
               this.damageArmor(itemstack, 2, user);
            }

         }
      }
   }

   public static boolean shouldHit(int level, EaglercraftRandom rnd) {
      return level <= 0 ? false : rnd.nextFloat() < 0.14F * (float)level;
   }

   public static int getDamage(int level, EaglercraftRandom rnd) {
      return level > 10 ? level - 9 : 2 + rnd.nextInt(3);
   }

   private void damageArmor(ItemStack stack, int amount, EntityLivingBase entity) {
      int slot = -1;
      int x = 0;

      for(ItemStack i : entity.getArmorInventoryList()) {
         if (i == stack) {
            slot = x;
            break;
         }

         ++x;
      }

      if (slot != -1 && stack.getItem() instanceof ISpecialArmor) {
         ISpecialArmor armor = (ISpecialArmor)stack.getItem();
         armor.damageArmor(entity, stack, DamageSource.causeThornsDamage(entity), amount, slot);
      } else {
         stack.damageItem(1, entity);
      }
   }
}
