package reborncore.common.items;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.Validate;

public class InventoryItem implements IItemHandler, IItemHandlerModifiable, ICapabilityProvider {
   @Nonnull
   ItemStack stack;
   int size;

   private InventoryItem(@Nonnull ItemStack stack, int size) {
      Validate.notNull(stack);
      Validate.isTrue(!stack.isEmpty());
      this.size = size;
      this.stack = stack;
   }

   public static InventoryItem getItemInvetory(ItemStack stack, int size) {
      return new InventoryItem(stack, size);
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public NBTTagCompound getInvData() {
      Validate.isTrue(!this.stack.isEmpty());
      if (!this.stack.hasTagCompound()) {
         this.stack.setTagCompound(new NBTTagCompound());
      }

      if (!this.stack.getTagCompound().hasKey("inventory")) {
         this.stack.getTagCompound().setTag("inventory", new NBTTagCompound());
      }

      return this.stack.getTagCompound().getCompoundTag("inventory");
   }

   public NBTTagCompound getSlotData(int slot) {
      this.validateSlotIndex(slot);
      NBTTagCompound invData = this.getInvData();
      if (!invData.hasKey("slot_" + slot)) {
         invData.setTag("slot_" + slot, new NBTTagCompound());
      }

      return invData.getCompoundTag("slot_" + slot);
   }

   public void setSlotData(int slot, NBTTagCompound tagCompound) {
      this.validateSlotIndex(slot);
      Validate.notNull(tagCompound);
      NBTTagCompound invData = this.getInvData();
      invData.setTag("slot_" + slot, tagCompound);
   }

   public List<ItemStack> getAllStacks() {
      return (List)IntStream.range(0, this.size).mapToObj(this::getStackInSlot).collect(Collectors.toList());
   }

   public int getSlots() {
      return this.size;
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      return new ItemStack(this.getSlotData(slot));
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.setSlotData(slot, stack.writeToNBT(new NBTTagCompound()));
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.validateSlotIndex(slot);
         ItemStack existing = this.getStackInSlot(slot);
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
               if (existing.isEmpty()) {
                  this.setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
               } else {
                  existing.grow(reachedLimit ? limit : stack.getCount());
               }
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
         ItemStack existing = this.getStackInSlot(slot);
         if (existing.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            int toExtract = Math.min(amount, existing.getMaxStackSize());
            if (existing.getCount() <= toExtract) {
               if (!simulate) {
                  this.setStackInSlot(slot, ItemStack.EMPTY);
               }

               return existing;
            } else {
               if (!simulate) {
                  this.setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
               }

               return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
         }
      }
   }

   public int getSlotLimit(int slot) {
      return 64;
   }

   public void validateSlotIndex(int slot) {
      if (slot < 0 || slot >= this.size) {
         throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.size + ")");
      }
   }

   public int getStackLimit(int slot, @Nonnull ItemStack stack) {
      return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this) : null);
   }
}
