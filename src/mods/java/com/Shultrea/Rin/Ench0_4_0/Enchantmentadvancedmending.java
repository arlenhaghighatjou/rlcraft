package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Enchantmentadvancedmending extends EnchantmentBase {
   public Enchantmentadvancedmending() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
      this.setName("advancedmending");
      this.setRegistryName("advancedmending");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.AdvancedMending;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public int getMinEnchantability(int par1) {
      return 40 * par1;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 65;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest == Enchantments.MENDING ? false : super.canApplyTogether(fTest);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return stack.isItemStackDamageable() && super.canApplyAtEnchantingTable(stack);
   }

   @SubscribeEvent
   public void onXP(PlayerPickupXpEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      EntityXPOrb orb = event.getOrb();
      if (orb.xpValue > 0 && ModConfig.miscellaneous.EnableDoubleXPBug) {
         player.addExperience(orb.xpValue);
      }

      ItemStack itemstack = EnchantmentHelper.getEnchantedItem(this, player);
      if (!itemstack.isEmpty() && itemstack.isItemDamaged()) {
         int value = Math.min(orb.xpValue * 3, itemstack.getItemDamage());
         itemstack.setItemDamage(itemstack.getItemDamage() - value);
         orb.xpValue -= value / 2;
         if (orb.xpValue < 0) {
            orb.xpValue = 0;
         }
      }

   }

   private int xpToDurability(int xp) {
      return xp * 3;
   }
}
