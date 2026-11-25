package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.entity.PortalEntity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ItemStaffSturdy extends ItemStaffSummoning {
   public PortalEntity portalEntity;

   public ItemStaffSturdy(String itemName, String textureName) {
      super(itemName, textureName);
   }

   public int getDurability() {
      return 1000;
   }

   public int getRapidTime(ItemStack itemStack) {
      return 20;
   }

   public int getSummonCostBoost() {
      return 2;
   }

   public float getSummonCostMod() {
      return 1.0F;
   }

   public int getSummonDuration() {
      return 2400;
   }

   public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
      return repairStack.getItem() == Items.GOLD_INGOT ? true : super.getIsRepairable(itemStack, repairStack);
   }
}
