package prospector.traverse.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import prospector.traverse.util.TUtils;

public class TraverseTab {
   public static final CreativeTabs TAB = new CreativeTabs("traverse") {
      @SideOnly(Side.CLIENT)
      public ItemStack getTabIconItem() {
         return new ItemStack(TUtils.getBlock("fir_sapling"));
      }
   };

   public TraverseTab() {
   }
}
