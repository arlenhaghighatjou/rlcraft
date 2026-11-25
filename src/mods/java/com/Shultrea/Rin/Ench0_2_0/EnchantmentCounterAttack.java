package com.Shultrea.Rin.Ench0_2_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentCounterAttack extends EnchantmentBase {
   public EnchantmentCounterAttack() {
      super(Rarity.RARE, EnumList.SWORD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("CounterAttack");
      this.setRegistryName("CounterAttack");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.CounterAttackEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.CounterAttack;
   }

   public int getMinEnchantability(int par1) {
      return 20 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 40;
   }

   public void onUserHurt(EntityLivingBase user, Entity target, int level) {
      if (target instanceof EntityLivingBase) {
         EntityLivingBase attacker = (EntityLivingBase)target;
         if (user instanceof EntityPlayer && attacker.getRNG().nextInt(100) < 20 + level * 5) {
            EntityPlayer player = (EntityPlayer)user;
            player.hurtResistantTime = 20;
            player.attackTargetEntityWithCurrentItem(attacker);
         }
      }

   }
}
