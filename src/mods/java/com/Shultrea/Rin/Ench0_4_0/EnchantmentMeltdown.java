package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;

public class EnchantmentMeltdown extends EnchantmentBase {
   public EnchantmentMeltdown() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
      this.setName("meltdown");
      this.setRegistryName("meltdown");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Meltdown;
   }

   public int getMaxLevel() {
      return ModConfig.level.Meltdown;
   }

   public int getMinEnchantability(int par1) {
      return 20 + 20 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 40;
   }

   public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {
      Random random = user.getRNG();
      ItemStack itemstack = EnchantmentHelper.getEnchantedItem(Smc_040.meltdown, user);
      if (Math.random() < (double)(0.05F + 0.05F * (float)level)) {
         if (attacker != null) {
            attacker.getEntityWorld().newExplosion(user, user.posX, user.posY, user.posZ, 1.9F + 0.3F * (float)level, true, false);
         }

         if (!itemstack.isEmpty()) {
            this.damageArmor(itemstack, 10 + random.nextInt(15 * level + 1), user);
         }
      } else if (!itemstack.isEmpty()) {
         this.damageArmor(itemstack, 1 + random.nextInt(3 * level), user);
      }

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
         armor.damageArmor(entity, stack, DamageSource.causeExplosionDamage(entity), amount, slot);
      } else {
         if (entity instanceof EntityPlayerMP) {
            stack.attemptDamageItem(amount, new Random(), (EntityPlayerMP)entity);
         } else {
            stack.damageItem(amount, entity);
         }

      }
   }
}
