package nightkosh.advanced_fishing.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Tabs {
   public static final CreativeTabs FISH_TAB = new CreativeTabs("tabFish") {
      public ItemStack getIconItemStack() {
         return new ItemStack(Items.FISH, 1, 5);
      }

      @SideOnly(Side.CLIENT)
      public ItemStack getTabIconItem() {
         return new ItemStack(Items.FISH, 1, 5);
      }
   };

   public Tabs() {
   }
}
