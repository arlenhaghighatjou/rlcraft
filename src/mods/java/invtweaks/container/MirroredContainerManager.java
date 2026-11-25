package invtweaks.container;

import invtweaks.InvTweaks;
import invtweaks.InvTweaksObfuscation;
import invtweaks.api.container.ContainerSection;
import invtweaks.forge.InvTweaksMod;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MirroredContainerManager implements IContainerManager {
   private ItemStack[] slotItems;
   @Nullable
   private ItemStack heldItem;
   @NotNull
   private List<ItemStack> droppedItems = new ArrayList();
   private Container container;
   private Map<ContainerSection, List<Integer>> itemRefs;
   @Nullable
   private Map<ContainerSection, List<Slot>> slotRefs;

   public MirroredContainerManager(Container cont) {
      this.container = cont;
      this.slotRefs = InvTweaksObfuscation.getContainerSlotMap(this.container);
      if (this.slotRefs == null) {
         this.slotRefs = new HashMap();
      }

      List<Slot> slots = this.container.inventorySlots;
      int size = slots.size();
      this.itemRefs = new HashMap();

      for(Entry<ContainerSection, List<Slot>> section : this.slotRefs.entrySet()) {
         Stream var10000 = ((List)section.getValue()).stream();
         slots.getClass();
         List<Integer> slotIndices = (List)var10000.map(slots::indexOf).collect(Collectors.toList());
         this.itemRefs.put(section.getKey(), slotIndices);
      }

      this.slotItems = new ItemStack[size];

      for(int i = 0; i < size; ++i) {
         this.slotItems[i] = ((Slot)slots.get(i)).getStack().copy();
      }

      this.heldItem = InvTweaks.getInstance().getHeldStack();
   }

   public boolean move(ContainerSection srcSection, int srcIndex, ContainerSection destSection, int destIndex) {
      int srcSlotIdx = this.slotPositionToIndex(srcSection, srcIndex);
      if (destIndex == -999) {
         this.droppedItems.add(this.slotItems[srcSlotIdx]);
         this.slotItems[srcSlotIdx] = null;
      }

      int destSlotIdx = this.slotPositionToIndex(destSection, destIndex);
      Slot srcSlot = this.getSlot(srcSection, srcIndex);
      Slot destSlot = this.getSlot(destSection, destIndex);
      ItemStack srcItem = this.slotItems[srcSlotIdx];
      ItemStack destItem = this.slotItems[destSlotIdx];
      if (srcItem != null && !destSlot.isItemValid(srcItem)) {
         return false;
      } else if (destItem != null && !srcSlot.isItemValid(destItem)) {
         return false;
      } else {
         this.slotItems[srcSlotIdx] = destItem;
         this.slotItems[destSlotIdx] = srcItem;
         return true;
      }
   }

   public boolean moveSome(ContainerSection srcSection, int srcIndex, ContainerSection destSection, int destIndex, int amount) {
      return false;
   }

   public boolean putHoldItemDown(ContainerSection destSection, int destIndex) {
      int destSlotIdx = this.slotPositionToIndex(destSection, destIndex);
      if (this.slotItems[destSlotIdx] != null) {
         return false;
      } else {
         this.slotItems[destSlotIdx] = this.heldItem;
         this.heldItem = null;
         return true;
      }
   }

   public void click(ContainerSection section, int index, boolean rightClick) {
   }

   public boolean hasSection(ContainerSection section) {
      return this.itemRefs.containsKey(section);
   }

   public List<Slot> getSlots(ContainerSection section) {
      return (List)this.slotRefs.get(section);
   }

   public int getSize() {
      return this.slotItems.length;
   }

   public int getSize(ContainerSection section) {
      return ((List)this.itemRefs.get(section)).size();
   }

   public int getFirstEmptyIndex(ContainerSection section) {
      int i = 0;

      for(Iterator var3 = ((List)this.itemRefs.get(section)).iterator(); var3.hasNext(); ++i) {
         int slot = ((Integer)var3.next()).intValue();
         if (this.slotItems[slot].isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   public boolean isSlotEmpty(ContainerSection section, int slot) {
      return this.getItemStack(section, slot).isEmpty();
   }

   @NotNull
   public Slot getSlot(ContainerSection section, int index) {
      return this.container.getSlot(this.slotPositionToIndex(section, index));
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
      return this.slotItems[this.slotPositionToIndex(section, index)];
   }

   public Container getContainer() {
      return this.container;
   }

   public void applyChanges() {
      InvTweaksMod.proxy.sortComplete();
   }

   private int slotPositionToIndex(ContainerSection section, int index) {
      if (index == -999) {
         return -999;
      } else if (index < 0) {
         return -1;
      } else {
         return this.hasSection(section) ? ((Integer)((List)this.itemRefs.get(section)).get(index)).intValue() : -1;
      }
   }
}
