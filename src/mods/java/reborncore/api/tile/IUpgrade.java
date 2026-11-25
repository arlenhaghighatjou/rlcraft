package reborncore.api.tile;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.recipes.IUpgradeHandler;

public interface IUpgrade {
   void process(IUpgradeHandler var1, ItemStack var2);

   @SideOnly(Side.CLIENT)
   void handleRightClick(TileEntity var1, ItemStack var2, Container var3, int var4);

   default boolean isValidForInventory(IUpgradeable upgradeable, ItemStack stack) {
      return true;
   }
}
