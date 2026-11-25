package com.ferreusveritas.dynamictrees;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModTabs {
   public static final CreativeTabs dynamicTreesTab = new CreativeTabs("dynamictrees") {
      @SideOnly(Side.CLIENT)
      public ItemStack getTabIconItem() {
         return TreeRegistry.findSpeciesSloppy(EnumType.OAK.getName()).getSeedStack(1);
      }
   };

   public ModTabs() {
   }
}
