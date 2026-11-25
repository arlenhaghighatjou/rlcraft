package reborncore.api.tile;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public interface IUpgradeable {
   default boolean canBeUpgraded() {
      return true;
   }

   ISidedInventory getUpgradeInventory();

   /** @deprecated */
   @Deprecated
   default IInventory getUpgradeInvetory() {
      return this.getUpgradeInventory();
   }

   default int getUpgradeSlotCount() {
      return 4;
   }

   default boolean isUpgradeValid(IUpgrade upgrade, ItemStack stack) {
      return true;
   }
}
