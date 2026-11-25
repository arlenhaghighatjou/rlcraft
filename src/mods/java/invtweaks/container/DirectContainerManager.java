package invtweaks.container;

import invtweaks.InvTweaks;
import invtweaks.InvTweaksObfuscation;
import invtweaks.api.container.ContainerSection;
import invtweaks.forge.InvTweaksMod;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DirectContainerManager implements IContainerManager {
   @NotNull
   private final Container container;
   @NotNull
   private Map<ContainerSection, List<Slot>> slotRefs = new HashMap();

   public DirectContainerManager(@NotNull Container cont) {
      this.container = cont;
      this.initSlots();
   }

   private void initSlots() {
      Map<ContainerSection, List<Slot>> refs = InvTweaksObfuscation.getContainerSlotMap(this.container);
      if (refs == null) {
         this.slotRefs = new HashMap();
      } else {
         this.slotRefs = refs;
      }

   }

   public boolean move(ContainerSection srcSection, int srcIndex, ContainerSection destSection, int destIndex) {
      ItemStack srcStack = this.getItemStack(srcSection, srcIndex);
      ItemStack destStack = this.getItemStack(destSection, destIndex);
      if (srcStack.isEmpty() && destIndex != -999) {
         return false;
      } else if (srcSection == destSection && srcIndex == destIndex) {
         return true;
      } else {
         if (destIndex != -999) {
            Slot destSlot = this.getSlot(destSection, destIndex);
            if (!destSlot.isItemValid(srcStack)) {
               return false;
            }
         }

         if (!InvTweaks.getInstance().getHeldStack().isEmpty()) {
            int firstEmptyIndex = this.getFirstEmptyIndex(ContainerSection.INVENTORY);
            if (firstEmptyIndex == -1) {
               return false;
            }

            this.leftClick(ContainerSection.INVENTORY, firstEmptyIndex);
         }

         assert !srcStack.isEmpty();

         if (!destStack.isEmpty() && !InvTweaksObfuscation.areItemsStackable(srcStack, destStack)) {
            int intermediateSlot = this.getFirstEmptyUsableSlotNumber();
            ContainerSection intermediateSection = this.getSlotSection(intermediateSlot);
            int intermediateIndex = this.getSlotIndex(intermediateSlot);
            if (intermediateIndex == -1) {
               return false;
            }

            Slot interSlot = this.getSlot(intermediateSection, intermediateIndex);
            if (!interSlot.isItemValid(destStack)) {
               return false;
            }

            Slot srcSlot = this.getSlot(srcSection, srcIndex);
            if (!srcSlot.isItemValid(destStack)) {
               return false;
            }

            this.leftClick(destSection, destIndex);
            this.leftClick(intermediateSection, intermediateIndex);
            this.leftClick(srcSection, srcIndex);
            this.leftClick(destSection, destIndex);
            this.leftClick(intermediateSection, intermediateIndex);
            this.leftClick(srcSection, srcIndex);
         } else {
            this.leftClick(srcSection, srcIndex);
            this.leftClick(destSection, destIndex);
            if (!InvTweaks.getInstance().getHeldStack().isEmpty()) {
               Slot srcSlot = this.getSlot(srcSection, srcIndex);
               if (srcSlot.isItemValid(InvTweaks.getInstance().getHeldStack())) {
                  this.leftClick(srcSection, srcIndex);
               } else {
                  int firstEmptyIndex = this.getFirstEmptyIndex(ContainerSection.INVENTORY);
                  if (firstEmptyIndex != -1) {
                     this.leftClick(ContainerSection.INVENTORY, firstEmptyIndex);
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean moveSome(ContainerSection srcSection, int srcIndex, ContainerSection destSection, int destIndex, int amount) {
      ItemStack source = this.getItemStack(srcSection, srcIndex);
      if (!source.isEmpty() && (srcSection != destSection || srcIndex != destIndex)) {
         ItemStack destination = this.getItemStack(srcSection, srcIndex);
         int sourceSize = source.getCount();
         int movedAmount = Math.min(amount, sourceSize);
         if (!destination.isEmpty() && !InvTweaksObfuscation.areItemStacksEqual(source, destination)) {
            return false;
         } else {
            this.leftClick(srcSection, srcIndex);

            for(int i = 0; i < movedAmount; ++i) {
               this.rightClick(destSection, destIndex);
            }

            if (movedAmount < sourceSize) {
               this.leftClick(srcSection, srcIndex);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public boolean putHoldItemDown(ContainerSection destSection, int destIndex) {
      ItemStack heldStack = InvTweaks.getInstance().getHeldStack();
      if (!heldStack.isEmpty()) {
         if (this.getItemStack(destSection, destIndex).isEmpty()) {
            this.click(destSection, destIndex, false);
            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public void click(ContainerSection section, int index, boolean rightClick) {
      int slot = this.indexToSlot(section, index);
      if (slot != -1) {
         int data = rightClick ? 1 : 0;
         InvTweaksMod.proxy.slotClick(InvTweaks.getInstance().getPlayerController(), this.container.windowId, slot, data, ClickType.PICKUP, InvTweaks.getInstance().getThePlayer());
      }

   }

   public boolean hasSection(ContainerSection section) {
      return this.slotRefs.containsKey(section);
   }

   public List<Slot> getSlots(ContainerSection section) {
      return (List)this.slotRefs.get(section);
   }

   public int getSize() {
      int result = 0;

      for(List<Slot> slots : this.slotRefs.values()) {
         result += slots.size();
      }

      return result;
   }

   public int getSize(ContainerSection section) {
      return this.hasSection(section) ? ((List)this.slotRefs.get(section)).size() : 0;
   }

   public int getFirstEmptyIndex(ContainerSection section) {
      int i = 0;

      for(Slot slot : (List)this.slotRefs.get(section)) {
         if (!slot.getHasStack()) {
            return i;
         }

         ++i;
      }

      return -1;
   }

   public boolean isSlotEmpty(ContainerSection section, int slot) {
      return this.hasSection(section) && this.getItemStack(section, slot).isEmpty();
   }

   @Nullable
   public Slot getSlot(ContainerSection section, int index) {
      List<Slot> slots = (List)this.slotRefs.get(section);
      return slots != null ? (Slot)slots.get(index) : null;
   }

   public int getSlotIndex(int slotNumber, boolean preferInventory) {
      for(ContainerSection section : this.slotRefs.keySet()) {
         if (!preferInventory && section != ContainerSection.INVENTORY || preferInventory && section != ContainerSection.INVENTORY_NOT_HOTBAR && section != ContainerSection.INVENTORY_HOTBAR) {
            int i = 0;

            for(Slot slot : (List)this.slotRefs.get(section)) {
               if (InvTweaksObfuscation.getSlotNumber(slot) == slotNumber) {
                  return i;
               }

               ++i;
            }
         }
      }

      return -1;
   }

   @Nullable
   public ContainerSection getSlotSection(int slotNumber) {
      label26:
      for(ContainerSection section : this.slotRefs.keySet()) {
         if (section != ContainerSection.INVENTORY) {
            Iterator var4 = ((List)this.slotRefs.get(section)).iterator();

            while(true) {
               if (!var4.hasNext()) {
                  continue label26;
               }

               Slot slot = (Slot)var4.next();
               if (InvTweaksObfuscation.getSlotNumber(slot) == slotNumber) {
                  break;
               }
            }

            return section;
         }
      }

      return null;
   }

   @NotNull
   public ItemStack getItemStack(ContainerSection section, int index) {
      int slot = this.indexToSlot(section, index);
      return slot >= 0 && slot < this.container.inventorySlots.size() ? InvTweaksObfuscation.getSlotStack(this.container, slot) : ItemStack.EMPTY;
   }

   @NotNull
   public Container getContainer() {
      return this.container;
   }

   private int getFirstEmptyUsableSlotNumber() {
      label23:
      for(ContainerSection section : this.slotRefs.keySet()) {
         Iterator var3 = ((List)this.slotRefs.get(section)).iterator();

         Slot slot;
         while(true) {
            if (!var3.hasNext()) {
               continue label23;
            }

            slot = (Slot)var3.next();
            if (InvTweaksObfuscation.isBasicSlot(slot) && !slot.getHasStack()) {
               break;
            }
         }

         return InvTweaksObfuscation.getSlotNumber(slot);
      }

      return -1;
   }

   private int indexToSlot(ContainerSection section, int index) {
      if (index == -999) {
         return -999;
      } else if (index < 0) {
         return -1;
      } else if (this.hasSection(section)) {
         Slot slot = (Slot)((List)this.slotRefs.get(section)).get(index);
         return slot != null ? InvTweaksObfuscation.getSlotNumber(slot) : -1;
      } else {
         return -1;
      }
   }

   public void applyChanges() {
      InvTweaksMod.proxy.sortComplete();
   }
}
