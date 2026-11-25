package com.alcatrazescapee.alcatrazcore.network.capability;

import com.alcatrazescapee.alcatrazcore.network.PacketTContainerUpdate;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;

@ParametersAreNonnullByDefault
public abstract class CapabilityContainerListener<T> implements IContainerListener {
   private final EntityPlayerMP player;
   private final Capability<T> capability;
   private final EnumFacing targetFace;

   public CapabilityContainerListener(EntityPlayerMP player, Capability<T> capability, @Nullable EnumFacing targetFace) {
      this.player = player;
      this.capability = capability;
      this.targetFace = targetFace;
   }

   public void sendAllContents(Container container, NonNullList<ItemStack> items) {
      NonNullList<ItemStack> syncableItemsList = NonNullList.withSize(items.size(), ItemStack.EMPTY);

      for(int index = 0; index < syncableItemsList.size(); ++index) {
         ItemStack stack = (ItemStack)syncableItemsList.get(index);
         if (this.shouldSyncItem(stack)) {
            syncableItemsList.set(index, stack);
         } else {
            syncableItemsList.set(index, ItemStack.EMPTY);
         }
      }

      PacketTContainerUpdate<T, ?> message = this.createBulkUpdateMessage(container.windowId, syncableItemsList);
      if (message.hasData()) {
         this.sendToPlayer(this.player, message);
      }

   }

   public void sendSlotContents(Container container, int slotIndex, ItemStack stack) {
      if (this.shouldSyncItem(stack)) {
         T cap = (T)stack.getCapability(this.capability, this.targetFace);
         if (cap != null) {
            PacketTContainerUpdate<T, ?> message = this.createSingleUpdateMessage(container.windowId, slotIndex, cap);
            if (message.hasData()) {
               this.sendToPlayer(this.player, message);
            }

         }
      }
   }

   public void sendWindowProperty(Container container, int ID, int value) {
   }

   public void sendAllWindowProperties(Container container, IInventory inventory) {
   }

   protected abstract PacketTContainerUpdate<T, ?> createBulkUpdateMessage(int var1, NonNullList<ItemStack> var2);

   protected abstract PacketTContainerUpdate<T, ?> createSingleUpdateMessage(int var1, int var2, T var3);

   protected abstract void sendToPlayer(EntityPlayerMP var1, PacketTContainerUpdate<T, ?> var2);

   protected boolean shouldSyncItem(ItemStack stack) {
      return stack.hasCapability(this.capability, this.targetFace);
   }
}
