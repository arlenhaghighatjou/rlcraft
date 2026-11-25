package dev.satyrn.wolfarmor.common.inventory;

import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.advancements.WolfArmorTrigger;
import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.api.util.Criteria;
import dev.satyrn.wolfarmor.api.util.Items;
import dev.satyrn.wolfarmor.item.ItemWolfArmor;
import javax.annotation.Nonnull;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;

public class ContainerWolfInventory extends Container {
   public static final int INVENTORY_SLOT_ARMOR = 0;
   public static final int INVENTORY_SLOT_CHEST_START = 1;
   private final IInventory wolfInventory;
   private final EntityWolf theWolf;

   public ContainerWolfInventory(@Nonnull IInventory playerInventory, @Nonnull IInventory wolfInventory, @Nonnull final EntityWolf theWolf, @Nonnull final EntityPlayer player) {
      this.wolfInventory = wolfInventory;
      this.theWolf = theWolf;
      this.wolfInventory.openInventory(player);
      this.addSlotToContainer(new Slot(wolfInventory, 0, 8, 18) {
         public int getSlotStackLimit() {
            return 1;
         }

         public boolean isItemValid(@Nonnull ItemStack stack) {
            return stack.isEmpty() || super.isItemValid(stack) && Items.isValidWolfArmor(stack.getItem());
         }

         public void onSlotChanged() {
            super.onSlotChanged();
            ItemStack stack = this.getStack();
            if (player instanceof EntityPlayerMP && !stack.isEmpty() && Items.isValidWolfArmor(stack.getItem())) {
               ((WolfArmorTrigger)Criteria.EQUIP_WOLF_ARMOR).trigger((EntityPlayerMP)player, theWolf);
            }

            if (stack.getItem() instanceof ItemWolfArmor && !theWolf.getEntityWorld().isRemote) {
               ItemWolfArmor wolfArmor = (ItemWolfArmor)stack.getItem();
               SoundEvent sound = wolfArmor.getMaterial().getEquipSound();
               theWolf.playSound(sound, 1.0F, (theWolf.getRNG().nextFloat() - theWolf.getRNG().nextFloat()) * 0.2F + 1.0F);
            }

         }
      });
      IArmoredWolf wolfArmor = (IArmoredWolf)theWolf;
      int columns = WolfArmorMod.getConfig().getChestSize().getColumns();
      int rows = WolfArmorMod.getConfig().getChestSize().getRows();
      int xItemOffset = (5 - columns) * 9;
      int yItemOffset = (3 - rows) * 9;
      if (wolfArmor.getHasChest()) {
         for(int y = 0; y < rows; ++y) {
            for(int x = 0; x < columns; ++x) {
               this.addSlotToContainer(new Slot(wolfInventory, 1 + x + y * columns, 80 + xItemOffset + x * 18, 18 + yItemOffset + y * 18));
            }
         }
      }

      for(int y = 0; y < 3; ++y) {
         for(int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
         }
      }

      for(int x = 0; x < 9; ++x) {
         this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 142));
      }

   }

   @Nonnull
   public ItemStack transferStackInSlot(@Nonnull EntityPlayer player, int slot) {
      ItemStack stack = ItemStack.EMPTY;
      Slot inventorySlot = (Slot)this.inventorySlots.get(slot);
      if (inventorySlot != null && inventorySlot.getHasStack()) {
         ItemStack stackInSlot = inventorySlot.getStack();
         if (!stackInSlot.isEmpty()) {
            stack = stackInSlot.copy();
            if (slot < this.wolfInventory.getSizeInventory()) {
               if (!this.mergeItemStack(stackInSlot, this.wolfInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.getSlot(0).isItemValid(stackInSlot) && !this.getSlot(0).getHasStack()) {
               if (!this.mergeItemStack(stackInSlot, 0, 2, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.wolfInventory.getSizeInventory() <= 1 || !this.mergeItemStack(stackInSlot, 1, this.wolfInventory.getSizeInventory(), false)) {
               return ItemStack.EMPTY;
            }

            if (stackInSlot.getCount() == 0) {
               inventorySlot.putStack(ItemStack.EMPTY);
            } else {
               inventorySlot.onSlotChanged();
            }
         }
      }

      return stack;
   }

   public boolean canInteractWith(@Nonnull EntityPlayer player) {
      return this.wolfInventory.isUsableByPlayer(player) && !this.theWolf.isDead && this.theWolf.getDistance(player) < 8.0F;
   }
}
