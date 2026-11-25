package svenhjol.charm.crafting.container;

import invtweaks.api.container.ChestContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.crafting.tile.TileCrate;
import svenhjol.meson.MesonContainer;

@ChestContainer
public class ContainerCrate extends MesonContainer {
   protected final TileCrate crate;

   public ContainerCrate(InventoryPlayer playerInv, final TileCrate crate) {
      super(playerInv, crate);
      this.crate = crate;
      IItemHandler inventory = (IItemHandler)crate.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      if (inventory != null) {
         int x = 8;
         int y = 35;
         int slotIndex = 0;

         for(int i = 0; i < inventory.getSlots(); ++i) {
            this.addSlotToContainer(new SlotItemHandler(inventory, slotIndex, x, y) {
               public void onSlotChanged() {
                  crate.markDirty();
               }
            });
            ++slotIndex;
            x += 18;
         }

         for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
               this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
         }

         for(int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
         }

      }
   }

   public void onContainerClosed(EntityPlayer player) {
      super.onContainerClosed(player);
      player.playSound(CharmSounds.WOOD_CLOSE, 1.0F, 1.0F);
   }

   public boolean canInteractWith(EntityPlayer player) {
      return !this.crate.isInvalid();
   }
}
