package com.oblivioussp.spartanshields.enchantment;

import com.oblivioussp.spartanshields.init.EnchantmentRegistry;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class EnchantmentSpikes extends EnchantmentSS {
   public EnchantmentSpikes(Rarity rarityIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnchantmentSS.TYPE_SHIELD, slots);
      this.setName("spikes");
      this.setRegistryName("ssEnchSpikes");
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + 20 * (enchantmentLevel - 1);
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public String getName() {
      return String.format("enchantment.%s:%s", "spartanshields", this.name);
   }

   public void onUserAttacked(EntityLivingBase user, Entity attacker, int level) {
      Random random = user.getRNG();
      ItemStack itemStack = EnchantmentHelper.getEnchantedItem(EnchantmentRegistry.enchSpikes, user);
      ItemStack activeStack = user.getActiveItemStack();
      if (!activeStack.isEmpty() && activeStack == itemStack) {
         if (shouldHit(level, random)) {
            if (attacker != null) {
               attacker.attackEntityFrom(DamageSource.causeThornsDamage(user), (float)getDamage(level, random));
            }

            if (!itemStack.isEmpty()) {
               itemStack.damageItem(9, user);
            }
         } else if (!itemStack.isEmpty()) {
            itemStack.damageItem(3, user);
         }
      }

   }

   public static boolean shouldHit(int level, Random rnd) {
      return level <= 0 ? false : rnd.nextFloat() < 0.2F * (float)level;
   }

   public static int getDamage(int level, Random rnd) {
      return level == 1 ? 2 : 2 + rnd.nextInt(level - 1);
   }
}
