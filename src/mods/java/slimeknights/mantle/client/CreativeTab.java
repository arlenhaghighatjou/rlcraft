package slimeknights.mantle.client;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTab extends CreativeTabs {
   private ItemStack icon;

   public CreativeTab(String label, ItemStack backupIcon) {
      super(label);
      this.icon = backupIcon;
   }

   public void setDisplayIcon(ItemStack displayIcon) {
      if (!displayIcon.isEmpty()) {
         this.icon = displayIcon;
      }

   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public ItemStack getIconItemStack() {
      return this.icon;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public ItemStack getTabIconItem() {
      return this.icon;
   }
}
