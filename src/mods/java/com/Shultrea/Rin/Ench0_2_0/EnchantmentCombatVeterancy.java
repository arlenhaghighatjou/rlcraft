package com.Shultrea.Rin.Ench0_2_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentCombatVeterancy extends EnchantmentBase {
   public EnchantmentCombatVeterancy() {
      super(Rarity.RARE, EnumList.COMBAT_WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("CombatRegeneration");
      this.setRegistryName("CombatRegeneration");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.CombatRegenerationEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.CombatRegeneration;
   }

   public int getMinEnchantability(int par1) {
      return 25 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 25;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onRegen(LivingHealEvent e) {
      EntityLivingBase en = e.getEntityLiving();
      if (en != null) {
         if (!en.getHeldItemMainhand().isEmpty()) {
            int level = EnchantmentHelper.getEnchantmentLevel(this, en.getHeldItemMainhand());
            if (level > 0) {
               e.setAmount(e.getAmount() * (1.05F + 0.15F * (float)level));
            }

         }
      }
   }
}
