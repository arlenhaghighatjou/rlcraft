package vazkii.quark.base.handler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.arl.util.ItemNBTHelper;

public class ProxiedItemStackHandler implements IItemHandler, IItemHandlerModifiable, ICapabilityProvider {
   protected final ItemStack stack;
   protected final String key;
   protected final int size;

   public ProxiedItemStackHandler(ItemStack stack) {
      this(stack, "Inventory", 1);
   }

   public ProxiedItemStackHandler(ItemStack stack, String key) {
      this(stack, key, 1);
   }

   public ProxiedItemStackHandler(ItemStack stack, int size) {
      this(stack, "Inventory", size);
   }

   public ProxiedItemStackHandler(ItemStack stack, String key, int size) {
      this.stack = stack;
      this.key = key;
      this.size = size;
   }

   private NBTTagList getStackList() {
      NBTTagList list = ItemNBTHelper.getList(this.stack, this.key, 10, true);
      if (list == null) {
         ItemNBTHelper.setList(this.stack, this.key, list = new NBTTagList());
      }

      while(list.tagCount() < this.size) {
         list.appendTag(new NBTTagCompound());
      }

      return list;
   }

   private void writeStack(int index, @Nonnull ItemStack stack) {
      this.getStackList().set(index, stack.serializeNBT());
      this.onContentsChanged(index);
   }

   private ItemStack readStack(int index) {
      return new ItemStack(this.getStackList().getCompoundTagAt(index));
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.validateSlotIndex(slot);
      this.writeStack(slot, stack);
      this.onContentsChanged(slot);
   }

   public int getSlots() {
      return this.size;
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      this.validateSlotIndex(slot);
      return this.readStack(slot);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.validateSlotIndex(slot);
         ItemStack existing = this.readStack(slot);
         int limit = this.getStackLimit(slot, stack);
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
               this.writeStack(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }

            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
         }
      }
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (amount == 0) {
         return ItemStack.EMPTY;
      } else {
         this.validateSlotIndex(slot);
         ItemStack existing = this.readStack(slot);
         if (existing.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            int toExtract = Math.min(amount, existing.getMaxStackSize());
            if (existing.getCount() <= toExtract) {
               if (!simulate) {
                  this.writeStack(slot, ItemStack.EMPTY);
               }

               return existing;
            } else {
               if (!simulate) {
                  this.writeStack(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
               }

               return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
         }
      }
   }

   public int getSlotLimit(int slot) {
      return 64;
   }

   protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
      return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
   }

   public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      return true;
   }

   protected void validateSlotIndex(int slot) {
      if (slot < 0 || slot >= this.size) {
         throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.size + ")");
      }
   }

   protected void onContentsChanged(int slot) {
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this) : null);
   }
}
