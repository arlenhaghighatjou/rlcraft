package com.oblivioussp.spartanshields.enchantment;

import com.google.common.base.Predicate;
import com.oblivioussp.spartanshields.item.ItemShieldBase;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.util.EnumHelper;

public abstract class EnchantmentSS extends Enchantment {
   public static EnumEnchantmentType TYPE_SHIELD = EnumHelper.addEnchantmentType("ss_shield", (item) -> {
      return item instanceof ItemShieldBase;
   });

   protected EnchantmentSS(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
      super(rarityIn, typeIn, slots);
   }

   public abstract void onUserAttacked(EntityLivingBase var1, Entity var2, int var3);
}
