package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;

public class EnchantmentScytheDamage extends EnchantmentBase {
   public EnchantmentScytheDamage() {
      super(Rarity.RARE, EnumList.HOE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("ScytheDamage");
      this.setRegistryName("ScytheDamage");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.ScytheDamage;
   }

   public int getMaxLevel() {
      return ModConfig.level.ScytheDamage;
   }

   public int getMinEnchantability(int par1) {
      return 14 + 12 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return super.canApplyAtEnchantingTable(stack) && stack.getItem() instanceof ItemHoe;
   }

   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      return ModConfig.enabled.ScytheDamage ? 1.0F + 0.55F * (float)level : 0.0F;
   }
}
