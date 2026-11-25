package com.Shultrea.Rin.Ench0_4_5;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentUnsheathing extends EnchantmentBase {
   public EnchantmentUnsheathing() {
      super(Rarity.VERY_RARE, EnumList.SWORD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Unsheathing");
      this.setRegistryName("Unsheathing");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Unsheathing;
   }

   public int getMaxLevel() {
      return ModConfig.level.Unsheathing;
   }

   public int getMinEnchantability(int par1) {
      return 30 + (par1 - 1) * 15;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onAttack(LivingAttackEvent e) {
      if (!e.isCanceled() && e.getAmount() > 0.0F && e.getSource().getTrueSource() != null && e.getEntityLiving() != null && e.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer victim = (EntityPlayer)e.getEntityLiving();
         InventoryPlayer inv = victim.inventory;

         for(int x = 0; x < InventoryPlayer.getHotbarSize(); ++x) {
            ItemStack stack = inv.getStackInSlot(x);
            if (EnchantmentHelper.getEnchantmentLevel(this, stack) > 0 && EnchantmentsUtility.RANDOM.nextInt(2) > -1 + EnchantmentHelper.getEnchantmentLevel(this, stack) && inv.getStackInSlot(inv.currentItem) != stack) {
               ItemStack s = inv.getStackInSlot(inv.currentItem);
               inv.setInventorySlotContents(inv.currentItem, stack);
               inv.setInventorySlotContents(x, s);
               stack.setAnimationsToGo(0);
            }
         }
      }

   }
}
