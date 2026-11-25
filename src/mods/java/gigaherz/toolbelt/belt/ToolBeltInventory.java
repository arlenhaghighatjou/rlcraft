package gigaherz.toolbelt.belt;

import gigaherz.toolbelt.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class ToolBeltInventory implements IItemHandlerModifiable {
   private final ItemStack itemStack;

   ToolBeltInventory(ItemStack itemStack) {
      this.itemStack = itemStack;
   }

   private NBTTagCompound getTag() {
      NBTTagCompound tag = this.itemStack.getTagCompound();
      if (tag == null) {
         this.itemStack.setTagCompound(tag = new NBTTagCompound());
      }

      return tag;
   }

   public int getSlots() {
      return MathHelper.clamp(this.getTag().getInteger("Size"), 2, 9);
   }

   public ItemStack getStackInSlot(int slot) {
      this.validateSlotIndex(slot);
      NBTTagList tagList = this.getTag().getTagList("Items", 10);

      for(int i = 0; i < tagList.tagCount(); ++i) {
         NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
         if (itemTags.getInteger("Slot") == slot) {
            return new ItemStack(itemTags);
         }
      }

      return ItemStack.EMPTY;
   }

   public void setStackInSlot(int slot, ItemStack stack) {
      this.validateSlotIndex(slot);
      NBTTagCompound itemTag = null;
      boolean hasStack = stack.getCount() > 0;
      if (hasStack) {
         itemTag = new NBTTagCompound();
         itemTag.setInteger("Slot", slot);
         stack.writeToNBT(itemTag);
      }

      NBTTagList tagList = this.getTag().getTagList("Items", 10);

      for(int i = 0; i < tagList.tagCount(); ++i) {
         NBTTagCompound existing = tagList.getCompoundTagAt(i);
         if (existing.getInteger("Slot") == slot) {
            if (hasStack) {
               tagList.set(i, itemTag);
            } else {
               tagList.removeTag(i);
            }

            return;
         }
      }

      if (hasStack) {
         tagList.appendTag(itemTag);
      }

      this.getTag().setTag("Items", tagList);
   }

   public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
      if (!this.canInsertItem(slot, stack)) {
         return stack;
      } else if (stack.getCount() <= 0) {
         return ItemStack.EMPTY;
      } else {
         this.validateSlotIndex(slot);
         ItemStack existing = this.getStackInSlot(slot);
         int limit = stack.getMaxStackSize();
         if (existing.getCount() > 0) {
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
               if (existing.getCount() <= 0) {
                  existing = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
               } else {
                  existing.grow(reachedLimit ? limit : stack.getCount());
               }

               this.setStackInSlot(slot, existing);
            }

            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
         }
      }
   }

   protected boolean canInsertItem(int slot, ItemStack stack) {
      return Config.isItemStackAllowed(stack);
   }

   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (amount == 0) {
         return ItemStack.EMPTY;
      } else {
         this.validateSlotIndex(slot);
         ItemStack existing = this.getStackInSlot(slot);
         if (existing.getCount() <= 0) {
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

   private void validateSlotIndex(int slot) {
      if (slot < 0 || slot >= this.getSlots()) {
         throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.getSlots() + ")");
      }
   }
}
