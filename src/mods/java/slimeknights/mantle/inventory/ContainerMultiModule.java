package slimeknights.mantle.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

public class ContainerMultiModule<T extends TileEntity & IInventory> extends BaseContainer<T> {
   public List<Container> subContainers = Lists.newArrayList();
   protected Map<Integer, Container> slotContainerMap = Maps.newHashMap();
   protected Map<Container, Pair<Integer, Integer>> subContainerSlotRanges = Maps.newHashMap();
   protected int subContainerSlotStart = -1;
   protected Set<Container> shiftClickContainers = Sets.newHashSet();

   public ContainerMultiModule(T tile) {
      super(tile);
   }

   public void addSubContainer(Container subcontainer, boolean preferForShiftClick) {
      if (this.subContainers.isEmpty()) {
         this.subContainerSlotStart = this.inventorySlots.size();
      }

      this.subContainers.add(subcontainer);
      if (preferForShiftClick) {
         this.shiftClickContainers.add(subcontainer);
      }

      int begin = this.inventorySlots.size();

      for(Object slot : subcontainer.inventorySlots) {
         SlotWrapper wrapper = new SlotWrapper((Slot)slot);
         this.addSlotToContainer(wrapper);
         this.slotContainerMap.put(Integer.valueOf(wrapper.slotNumber), subcontainer);
      }

      int end = this.inventorySlots.size();
      this.subContainerSlotRanges.put(subcontainer, Pair.of(Integer.valueOf(begin), Integer.valueOf(end)));
   }

   public <TC extends Container> TC getSubContainer(Class<TC> clazz) {
      return (TC)this.getSubContainer(clazz, 0);
   }

   public <TC extends Container> TC getSubContainer(Class<TC> clazz, int index) {
      for(Container sub : this.subContainers) {
         if (clazz.isAssignableFrom(sub.getClass())) {
            --index;
         }

         if (index < 0) {
            return (TC)sub;
         }
      }

      return (TC)null;
   }

   public Container getSlotContainer(int slotNumber) {
      return (Container)(this.slotContainerMap.containsKey(Integer.valueOf(slotNumber)) ? (Container)this.slotContainerMap.get(Integer.valueOf(slotNumber)) : this);
   }

   public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
      for(Container sub : this.subContainers) {
         if (!sub.canInteractWith(playerIn)) {
            return false;
         }
      }

      return super.canInteractWith(playerIn);
   }

   public void onContainerClosed(EntityPlayer playerIn) {
      for(Container sub : this.subContainers) {
         sub.onContainerClosed(playerIn);
      }

      super.onContainerClosed(playerIn);
   }

   @Nonnull
   public ItemStack slotClick(int slotId, int dragType, ClickType type, EntityPlayer player) {
      if (slotId == -999 && type == ClickType.QUICK_CRAFT) {
         for(Container container : this.subContainers) {
            container.slotClick(slotId, dragType, type, player);
         }
      }

      return super.slotClick(slotId, dragType, type, player);
   }

   @Nonnull
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack ret = slot.getStack().copy();
         ItemStack itemstack = slot.getStack().copy();
         Container container = this.getSlotContainer(index);
         boolean nothingDone = true;
         if (container != this) {
            nothingDone = nothingDone & this.moveToTileInventory(itemstack);
            nothingDone = nothingDone & this.moveToPlayerInventory(itemstack);
         } else if (index >= this.subContainerSlotStart && (index >= this.playerInventoryStart || this.subContainerSlotStart >= 0)) {
            if (index < this.playerInventoryStart || this.playerInventoryStart < 0) {
               return ItemStack.EMPTY;
            }

            nothingDone = nothingDone & this.moveToTileInventory(itemstack);
            nothingDone = nothingDone & this.moveToAnyContainer(itemstack, this.subContainers);
         } else {
            nothingDone = nothingDone & this.refillAnyContainer(itemstack, this.subContainers);
            nothingDone = nothingDone & this.moveToPlayerInventory(itemstack);
            nothingDone = nothingDone & this.moveToAnyContainer(itemstack, this.subContainers);
         }

         if (nothingDone) {
            return ItemStack.EMPTY;
         } else {
            return this.notifySlotAfterTransfer(playerIn, itemstack, ret, slot);
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Nonnull
   protected ItemStack notifySlotAfterTransfer(EntityPlayer player, @Nonnull ItemStack stack, @Nonnull ItemStack original, Slot slot) {
      slot.onSlotChange(stack, original);
      if (stack.getCount() == original.getCount()) {
         return ItemStack.EMPTY;
      } else {
         slot.putStack(stack);
         slot.onTake(player, stack);
         if (slot.getHasStack() && slot.getStack().isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         }

         return original;
      }
   }

   protected boolean moveToTileInventory(@Nonnull ItemStack itemstack) {
      if (itemstack.isEmpty()) {
         return false;
      } else {
         int end = this.subContainerSlotStart;
         if (end < 0) {
            end = this.playerInventoryStart;
         }

         return !this.mergeItemStack(itemstack, 0, end, false);
      }
   }

   protected boolean moveToPlayerInventory(@Nonnull ItemStack itemstack) {
      if (itemstack.isEmpty()) {
         return false;
      } else {
         return this.playerInventoryStart > 0 && !this.mergeItemStack(itemstack, this.playerInventoryStart, this.inventorySlots.size(), true);
      }
   }

   protected boolean moveToAnyContainer(@Nonnull ItemStack itemstack, Collection<Container> containers) {
      if (itemstack.isEmpty()) {
         return false;
      } else {
         for(Container submodule : containers) {
            if (this.moveToContainer(itemstack, submodule)) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean moveToContainer(@Nonnull ItemStack itemstack, Container container) {
      Pair<Integer, Integer> range = (Pair)this.subContainerSlotRanges.get(container);
      return !this.mergeItemStack(itemstack, ((Integer)range.getLeft()).intValue(), ((Integer)range.getRight()).intValue(), false);
   }

   protected boolean refillAnyContainer(@Nonnull ItemStack itemstack, Collection<Container> containers) {
      if (itemstack.isEmpty()) {
         return false;
      } else {
         for(Container submodule : containers) {
            if (this.refillContainer(itemstack, submodule)) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean refillContainer(@Nonnull ItemStack itemstack, Container container) {
      Pair<Integer, Integer> range = (Pair)this.subContainerSlotRanges.get(container);
      return !this.mergeItemStackRefill(itemstack, ((Integer)range.getLeft()).intValue(), ((Integer)range.getRight()).intValue(), false);
   }

   public <TE extends TileEntity> TE detectTE(Class<TE> clazz) {
      return (TE)(ObjectUtils.firstNonNull(new TileEntity[]{this.detectChest(this.pos.north(), clazz), this.detectChest(this.pos.east(), clazz), this.detectChest(this.pos.south(), clazz), this.detectChest(this.pos.west(), clazz)}));
   }

   private <TE extends TileEntity> TE detectChest(BlockPos pos, Class<TE> clazz) {
      TileEntity te = this.world.getTileEntity(pos);
      return (TE)(te != null && clazz.isAssignableFrom(te.getClass()) ? te : null);
   }
}
