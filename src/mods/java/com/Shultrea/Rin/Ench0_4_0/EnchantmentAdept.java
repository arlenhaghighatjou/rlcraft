package com.Shultrea.Rin.Ench0_4_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;

public class EnchantmentAdept extends EnchantmentBase {
   public EnchantmentAdept() {
      super(Rarity.RARE, EnumList.COMBAT_WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Adept");
      this.setRegistryName("Adept");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Adept;
   }

   public int getMaxLevel() {
      return ModConfig.level.Adept;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 40;
   }

   public int getMinEnchantability(int par1) {
      return 26 + (par1 - 1) * 12;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && fTest != Enchantments.LOOTING && fTest != Smc_030.AdvancedLooting;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public boolean canApply(ItemStack fTest) {
      return fTest.getItem() instanceof ItemAxe ? true : super.canApply(fTest);
   }

   public void register(EventBus bus) {
      bus.register(LivingExperienceDropEvent.class, this::onDeath);
   }

   public void onDeath(LivingExperienceDropEvent fEvent) {
      EntityPlayer player = fEvent.getAttackingPlayer();
      if (player != null) {
         ItemStack stack = player.getHeldItemMainhand();
         if (stack == null || stack.isEmpty()) {
            stack = player.getHeldItemOffhand();
            if (stack == null || stack.isEmpty()) {
               return;
            }
         }

         int lvl = EnchantmentHelper.getEnchantmentLevel(this, stack);
         if (lvl > 0) {
            if (fEvent.getOriginalExperience() > 0) {
               if (fEvent.getEntityLiving() != null && !fEvent.getEntityLiving().isNonBoss()) {
                  fEvent.setDroppedExperience(2 + lvl + (int)((float)fEvent.getOriginalExperience() * (0.75F + 0.5F * (float)lvl)));
               } else {
                  fEvent.setDroppedExperience(2 + lvl + (int)((float)fEvent.getOriginalExperience() * (1.05F + 0.15F * (float)lvl)));
               }

            }
         }
      }
   }
}
