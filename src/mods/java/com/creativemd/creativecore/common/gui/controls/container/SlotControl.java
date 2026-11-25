package com.creativemd.creativecore.common.gui.controls.container;

import com.creativemd.creativecore.common.gui.ContainerControl;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.controls.container.client.GuiSlotControl;
import com.creativemd.creativecore.common.gui.event.container.SlotChangeEvent;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotControl extends ContainerControl {
   public Slot slot;
   public ItemStack lastSended = ItemStack.EMPTY;

   public SlotControl(Slot slot) {
      super(slot.inventory.getName() + slot.getSlotIndex());
      this.slot = slot;
      this.lastSended = slot.getStack().copy();
   }

   @SideOnly(Side.CLIENT)
   public GuiControl createGuiControl() {
      GuiControl control = new GuiSlotControl(this.slot.xPos, this.slot.yPos, this);
      control.enabled = this.enabled;
      return control;
   }

   public void onTick() {
      if (!this.isRemote() && !ItemStack.areItemStacksEqual(this.lastSended, this.slot.getStack())) {
         this.sendUpdate();
         this.raiseEvent(new SlotChangeEvent(this));
      }

   }

   public int getItemStackLimit(ItemStack stack) {
      return stack.getMaxStackSize();
   }

   public int getStackLimit(Slot slot, ItemStack stack) {
      return Math.min(slot.getSlotStackLimit(), this.getItemStackLimit(stack));
   }

   public void writeToNBTUpdate(NBTTagCompound nbt) {
      ItemStack stack = this.slot.getStack();
      if (this.slot.getHasStack()) {
         stack.writeToNBT(nbt);
         if (stack.getCount() > 127) {
            nbt.setInteger("realCount", stack.getCount());
         }

         this.lastSended = stack.copy();
      } else {
         this.lastSended = ItemStack.EMPTY;
      }

      NBTTagCompound dragSlot = new NBTTagCompound();
      if (!this.getPlayer().inventory.getItemStack().isEmpty()) {
         this.getPlayer().inventory.getItemStack().writeToNBT(dragSlot);
      }

      nbt.setTag("drag", dragSlot);
   }

   public void onPacketReceive(NBTTagCompound nbt) {
      switch(nbt.getInteger("type")) {
      case 0:
         if (!this.isRemote()) {
            System.out.println("Client is sending stuff!");
         }

         ItemStack stack = ItemStack.EMPTY;
         if (nbt.hasKey("id")) {
            stack = new ItemStack(nbt);
            if (nbt.hasKey("realCount")) {
               stack.setCount(nbt.getInteger("realCount"));
            }
         }

         this.slot.putStack(stack);
         if (!ItemStack.areItemStacksEqual(this.lastSended, this.slot.getStack())) {
            this.raiseEvent(new SlotChangeEvent(this));
         }

         this.lastSended = stack;
         ItemStack dragStack = ItemStack.EMPTY;
         if (nbt.hasKey("drag")) {
            dragStack = new ItemStack(nbt.getCompoundTag("drag"));
         }

         this.getPlayer().inventory.setItemStack(dragStack);
         break;
      case 1:
         this.onSlotClicked(nbt.getInteger("button"), nbt.getBoolean("shift"), nbt.getInteger("scrolled"));
         break;
      case 2:
         this.dropItem(nbt.getBoolean("ctrl"));
         break;
      case 3:
         this.splitStack(nbt.getIntArray("slots"), this.getPlayer().inventory.getItemStack(), nbt.getBoolean("right"));
      }

   }

   public void dropItem(boolean ctrl) {
      if (this.slot.getHasStack()) {
         ItemStack drop = this.slot.getStack().copy();
         if (ctrl) {
            this.slot.putStack(ItemStack.EMPTY);
         } else {
            drop.setCount(1);
            ItemStack newStack = this.slot.getStack();
            newStack.shrink(1);
         }

         this.getPlayer().dropItem(drop, true);
         this.slot.onTake(this.getPlayer(), drop);
      }

   }

   public ItemStack putItemInSlot(Slot slot, ItemStack stack) {
      boolean canStack = Container.canAddItemToSlot(slot, stack, true);
      int availableStackSize = this.getStackLimit(slot, stack);
      if (canStack && slot.isItemValid(stack)) {
         int neededStackSize = stack.getCount();
         if (slot.getHasStack()) {
            availableStackSize -= slot.getStack().getCount();
         }

         int additionalStackSize = Math.min(availableStackSize, neededStackSize);
         if (additionalStackSize > 0) {
            ItemStack inSlot = stack.copy();
            inSlot.setCount(additionalStackSize);
            if (slot.getHasStack()) {
               inSlot.grow(slot.getStack().getCount());
            }

            slot.putStack(inSlot);
            stack.shrink(additionalStackSize);
         }
      }

      return stack;
   }

   public void switchItems() {
      ItemStack hand = this.getPlayer().inventory.getItemStack();
      InventoryPlayer inventoryplayer = this.getPlayer().inventory;
      ItemStack slotItem = this.slot.getStack();
      if (this.slot.isItemValid(hand)) {
         boolean canStack = Container.canAddItemToSlot(this.slot, hand, true);
         int stackLimit = this.getStackLimit(this.slot, hand);
         if (canStack) {
            int neededStackSize = hand.getCount();
            if (this.slot.getHasStack()) {
               stackLimit -= slotItem.getCount();
            }

            int usedStackSize = Math.min(stackLimit, neededStackSize);
            ItemStack inSlot = hand.copy();
            inSlot.setCount(usedStackSize);
            if (this.slot.getHasStack()) {
               inSlot.grow(slotItem.getCount());
            }

            this.slot.putStack(inSlot);
            int left = Math.max(0, neededStackSize - usedStackSize);
            hand.setCount(left);
         } else if (this.slot.canTakeStack(this.getPlayer()) && hand.getCount() <= stackLimit && slotItem.getCount() <= this.getItemStackLimit(slotItem)) {
            this.slot.putStack(hand);
            inventoryplayer.setItemStack(slotItem);
         }
      }

   }

   public boolean canMergeIntoInventory(Slot mergeSlot) {
      return mergeSlot.inventory != this.slot.inventory;
   }

   public void mergeToOtherInventory(ItemStack toTransfer, boolean useEmptySlot) {
      for(int i = 0; i < this.getParent().controls.size(); ++i) {
         if (this.getParent().controls.get(i) instanceof SlotControl) {
            Slot mergeSlot = ((SlotControl)this.getParent().controls.get(i)).slot;
            if (this.canMergeIntoInventory(mergeSlot) && (useEmptySlot || mergeSlot.getHasStack())) {
               toTransfer = ((SlotControl)this.getParent().controls.get(i)).putItemInSlot(mergeSlot, toTransfer);
               if (toTransfer.isEmpty()) {
                  return;
               }
            }
         }
      }

   }

   public void transferIntoOtherInventory(boolean reverse, int amount) {
      if (this.slot.getHasStack()) {
         ItemStack stack = this.slot.getStack();
         if (reverse) {
            int stackSizeLeft = Math.min(amount, stack.getCount());

            for(int i = 0; i < this.getParent().controls.size(); ++i) {
               if (this.getParent().controls.get(i) instanceof SlotControl) {
                  Slot mergeSlot = ((SlotControl)this.getParent().controls.get(i)).slot;
                  if (mergeSlot.inventory != this.slot.inventory) {
                     boolean canStack = Container.canAddItemToSlot(mergeSlot, stack, true);
                     if (canStack && mergeSlot.getHasStack() && !mergeSlot.getStack().isEmpty()) {
                        int transfer = Math.min(stackSizeLeft, ((SlotControl)this.getParent().controls.get(i)).getStackLimit(this.slot, stack));
                        stackSizeLeft -= transfer;
                        stack.grow(transfer);
                        mergeSlot.getStack().shrink(transfer);
                        if (stackSizeLeft <= 0) {
                           return;
                        }
                     }
                  }
               }
            }
         } else {
            this.transferIntoOtherInventory(amount);
         }
      }

   }

   public void transferIntoOtherInventory(int amount) {
      ItemStack stack = this.slot.getStack();
      if (amount > stack.getCount()) {
         amount = stack.getCount();
      }

      ItemStack copy = stack.copy();
      copy.setCount(amount);
      int minAmount = stack.getCount() - amount;
      this.mergeToOtherInventory(copy, false);
      if (!copy.isEmpty()) {
         this.mergeToOtherInventory(copy, true);
      }

      if (copy.isEmpty()) {
         stack.setCount(minAmount);
      } else {
         stack.shrink(amount - copy.getCount());
      }

   }

   public void takeStack(boolean leftClick, InventoryPlayer inventoryplayer) {
      ItemStack hand = this.getPlayer().inventory.getItemStack();
      ItemStack slotItem = this.slot.getStack();
      if (leftClick) {
         int stackSize = Math.min(Math.min(slotItem.getCount(), slotItem.getMaxStackSize()), slotItem.getCount());
         ItemStack newHand = slotItem.copy();
         newHand.setCount(stackSize);
         inventoryplayer.setItemStack(newHand);
         slotItem.shrink(stackSize);
         this.slot.onTake(this.getPlayer(), newHand);
      } else {
         int amount = (slotItem.getCount() + 1) / 2;
         hand = this.slot.decrStackSize(amount);
         inventoryplayer.setItemStack(hand);
         this.slot.onTake(this.getPlayer(), hand);
      }

   }

   public void onSlotClicked(int mouseButton, boolean shift, int scrolled) {
      ItemStack hand = this.getPlayer().inventory.getItemStack();
      InventoryPlayer inventoryplayer = this.getPlayer().inventory;
      ItemStack slotItem = this.slot.getStack();
      boolean isWheel = false;
      if (mouseButton == 0) {
         if (this.slot.canTakeStack(this.getPlayer())) {
            if (shift) {
               this.transferIntoOtherInventory(false, 64);
            } else if (!hand.isEmpty()) {
               this.switchItems();
            } else if (this.slot.getHasStack()) {
               this.takeStack(true, inventoryplayer);
            }
         }
      } else if (mouseButton == 1) {
         if (!hand.isEmpty()) {
            if (this.slot.isItemValid(hand)) {
               boolean canStack = Container.canAddItemToSlot(this.slot, hand, true);
               if (canStack) {
                  int stacksize = 1;
                  if (this.slot.getHasStack()) {
                     stacksize += slotItem.getCount();
                  }

                  stacksize = Math.min(stacksize, this.getStackLimit(this.slot, hand));
                  if (this.slot.getHasStack() && stacksize > slotItem.getCount() || !this.slot.getHasStack() && stacksize > 0) {
                     ItemStack inSlot = hand.copy();
                     inSlot.setCount(stacksize);
                     this.slot.putStack(inSlot);
                     hand.shrink(1);
                  }
               } else {
                  this.switchItems();
               }
            }
         } else if (this.slot.getHasStack() && this.slot.canTakeStack(this.getPlayer())) {
            this.takeStack(false, inventoryplayer);
         }
      } else if (mouseButton == 2) {
         if (hand.isEmpty() && this.slot.getHasStack() && this.getPlayer().capabilities.isCreativeMode) {
            ItemStack stack = slotItem.copy();
            stack.setCount(this.getItemStackLimit(stack));
            inventoryplayer.setItemStack(stack);
         }
      } else if (mouseButton == 3 && (scrolled == 1 || this.slot.canTakeStack(this.getPlayer()))) {
         this.transferIntoOtherInventory(scrolled == -1, 1);
      }

   }

   public void splitStack(int[] slots, ItemStack stack, boolean isRightClick) {
      stack = stack.copy();
      int StackPerSlot = MathHelper.floor((float)stack.getCount() / (float)slots.length);
      if (isRightClick) {
         StackPerSlot = 1;
      }

      for(int i = 0; i < slots.length; ++i) {
         SlotControl control = (SlotControl)this.getParent().controls.get(slots[i]);
         int stackSize = control.getStackLimit(control.slot, stack);
         if (control.slot.getHasStack()) {
            stackSize -= control.slot.getStack().getCount();
         }

         int use = Math.min(StackPerSlot, Math.min(stackSize, stack.getCount()));
         if (use > 0) {
            int size = use;
            if (control.slot.getHasStack()) {
               size = use + control.slot.getStack().getCount();
            }

            ItemStack inSlot = stack.copy();
            inSlot.setCount(size);
            control.slot.putStack(inSlot);
         }

         stack.shrink(use);
      }

      this.getPlayer().inventory.setItemStack(stack.copy());
   }
}
