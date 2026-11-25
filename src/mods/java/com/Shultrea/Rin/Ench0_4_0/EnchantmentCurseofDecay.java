package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Interfaces.IEnchantmentCurse;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentCurseofDecay extends EnchantmentBase implements IEnchantmentCurse {
   public EnchantmentCurseofDecay() {
      super(Rarity.VERY_RARE, EnumList.ALL, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setRegistryName("CurseofDecay");
      this.setName("CurseofDecay");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.CurseofDecay;
   }

   public int getMinEnchantability(int par1) {
      return 45;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 45;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof EnchantmentCurseofPossession);
   }

   public boolean isCurse() {
      return true;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   @SubscribeEvent
   public void onThrow(ItemTossEvent fEvent) {
      if (fEvent.getEntityItem() != null && EnchantmentHelper.getEnchantmentLevel(Smc_040.CurseofDecay, fEvent.getEntityItem().getItem()) > 0) {
         fEvent.getEntityItem().lifespan = 80;
         fEvent.getEntityItem().setPickupDelay(10);
      }

   }
}
