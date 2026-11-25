package com.Shultrea.Rin.Ench0_2_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_020;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Interfaces.IConditionalDamage;
import com.Shultrea.Rin.Interfaces.IDamageMultiplier;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentMortalitas extends EnchantmentBase {
   public EnchantmentMortalitas() {
      super(Rarity.VERY_RARE, EnumList.COMBAT, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Mortalitas");
      this.setRegistryName("Mortalitas");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Mortalitas;
   }

   public int getMaxLevel() {
      return ModConfig.level.Mortalitas;
   }

   public int getMinEnchantability(int par1) {
      return 18 + 9 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment e) {
      return super.canApplyTogether(e) && !(e instanceof IDamageMultiplier) && !(e instanceof IConditionalDamage);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onDamage(LivingHurtEvent e) {
      if (e.getSource().getTrueSource() != null && e.getSource().getTrueSource() instanceof EntityPlayer && e.getSource().damageType.equals("player")) {
         EntityPlayer attacker = (EntityPlayer)e.getSource().getTrueSource();
         ItemStack stack = attacker.getHeldItemMainhand();
         if (stack.isEmpty() || stack == null) {
            return;
         }

         int level = EnchantmentHelper.getEnchantmentLevel(Smc_020.Mortalitas, stack);
         if (level <= 0) {
            return;
         }

         if (isOffensivePetDisallowed(e.getSource().getImmediateSource(), e.getSource().getTrueSource())) {
            return;
         }

         if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
         }

         float damage = stack.getTagCompound().getFloat("additionalDamage");
         e.setAmount(e.getAmount() + damage);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void Death(LivingDeathEvent fEvent) {
      if (fEvent.getSource().getTrueSource() != null) {
         if (fEvent.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityLivingBase victim = fEvent.getEntityLiving();
            if (victim != null) {
               EntityPlayer attacker = (EntityPlayer)fEvent.getSource().getTrueSource();
               ItemStack stack = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (!stack.isEmpty() && stack != null) {
                  int level = EnchantmentHelper.getEnchantmentLevel(Smc_020.Mortalitas, stack);
                  if (level > 0) {
                     if (!stack.hasTagCompound()) {
                        stack.setTagCompound(new NBTTagCompound());
                     }

                     float amount = stack.getTagCompound().getFloat("additionalDamage");
                     amount = amount + 0.05F / (float)this.getMaxLevel() * (float)level;
                     amount = MathHelper.clamp(amount, 0.0F, (float)level);
                     stack.getTagCompound().setFloat("additionalDamage", amount);
                  }
               }
            }
         }
      }
   }
}
