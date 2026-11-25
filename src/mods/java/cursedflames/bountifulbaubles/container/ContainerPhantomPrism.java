package cursedflames.bountifulbaubles.container;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.container.SlotBauble;
import cursedflames.bountifulbaubles.network.NBTPacket;
import cursedflames.bountifulbaubles.network.PacketHandler;
import cursedflames.bountifulbaubles.util.GenericSlot;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vazkii.botania.api.item.IPhantomInkable;

public class ContainerPhantomPrism extends Container {
   EntityPlayer player;
   public boolean guiDirty = false;
   IInventory playerInventory;
   IBaublesItemHandler baublesInventory;
   public int[] currentStates = new int[11];

   public ContainerPhantomPrism(EntityPlayer player) {
      this.player = player;
      this.playerInventory = player.inventory;
      this.baublesInventory = BaublesApi.getBaublesHandler(player);
      this.addPlayerSlots();
      if (!player.world.isRemote) {
         ;
      }
   }

   protected void addPlayerSlots() {
      for(int row = 0; row < 2; ++row) {
         for(int col = 0; col < 4 && row * 4 + col < this.baublesInventory.getSlots(); ++col) {
            int x = 8 + (col + 5) * 18;
            int y = row * 18 + 26;
            this.addSlotToContainer(new SlotBauble(this.player, this.baublesInventory, col + row * 4, x, y));
         }
      }

      EntityEquipmentSlot[] armorSlots = new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD};

      for(int row = 3; row >= 0; --row) {
         int x = 8;
         int y = 8 + (3 - row) * 18;
         EntityEquipmentSlot armorSlot = armorSlots[row];
         Slot slot = new GenericSlot(this.playerInventory, 36 + row, x, y, (stack) -> {
            return !stack.isEmpty() && stack.getItem().isValidArmor(stack, armorSlot, this.player);
         }, 1);
         this.addSlotToContainer(slot);
      }

      this.addSlotToContainer(new Slot(this.playerInventory, 40, 170, 138));

      for(int row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            int x = 8 + col * 18;
            int y = row * 18 + 80;
            this.addSlotToContainer(new Slot(this.playerInventory, col + row * 9 + 9, x, y));
         }
      }

      for(int row = 0; row < 9; ++row) {
         int x = 8 + row * 18;
         int y = 138;
         this.addSlotToContainer(new Slot(this.playerInventory, row, x, y));
      }

   }

   public boolean canInteractWith(EntityPlayer player) {
      return player == this.player;
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();
      int[] states = new int[11];

      for(int i = 0; i < 7; ++i) {
         ItemStack stack = this.baublesInventory.getStackInSlot(i);
         if (stack != null && !stack.isEmpty() && stack.getItem() instanceof IPhantomInkable) {
            states[i] = ((IPhantomInkable)stack.getItem()).hasPhantomInk(stack) ? -1 : 1;
         } else {
            states[i] = 0;
         }
      }

      for(int i = 0; i < 4; ++i) {
         ItemStack stack = this.playerInventory.getStackInSlot(36 + i);
         if (stack != null && !stack.isEmpty() && stack.getItem() instanceof IPhantomInkable) {
            states[i + 7] = ((IPhantomInkable)stack.getItem()).hasPhantomInk(stack) ? -1 : 1;
         } else {
            states[i + 7] = 0;
         }
      }

      NBTTagCompound changes = new NBTTagCompound();

      for(int i = 0; i < 11; ++i) {
         if (states[i] != this.currentStates[i]) {
            changes.setInteger(String.valueOf(i), states[i]);
            this.currentStates[i] = states[i];
         }
      }

      if (!changes.hasNoTags() && !this.player.world.isRemote) {
         for(int j = 0; j < this.listeners.size(); ++j) {
            PacketHandler.INSTANCE.sendTo(new NBTPacket(changes, PacketHandler.HandlerIds.PRISM_UPDATE_GUI.id), (EntityPlayerMP)this.listeners.get(j));
         }

      }
   }

   public void readChanges(NBTTagCompound tag) {
      for(int i = 0; i < 11; ++i) {
         String key = String.valueOf(i);
         if (tag.hasKey(key)) {
            int ind = Integer.valueOf(key).intValue();
            if (ind >= 0 && ind < 11) {
               int val = tag.getInteger(key);
               this.currentStates[ind] = val;
            }
         }
      }

      this.guiDirty = true;
   }

   public void toggle(int index) {
      ItemStack stack = null;
      if (index < 7) {
         stack = this.baublesInventory.getStackInSlot(index);
      } else if (index < 11) {
         stack = this.playerInventory.getStackInSlot(29 + index);
      }

      if (stack != null && !stack.isEmpty()) {
         if (stack.getItem() instanceof IPhantomInkable) {
            IPhantomInkable item = (IPhantomInkable)stack.getItem();
            item.setPhantomInk(stack, !item.hasPhantomInk(stack));
            this.detectAndSendChanges();
         }
      }
   }

   @Nullable
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index < 11) {
            if (!this.mergeItemStack(itemstack1, 11, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, 11, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemstack;
   }
}
