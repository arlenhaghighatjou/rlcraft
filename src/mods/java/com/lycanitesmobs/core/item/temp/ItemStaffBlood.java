package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.entity.PortalEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ItemStaffBlood extends ItemStaffSummoning {
   public PortalEntity portalEntity;

   public ItemStaffBlood(String itemName, String textureName) {
      super(itemName, textureName);
   }

   public int getDurability() {
      return 250;
   }

   public int getRapidTime(ItemStack itemStack) {
      return 20;
   }

   public int getSummonCostBoost() {
      return 0;
   }

   public float getSummonCostMod() {
      return 0.5F;
   }

   public int getSummonDuration() {
      return 1200;
   }

   public boolean getAdditionalCosts(EntityPlayer player) {
      if (player.getHealth() <= 7.0F) {
         return false;
      } else {
         player.attackEntityFrom(DamageSource.MAGIC, 6.0F);
         return true;
      }
   }

   public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
      return repairStack.getItem() == Items.GOLD_INGOT ? true : super.getIsRepairable(itemStack, repairStack);
   }
}
