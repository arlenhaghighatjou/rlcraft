package com.Shultrea.Rin.Ench0_2_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_020;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentParry extends EnchantmentBase {
   public EnchantmentParry() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Parry");
      this.setRegistryName("Parry");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.ParryEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Parry;
   }

   public int getMinEnchantability(int par1) {
      return 20 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 40;
   }

   public void register(EventBus bus) {
      bus.register(LivingAttackEvent.class, EventPriority.LOWEST, false, this::HandleEnchant);
   }

   public void HandleEnchant(LivingAttackEvent e) {
      if (e.getSource().damageType == "player" || e.getSource().damageType == "mob" || e.getSource().damageType != "arrow") {
         if (e.getEntity() instanceof EntityLivingBase) {
            if (e.getSource().getTrueSource() instanceof EntityLivingBase) {
               EntityLivingBase victim = e.getEntityLiving();
               if (victim != null) {
                  ItemStack weaponSword = victim.getHeldItemMainhand();
                  EntityLivingBase attacker = (EntityLivingBase)e.getSource().getTrueSource();
                  if (attacker != null) {
                     if (weaponSword != null) {
                        if (EnchantmentHelper.getEnchantmentLevel(Smc_020.Parry, weaponSword) > 0) {
                           int levelParry = EnchantmentHelper.getEnchantmentLevel(Smc_020.Parry, weaponSword);
                           if (victim.world.rand.nextInt(100) < 16 + levelParry * 8) {
                              if (!attacker.world.isRemote) {
                                 EnchantmentsUtility.ImprovedKnockBack(attacker, 0.3F + 0.15F * (float)levelParry, victim.posX - attacker.posX, victim.posZ - attacker.posZ);
                              }

                              attacker.getEntityWorld().playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.3F, 3.0F);
                              e.setCanceled(true);
                              if (victim instanceof EntityPlayer) {
                                 EntityPlayer player = (EntityPlayer)victim;
                                 player.hurtResistantTime = 15;
                              } else {
                                 e.getEntityLiving().hurtResistantTime = 15;
                              }
                           }

                        }
                     }
                  }
               }
            }
         }
      }
   }
}
