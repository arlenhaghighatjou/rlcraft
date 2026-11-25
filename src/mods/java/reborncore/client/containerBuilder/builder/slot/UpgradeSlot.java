package reborncore.client.containerBuilder.builder.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.client.containerBuilder.IRightClickHandler;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.common.util.Inventory;

public class UpgradeSlot extends Slot implements IRightClickHandler {
   public UpgradeSlot(IInventory inventory, int index, int xPosition, int yPosition) {
      super(inventory, index, xPosition, yPosition);
   }

   public boolean isItemValid(ItemStack stack) {
      if (!(stack.getItem() instanceof IUpgrade)) {
         return false;
      } else {
         IUpgrade upgrade = (IUpgrade)stack.getItem();
         IUpgradeable upgradeable = null;
         Inventory inv = (Inventory)this.inventory;
         TileEntity tileEntity = inv.getTileBase();
         if (tileEntity instanceof IUpgradeable) {
            upgradeable = (IUpgradeable)tileEntity;
         }

         return upgrade.isValidForInventory(upgradeable, stack) && (upgradeable == null || upgradeable.isUpgradeValid(upgrade, stack));
      }
   }

   public int getSlotStackLimit() {
      return 1;
   }

   public boolean handleRightClick(int slotID, EntityPlayer player, BuiltContainer container) {
      if (this.inventory instanceof Inventory) {
         Inventory inv = (Inventory)this.inventory;
         TileEntity tileEntity = inv.getTileBase();
         if (tileEntity instanceof IUpgradeable) {
            IUpgradeable upgradeable = (IUpgradeable)tileEntity;
            if (upgradeable.canBeUpgraded()) {
               ItemStack stack = upgradeable.getUpgradeInventory().getStackInSlot(slotID);
               if (!stack.isEmpty() && stack.getItem() instanceof IUpgrade && player.world.isRemote) {
                  ((IUpgrade)stack.getItem()).handleRightClick(tileEntity, stack, container, slotID);
               }
            }
         }
      }

      return true;
   }
}
