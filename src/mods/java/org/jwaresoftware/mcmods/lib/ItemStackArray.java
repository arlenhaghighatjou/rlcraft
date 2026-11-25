package org.jwaresoftware.mcmods.lib;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.items.IItemHandlerModifiable;

public final class ItemStackArray {
   public ItemStackArray() {
   }

   @Nonnull
   public static final ItemStack[] create(int slotCount) {
      ItemStack nullstack = ItemStacks.NULLSTACK();
      ItemStack[] inventoryImpl = new ItemStack[slotCount];

      for(int i = 0; i < inventoryImpl.length; ++i) {
         inventoryImpl[i] = nullstack;
      }

      return inventoryImpl;
   }

   public static final int count(@Nonnull ItemStack[] inventoryImpl) {
      int n = 0;

      for(int i = 0; i < inventoryImpl.length; ++i) {
         if (!ItemStacks.isEmpty(inventoryImpl[i]) && SharedGlue.isDefined(inventoryImpl[i].getItem())) {
            ++n;
         }
      }

      return n;
   }

   public static final void clear(@Nonnull ItemStack[] inventoryImpl) {
      ItemStack nullstack = ItemStacks.NULLSTACK();

      for(int i = 0; i < inventoryImpl.length; ++i) {
         inventoryImpl[i] = nullstack;
      }

   }

   @Nonnull
   public static final ItemStack[] readOrderedFrom(@Nonnull NBTTagCompound compound, @Nonnull String storageId, int size) {
      ItemStack[] inventoryImpl = create(size);
      if (compound.hasKey(storageId)) {
         NBTTagList slotsNbt = compound.getTagList(storageId, 10);

         for(int i = 0; i < slotsNbt.tagCount(); ++i) {
            NBTTagCompound slotNbt = slotsNbt.getCompoundTagAt(i);
            byte slot_index = slotNbt.getByte("Slot");
            if (slot_index >= 0 && slot_index < inventoryImpl.length) {
               inventoryImpl[slot_index] = new ItemStack(slotNbt);
            }
         }
      }

      return inventoryImpl;
   }

   public static final void writeOrderedTo(@Nonnull ItemStack[] inventoryImpl, @Nonnull NBTTagCompound compound, @Nonnull String storageId, String countId) {
      NBTTagList slotsNbt = new NBTTagList();

      for(int i = 0; i < inventoryImpl.length; ++i) {
         if (!ItemStacks.isEmpty(inventoryImpl[i])) {
            NBTTagCompound slotNbt = new NBTTagCompound();
            slotNbt.setByte("Slot", (byte)i);
            inventoryImpl[i].writeToNBT(slotNbt);
            slotsNbt.appendTag(slotNbt);
         }
      }

      compound.setTag(storageId, slotsNbt);
      if (countId != null) {
         compound.setByte(countId, (byte)slotsNbt.tagCount());
      }

   }

   @Nonnull
   public static final List<ItemStack> readFrom(@Nonnull NBTTagCompound compound, @Nonnull String storageId) {
      NBTTagList slotsNbt = compound.getTagList(storageId, 10);
      List<ItemStack> out = new ArrayList(31);

      for(int i = 0; i < slotsNbt.tagCount(); ++i) {
         NBTTagCompound slotNbt = slotsNbt.getCompoundTagAt(i);
         ItemStack slotstack = new ItemStack(slotNbt);
         if (!ItemStacks.isEmpty(slotstack)) {
            out.add(slotstack);
         }
      }

      return out;
   }

   public static final void addContentsInformation(@Nonnull List<ItemStack> contents, @Nonnull List<String> tooltip, boolean advanced, String more) {
      int i = 0;
      if (more == null) {
         more = "...[more]";
      }

      if (!contents.isEmpty()) {
         for(ItemStack item : contents) {
            String displayString = "" + TextFormatting.GRAY + "> " + Strings.boundedUiTipString(item, 2) + TextFormatting.RESET;
            tooltip.add(displayString);
            ++i;
            if (!advanced && i == 10 && contents.size() > 10) {
               int more_num = contents.size() - 10;
               tooltip.add(Strings.translateFormatted(more, more_num));
               break;
            }
         }
      }

   }

   private static boolean can_represent_strictly(ItemStack existing, ItemStack with) {
      if (existing.isEmpty()) {
         return true;
      } else {
         return with.isEmpty() ? false : (existing.getItem() != with.getItem() ? false : (existing.getMetadata() != with.getMetadata() ? false : ItemStack.areItemStackTagsEqual(existing, with)));
      }
   }

   public static final boolean mergeInternal(IItemHandlerModifiable into, @Nonnull ItemStack stack, int[] slots, boolean lossy_allowed) {
      boolean processed_something = false;
      ArrayList<ItemStackSnapshot> slot_snapshots = lossy_allowed ? null : new ArrayList();
      int stack_maxsize = stack.getMaxStackSize();
      if (stack.isStackable()) {
         for(int ii = 0; stack.getCount() > 0 && ii < slots.length; ++ii) {
            int iindex = slots[ii];
            ItemStack slot_stack = into.getStackInSlot(iindex);
            if (!slot_stack.isEmpty() && can_represent_strictly(stack, slot_stack)) {
               int slot_max = Math.min(stack_maxsize, into.getSlotLimit(iindex));
               int n = slot_stack.getCount() + stack.getCount();
               if (n <= slot_max) {
                  if (!lossy_allowed) {
                     slot_snapshots.add(new ItemStackSnapshot(slot_stack, iindex));
                  }

                  stack.setCount(0);
                  slot_stack.setCount(n);
                  processed_something = true;
               } else if (slot_stack.getCount() < slot_max) {
                  if (!lossy_allowed) {
                     slot_snapshots.add(new ItemStackSnapshot(slot_stack, iindex));
                  }

                  stack.shrink(slot_max - slot_stack.getCount());
                  slot_stack.setCount(slot_max);
                  processed_something = true;
               }
            }
         }
      }

      if (!stack.isEmpty()) {
         for(int ii = 0; ii < slots.length; ++ii) {
            int iindex = slots[ii];
            ItemStack slot_stack = into.getStackInSlot(iindex);
            if (slot_stack.isEmpty()) {
               int slot_max = Math.min(stack_maxsize, into.getSlotLimit(iindex));
               ItemStack copy = stack.copy();
               if (copy.getCount() > slot_max) {
                  copy.setCount(slot_max);
                  stack.shrink(slot_max);
               } else {
                  stack.setCount(0);
               }

               if (!lossy_allowed) {
                  slot_snapshots.add(new ItemStackSnapshot(slot_stack, iindex));
               }

               into.setStackInSlot(iindex, copy);
               processed_something = true;
               break;
            }
         }
      }

      if (processed_something && !lossy_allowed && !stack.isEmpty()) {
         for(ItemStackSnapshot snapshot : slot_snapshots) {
            into.setStackInSlot(snapshot.iindex, snapshot.savedstack);
            snapshot.savedstack = null;
         }

         processed_something = false;
      }

      return processed_something;
   }
}
