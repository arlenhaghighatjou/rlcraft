package noppes.vc;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VCTabs {
   public static final CreativeTabs BLOCKS = new CreativeTabs("vcblocks") {
      @SideOnly(Side.CLIENT)
      public ItemStack getTabIconItem() {
         return new ItemStack(VCBlocks.couch_wool, 1, 1);
      }
   };
   public static final CreativeTabs ITEMS = new CreativeTabs("vcitems") {
      @SideOnly(Side.CLIENT)
      public ItemStack getTabIconItem() {
         return new ItemStack(VCItems.coin_gold);
      }
   };
   public static final CreativeTabs WEAPONS = new CreativeTabs("vcweapons") {
      @SideOnly(Side.CLIENT)
      public ItemStack getTabIconItem() {
         return new ItemStack(VCWeapons.shuriken);
      }
   };
   public static final CreativeTabs ARMORS = new CreativeTabs("vcarmors") {
      @SideOnly(Side.CLIENT)
      public ItemStack getTabIconItem() {
         return new ItemStack(VCArmors.cow_leather_head);
      }
   };

   public VCTabs() {
   }
}
