package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.util.StringHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMultiSW extends ItemSW {
   protected String[] unlocNames;

   public ItemMultiSW(String registryName, String... unlocalizedNames) {
      super(registryName);
      this.unlocNames = unlocalizedNames;
      this.hasSubtypes = true;
   }

   public String[] getAllUnlocalizedNames() {
      return this.unlocNames;
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (tab == this.getCreativeTab()) {
         if (this.unlocNames == null) {
            super.getSubItems(tab, subItems);
            return;
         }

         for(int i = 0; i < this.unlocNames.length; ++i) {
            subItems.add(new ItemStack(this, 1, i));
         }
      }

   }

   public String getItemStackDisplayName(ItemStack stack) {
      if (stack.getItemDamage() >= this.unlocNames.length) {
         return super.getItemStackDisplayName(stack);
      } else {
         String unlocName = this.unlocNames[stack.getItemDamage()];
         return StringHelper.translateString(unlocName + ".name", "item");
      }
   }
}
