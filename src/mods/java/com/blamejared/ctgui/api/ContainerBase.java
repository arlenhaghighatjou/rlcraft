package com.blamejared.ctgui.api;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBase extends Container {
   protected List<SlotRecipe> recipeSlots = new LinkedList();

   public ContainerBase() {
   }

   public boolean canInteractWith(EntityPlayer player) {
      return true;
   }

   @Nullable
   public ItemStack slotClick(int i, int mousebtn, ClickType clickTypeIn, EntityPlayer player) {
      ItemStack stack = ItemStack.EMPTY;
      if (i >= 0 && this.getSlot(i) instanceof SlotRecipe) {
         if (mousebtn == 2) {
            this.getSlot(i).putStack(ItemStack.EMPTY);
         } else if (mousebtn == 0) {
            InventoryPlayer playerInv = player.inventory;
            this.getSlot(i).onSlotChanged();
            ItemStack stackSlot = this.getSlot(i).getStack();
            ItemStack stackHeld = playerInv.getItemStack();
            if (!stackSlot.isEmpty()) {
               stack = stackSlot.copy();
            }

            if (!stackHeld.isEmpty()) {
               ItemStack newStack = stackHeld.copy();
               if (!(this.getSlot(i) instanceof SlotRecipeOutput)) {
                  newStack.setCount(1);
               }

               this.getSlot(i).putStack(newStack);
            } else {
               this.getSlot(i).putStack(ItemStack.EMPTY);
            }
         } else if (mousebtn == 1) {
            InventoryPlayer playerInv = player.inventory;
            this.getSlot(i).onSlotChanged();
            ItemStack stackSlot = this.getSlot(i).getStack();
            ItemStack stackHeld = playerInv.getItemStack();
            if (!stackSlot.isEmpty()) {
               stack = stackSlot.copy();
            }

            if (!stackHeld.isEmpty()) {
               stackHeld = stackHeld.copy();
               if (!stackSlot.isEmpty() && stackHeld.isItemEqual(stackSlot) && i == 0) {
                  int max = stackSlot.getMaxStackSize();
                  stackSlot.setCount(stackSlot.getCount() + 1);
                  if (stackSlot.getCount() > max) {
                     stackSlot.setCount(max);
                  }

                  this.getSlot(i).putStack(stackSlot);
               } else {
                  stackHeld.setCount(1);
                  this.getSlot(i).putStack(stackHeld);
               }
            } else if (!stackSlot.isEmpty()) {
               stackSlot.setCount(stackSlot.getCount() - 1);
               if (stackSlot.getCount() == 0) {
                  this.getSlot(i).putStack(ItemStack.EMPTY);
               }
            }
         }
      } else {
         stack = super.slotClick(i, mousebtn, clickTypeIn, player);
      }

      return stack;
   }

   @Nullable
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      return ItemStack.EMPTY;
   }

   public List<SlotRecipe> getRecipeSlots() {
      return this.recipeSlots;
   }

   protected Slot addSlotToContainer(Slot slotIn) {
      if (slotIn instanceof SlotRecipe) {
         this.getRecipeSlots().add((SlotRecipe)slotIn);
      }

      return super.addSlotToContainer(slotIn);
   }
}
