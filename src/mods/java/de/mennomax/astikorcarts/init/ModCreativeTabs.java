package de.mennomax.astikorcarts.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ModCreativeTabs {
   public static CreativeTabs astikor = new CreativeTabs("astikorcarts") {
      public ItemStack getTabIconItem() {
         return new ItemStack(ModItems.WHEEL);
      }
   };

   public ModCreativeTabs() {
   }
}
