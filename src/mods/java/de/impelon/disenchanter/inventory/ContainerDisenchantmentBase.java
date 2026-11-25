package de.impelon.disenchanter.inventory;

import de.impelon.disenchanter.proxy.CommonProxy;
import de.impelon.disenchanter.tileentity.TileEntityDisenchantmentTableAutomatic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

public abstract class ContainerDisenchantmentBase extends Container {
   protected static final int SOURCE_SLOT = 0;
   protected static final int RECEIVER_SLOT = 1;
   protected static final int OUTPUT_SLOT = 2;
   protected static final int FIRST_NON_TABLE_SLOT = 3;
   protected World world;
   protected BlockPos position;

   public static ContainerDisenchantmentBase create(InventoryPlayer playerInventory, World world, BlockPos position) {
      TileEntity te = world.getTileEntity(position);
      return (ContainerDisenchantmentBase)(te instanceof TileEntityDisenchantmentTableAutomatic ? new ContainerDisenchantmentAutomatic(playerInventory, world, position) : new ContainerDisenchantmentManual(playerInventory, world, position));
   }

   public ContainerDisenchantmentBase(InventoryPlayer playerInventory, World world, BlockPos position) {
      this.world = world;
      this.position = position;
      this.addTableSlots();
      this.addPlayerSlots(playerInventory);
   }

   protected abstract IDisenchantmentItemHandler getTableInventory();

   protected void addTableSlots() {
      this.addSlotToContainer(new SlotItemHandler(this.getTableInventory(), 0, 26, 35));
      this.addSlotToContainer(new SlotItemHandler(this.getTableInventory(), 1, 75, 35));
      this.addSlotToContainer(new SlotItemHandler(this.getTableInventory(), 2, 133, 35) {
         public boolean isItemValid(ItemStack stack) {
            return false;
         }
      });
   }

   protected void addPlayerSlots(InventoryPlayer playerInventory) {
      for(int tmp = 0; tmp < 3; ++tmp) {
         for(int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInventory, col + tmp * 9 + 9, 8 + col * 18, 84 + tmp * 18));
         }
      }

      for(int var4 = 0; var4 < 9; ++var4) {
         this.addSlotToContainer(new Slot(playerInventory, var4, 8 + var4 * 18, 142));
      }

   }

   public boolean canInteractWith(EntityPlayer p) {
      return this.world.getBlockState(this.position).getBlock().equals(CommonProxy.disenchantmentTable) && p.getDistanceSq(this.position.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
   }

   public ItemStack transferStackInSlot(EntityPlayer p, int slotID) {
      ItemStack itemstackPrev = ItemStack.EMPTY;
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(slotID);
      if (slot != null && slot.getHasStack()) {
         itemstack = slot.getStack();
         itemstackPrev = itemstack.copy();
         switch(slotID) {
         case 0:
         case 1:
            if (!this.mergeItemStack(itemstack, 3, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
            break;
         case 2:
            if (!this.mergeItemStack(itemstack, 3, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
            break;
         default:
            ItemStack i = InventoryUtils.copyStackWithSize(itemstack, 1);
            int slockToCheck = this.getTableInventory().isItemValid(1, i) ? 1 : 0;
            if (((Slot)this.inventorySlots.get(slockToCheck)).getHasStack() || !this.mergeItemStack(i, slockToCheck, slockToCheck + 1, false)) {
               return ItemStack.EMPTY;
            }

            itemstack.shrink(1);
         }

         if (itemstack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack.getCount() == itemstackPrev.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p, itemstack);
      }

      return itemstack;
   }
}
