package cursedflames.bountifulbaubles.block;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.container.SlotBauble;
import cursedflames.bountifulbaubles.util.GenericSlot;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerReforger extends GenericContainer {
   EntityPlayer player;

   public ContainerReforger(IInventory playerInventory, EntityPlayer player, GenericTileEntity te) {
      super(playerInventory, te, false);
      this.player = player;
      this.addOwnSlots();
      this.addPlayerSlots(playerInventory);
   }

   protected void addOwnSlots() {
      IItemHandler itemHandler = (IItemHandler)this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      int x = 44;
      int y = 44;
      this.addSlotToContainer(new ContainerReforger.SlotReforger(itemHandler, 0, x, y));
   }

   protected void addPlayerSlots(IInventory playerInventory) {
      for(int row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            int x = 8 + col * 18;
            int y = row * 18 + 80;
            this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
         }
      }

      for(int row = 0; row < 9; ++row) {
         int x = 8 + row * 18;
         int y = 138;
         this.addSlotToContainer(new Slot(playerInventory, row, x, y));
      }

      this.addSlotToContainer(new Slot(playerInventory, 40, 152, 62));
      IBaublesItemHandler baublesInventory = BaublesApi.getBaublesHandler(this.player);

      for(int row = 0; row < 2; ++row) {
         for(int col = 0; col < 4 && row * 4 + col < baublesInventory.getSlots(); ++col) {
            int x = 8 + (col + 5) * 18;
            int y = row * 18 + 44;
            this.addSlotToContainer(new SlotBauble(this.player, baublesInventory, col + row * 4, x, y));
         }
      }

      EntityEquipmentSlot[] armorSlots = new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD};

      for(int row = 3; row >= 0; --row) {
         int x = 8 + (row + 5) * 18;
         int y = 26;
         EntityEquipmentSlot armorSlot = armorSlots[row];
         this.addSlotToContainer(new GenericSlot(playerInventory, 36 + row, x, y, (stack) -> {
            return !stack.isEmpty() && stack.getItem().isValidArmor(stack, armorSlot, this.player);
         }, 1));
      }

   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.te.canInteractWith(playerIn);
   }

   static class SlotReforger extends SlotItemHandler {
      public SlotReforger(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
         super(inventoryIn, index, xPosition, yPosition);
      }

      public boolean isItemValid(ItemStack stack) {
         return stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null) && super.isItemValid(stack);
      }
   }
}
