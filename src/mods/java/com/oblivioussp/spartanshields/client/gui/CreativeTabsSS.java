package com.oblivioussp.spartanshields.client.gui;

import com.oblivioussp.spartanshields.init.ItemRegistrySS;
import com.oblivioussp.spartanshields.util.ConfigHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CreativeTabsSS {
   public static final CreativeTabs TAB_SS = new CreativeTabs("spartanshields:tabBasic.name") {
      public ItemStack getIconItemStack() {
         if (!ConfigHandler.disableStandardShields) {
            return new ItemStack(ItemRegistrySS.shieldStone);
         } else {
            return !ConfigHandler.disableTowerShields ? new ItemStack(ItemRegistrySS.shieldTowerStone) : new ItemStack(Items.SHIELD);
         }
      }

      public ItemStack getTabIconItem() {
         return this.getIconItemStack();
      }
   };

   public CreativeTabsSS() {
   }
}
