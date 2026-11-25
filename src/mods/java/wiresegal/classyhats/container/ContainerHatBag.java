package wiresegal.classyhats.container;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.capability.CapabilityHatContainer;
import wiresegal.classyhats.capability.data.IHatContainer;
import wiresegal.classyhats.event.GuiEvents;
import wiresegal.classyhats.item.ItemHat;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0010\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u0005H\u0016J\u000e\u0010$\u001a\u00020%2\u0006\u0010\u0006\u001a\u00020\u0007J\u0018\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0007H\u0016R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\r\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u0012\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u0011\u0010\u0014\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000fR\u0011\u0010\u0016\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000fR\u0014\u0010\u0018\u001a\u00020\u0007X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u000fR\u0011\u0010\u001a\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u000fR\u0011\u0010\u001c\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u000fR\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u000f\"\u0004\b\u001f\u0010 \u00a8\u0006*"},
   d2 = {"Lwiresegal/classyhats/container/ContainerHatBag;", "Lnet/minecraft/inventory/Container;", "playerInv", "Lnet/minecraft/entity/player/InventoryPlayer;", "thePlayer", "Lnet/minecraft/entity/player/EntityPlayer;", "slotPos", "", "(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/entity/player/EntityPlayer;I)V", "hat", "Lwiresegal/classyhats/capability/data/IHatContainer;", "getHat", "()Lwiresegal/classyhats/capability/data/IHatContainer;", "idxHatsEnd", "getIdxHatsEnd", "()I", "idxHatsStart", "getIdxHatsStart", "idxHotbarEnd", "getIdxHotbarEnd", "idxHotbarStart", "getIdxHotbarStart", "idxPlayerHatEnd", "getIdxPlayerHatEnd", "idxPlayerHatStart", "getIdxPlayerHatStart", "idxPlayerInvEnd", "getIdxPlayerInvEnd", "idxPlayerInvStart", "getIdxPlayerInvStart", "getSlotPos", "setSlotPos", "(I)V", "canInteractWith", "", "par1EntityPlayer", "setSlot", "", "transferStackInSlot", "Lnet/minecraft/item/ItemStack;", "playerIn", "index", "classyhats"}
)
public final class ContainerHatBag extends Container {
   @NotNull
   private final IHatContainer hat;
   private final int idxPlayerHatStart;
   private final int idxPlayerHatEnd;
   private final int idxHatsStart;
   private final int idxHatsEnd;
   private final int idxPlayerInvStart;
   private final int idxPlayerInvEnd;
   private final int idxHotbarStart;
   private final int idxHotbarEnd;
   private int slotPos;

   @NotNull
   public final IHatContainer getHat() {
      return this.hat;
   }

   public final int getIdxPlayerHatStart() {
      return this.idxPlayerHatStart;
   }

   public final int getIdxPlayerHatEnd() {
      return this.idxPlayerHatEnd;
   }

   public final int getIdxHatsStart() {
      return this.idxHatsStart;
   }

   public final int getIdxHatsEnd() {
      return this.idxHatsEnd;
   }

   public final int getIdxPlayerInvStart() {
      return this.idxPlayerInvStart;
   }

   public final int getIdxPlayerInvEnd() {
      return this.idxPlayerInvEnd;
   }

   public final int getIdxHotbarStart() {
      return this.idxHotbarStart;
   }

   public final int getIdxHotbarEnd() {
      return this.idxHotbarEnd;
   }

   public boolean canInteractWith(@NotNull EntityPlayer par1EntityPlayer) {
      Intrinsics.checkParameterIsNotNull(par1EntityPlayer, "par1EntityPlayer");
      return true;
   }

   public final void setSlot(int slotPos) {
      int totalSlots = 19;
      this.slotPos = Math.max(0, Math.min(totalSlots, slotPos));
      final int j = 0;

      for(byte var4 = 4; j <= var4; ++j) {
         final int i = 0;

         for(byte var6 = 9; i <= var6; ++i) {
            <undefinedtype> slot = new SlotItemHandler((IItemHandler)this.hat.getHats(), this.slotPos * 50 + i + j * 10, 8 + i * 18, 8 + j * 18) {
               public boolean isItemValid(@NotNull ItemStack stack) {
                  Intrinsics.checkParameterIsNotNull(stack, "stack");
                  return stack.getItem() instanceof ItemHat;
               }
            };
            slot.slotNumber = j * 10 + i + this.idxHatsStart;
            this.inventorySlots.set(slot.slotNumber, slot);
            this.inventoryItemStacks.set(slot.slotNumber, slot.getStack());
         }
      }

   }

   @NotNull
   public ItemStack transferStackInSlot(@NotNull EntityPlayer playerIn, int index) {
      Intrinsics.checkParameterIsNotNull(playerIn, "playerIn");
      ItemStack stack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      Intrinsics.checkExpressionValueIsNotNull(slot, "slot");
      if (slot.getHasStack()) {
         ItemStack inSlot;
         label72: {
            inSlot = slot.getStack();
            stack = inSlot.copy();
            int var10000 = this.idxPlayerHatEnd;
            if (this.idxPlayerHatStart <= index) {
               if (var10000 > index) {
                  if (!this.mergeItemStack(inSlot, this.idxHatsStart, this.idxHatsEnd, false) && !this.mergeItemStack(inSlot, this.idxPlayerInvStart, this.idxPlayerInvEnd, false) && !this.mergeItemStack(inSlot, this.idxHotbarStart, this.idxHotbarEnd, false)) {
                     ItemStack var14 = ItemStack.EMPTY;
                     Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
                     return var14;
                  }
                  break label72;
               }
            }

            var10000 = this.idxHatsEnd;
            if (this.idxHatsStart <= index) {
               if (var10000 > index) {
                  if (!this.mergeItemStack(inSlot, this.idxPlayerHatStart, this.idxPlayerHatEnd, false) && !this.mergeItemStack(inSlot, this.idxPlayerInvStart, this.idxPlayerInvEnd, false) && !this.mergeItemStack(inSlot, this.idxHotbarStart, this.idxHotbarEnd, false)) {
                     ItemStack var11 = ItemStack.EMPTY;
                     Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
                     return var11;
                  }
                  break label72;
               }
            }

            var10000 = this.idxPlayerInvEnd;
            if (this.idxPlayerInvStart <= index) {
               if (var10000 > index) {
                  if (!this.mergeItemStack(inSlot, this.idxHatsStart, this.idxHatsEnd, false) && !this.mergeItemStack(inSlot, this.idxPlayerHatStart, this.idxPlayerHatEnd, false) && !this.mergeItemStack(inSlot, this.idxHotbarStart, this.idxHotbarEnd, false)) {
                     ItemStack var10 = ItemStack.EMPTY;
                     Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
                     return var10;
                  }
                  break label72;
               }
            }

            if (!this.mergeItemStack(inSlot, this.idxHatsStart, this.idxHatsEnd, false) && !this.mergeItemStack(inSlot, this.idxPlayerHatStart, this.idxPlayerHatEnd, false) && !this.mergeItemStack(inSlot, this.idxPlayerInvStart, this.idxPlayerInvEnd, false)) {
               ItemStack var9 = ItemStack.EMPTY;
               Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
               return var9;
            }
         }

         Intrinsics.checkExpressionValueIsNotNull(inSlot, "inSlot");
         if (inSlot.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         int var12 = inSlot.getCount();
         Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
         if (var12 == stack.getCount()) {
            ItemStack var13 = ItemStack.EMPTY;
            Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
            return var13;
         }

         ItemStack taken = slot.onTake(playerIn, inSlot);
         if (index == 0) {
            playerIn.dropItem(taken, false);
         }
      }

      Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
      return stack;
   }

   public final int getSlotPos() {
      return this.slotPos;
   }

   public final void setSlotPos(int var1) {
      this.slotPos = <set-?>;
   }

   public ContainerHatBag(@NotNull final InventoryPlayer playerInv, @NotNull EntityPlayer thePlayer, int slotPos) {
      Intrinsics.checkParameterIsNotNull(playerInv, "playerInv");
      Intrinsics.checkParameterIsNotNull(thePlayer, "thePlayer");
      super();
      this.slotPos = slotPos;
      this.hat = CapabilityHatContainer.Companion.getCapability(thePlayer);
      this.addSlotToContainer(new Slot(new HatInventoryWrapper(this.hat), 0, 188, 110) {
         public boolean isItemValid(@NotNull ItemStack stack) {
            Intrinsics.checkParameterIsNotNull(stack, "stack");
            return stack.getItem() instanceof ItemHat;
         }

         @NotNull
         public String getSlotTexture() {
            String var10000 = GuiEvents.INSTANCE.getSLOT_HAT().toString();
            Intrinsics.checkExpressionValueIsNotNull(var10000, "GuiEvents.SLOT_HAT.toString()");
            return var10000;
         }
      });
      this.idxPlayerHatEnd = this.inventorySlots.size();
      this.idxHatsStart = this.inventorySlots.size();
      int totalSlots = 19;
      this.slotPos = Math.max(0, Math.min(totalSlots, this.slotPos));
      final int i = 0;

      for(byte var6 = 4; i <= var6; ++i) {
         final int j = 0;

         for(byte var8 = 9; j <= var8; ++j) {
            this.addSlotToContainer((Slot)(new SlotItemHandler((IItemHandler)this.hat.getHats(), this.slotPos * 50 + j + i * 10, 8 + j * 18, 8 + i * 18) {
               public boolean isItemValid(@NotNull ItemStack stack) {
                  Intrinsics.checkParameterIsNotNull(stack, "stack");
                  return stack.getItem() instanceof ItemHat;
               }
            }));
         }
      }

      this.idxHatsEnd = this.inventorySlots.size();
      this.idxPlayerInvStart = this.inventorySlots.size();
      i = 0;

      for(byte var11 = 2; i <= var11; ++i) {
         int var13 = 0;

         for(byte var14 = 8; var13 <= var14; ++var13) {
            this.addSlotToContainer(new Slot((IInventory)playerInv, var13 + (i + 1) * 9, 17 + var13 * 18, 110 + i * 18));
         }
      }

      this.idxPlayerInvEnd = this.inventorySlots.size();
      this.idxHotbarStart = this.inventorySlots.size();
      i = 0;

      for(byte var12 = 8; i <= var12; ++i) {
         this.addSlotToContainer(new Slot((IInventory)playerInv, i, 17 + i * 18, 168));
      }

      this.idxHotbarEnd = this.inventorySlots.size();
      this.addSlotToContainer(new Slot((IInventory)playerInv, 40, 188, 168) {
         @NotNull
         public String getSlotTexture() {
            return "minecraft:items/empty_armor_slot_shield";
         }
      });
   }
}
