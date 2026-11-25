package com.Shultrea.Rin.Ench0_1_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Interfaces.IEnchantmentDamage;
import com.Shultrea.Rin.Interfaces.IEnhancedEnchantment;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class EnchantmentAdvancedSharpness extends EnchantmentBase implements IEnchantmentDamage, IEnhancedEnchantment {
   public EnchantmentAdvancedSharpness() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("AdvancedSharpness");
      this.setRegistryName("AdvancedSharpness");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.AdvancedSharpnessEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.AdvancedSharpness;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof EnchantmentDamage) && !(fTest instanceof IEnchantmentDamage);
   }

   public boolean canApply(ItemStack fTest) {
      return fTest.getItem() instanceof ItemAxe ? true : super.canApply(fTest);
   }

   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      return 1.25F + 0.95F * (float)level;
   }
}
