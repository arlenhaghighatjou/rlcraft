package com.Shultrea.Rin.Ench0_1_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Interfaces.IEnchantmentDamage;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class EnchantmentBluntness extends EnchantmentBase {
   public EnchantmentBluntness() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Bluntness");
      this.setRegistryName("Bluntness");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.BluntnessEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Bluntness;
   }

   public int getMinEnchantability(int par1) {
      return 14 + 14 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof EnchantmentDamage) && !(fTest instanceof IEnchantmentDamage);
   }

   public boolean canApply(ItemStack fTest) {
      return super.canApply(fTest) && fTest.getItem() instanceof ItemAxe;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public boolean isCurse() {
      return true;
   }

   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      return -0.5F - 0.5F * (float)level;
   }
}
