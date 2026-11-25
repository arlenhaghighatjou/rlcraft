package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Interfaces.IPotionDebuffer;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class EnchantmentLevitator extends EnchantmentBase implements IPotionDebuffer {
   public EnchantmentLevitator() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Levitator");
      this.setRegistryName("Levitator");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.LevitatorEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Levitator;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof IPotionDebuffer);
   }

   public void onEntityDamagedAlt(EntityLivingBase user, Entity entiti, ItemStack stack, int level) {
      if (entiti instanceof EntityLivingBase) {
         EntityLivingBase entity = (EntityLivingBase)entiti;
         entity.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 30 + level * 12, 1 + level));
      }
   }
}
