package reborncore.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import reborncore.client.gui.slots.BaseSlot;
import reborncore.client.gui.slots.SlotInput;
import reborncore.client.gui.slots.SlotOutput;
import reborncore.common.container.RebornContainer;

public class InventoryItemHandler implements IItemHandler {
   private final EnumFacing facing;
   private Map<EnumFacing, List<BaseSlot>> slotMap = new HashMap();
   private int slotLimit = 64;

   public InventoryItemHandler(RebornContainer container, EnumFacing facing) {
      this.facing = facing;

      for(EnumFacing _facing : EnumFacing.VALUES) {
         List<BaseSlot> slotList = new ArrayList();

         for(Entry<Integer, BaseSlot> entry : container.slotMap.entrySet()) {
            BaseSlot baseSlot = (BaseSlot)entry.getValue();
            if (_facing == EnumFacing.UP && baseSlot instanceof SlotInput) {
               slotList.add(baseSlot);
            } else if (_facing == EnumFacing.DOWN && baseSlot instanceof SlotOutput) {
               slotList.add(baseSlot);
            } else {
               slotList.add(baseSlot);
            }
         }

         this.slotMap.put(_facing, slotList);
      }

   }

   public int getSlots() {
      return ((List)this.slotMap.get(this.facing)).size();
   }

   public ItemStack getStackInSlot(int slotIndex) {
      return ((BaseSlot)((List)this.slotMap.get(this.facing)).get(slotIndex)).getStack();
   }

   public ItemStack insertItem(int slotIndex, ItemStack stack, boolean simulate) {
      if (!stack.isEmpty() && stack.getCount() != 0) {
         Slot slot = (Slot)((List)this.slotMap.get(this.facing)).get(slotIndex);
         if (!slot.getHasStack()) {
            slot.putStack(stack);
            return ItemStack.EMPTY;
         } else {
            ItemStack existing = slot.getStack();
            int limit = slot.getSlotStackLimit();
            if (!existing.isEmpty()) {
               if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                  return stack;
               }

               limit -= existing.getCount();
            }

            if (limit <= 0) {
               return stack;
            } else {
               boolean reachedLimit = stack.getCount() > limit;
               if (!simulate) {
                  if (existing.isEmpty()) {
                     slot.putStack(reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                  } else {
                     existing.setCount(reachedLimit ? limit : stack.getCount());
                  }
               }

               return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
            }
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack extractItem(int slotIndex, int amount, boolean simulate) {
      if (amount == 0) {
         return ItemStack.EMPTY;
      } else {
         Slot slot = (Slot)((List)this.slotMap.get(this.facing)).get(slotIndex);
         if (slot.getStack().isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            ItemStack existing = slot.getStack();
            if (existing.isEmpty()) {
               return ItemStack.EMPTY;
            } else {
               int toExtract = Math.min(amount, existing.getMaxStackSize());
               if (existing.getCount() <= toExtract) {
                  if (!simulate) {
                     slot.putStack(ItemStack.EMPTY);
                  }

                  return existing;
               } else {
                  if (!simulate) {
                     slot.putStack(ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                  }

                  return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
               }
            }
         }
      }
   }

   public int getSlotLimit(int slot) {
      return this.slotLimit;
   }
}
