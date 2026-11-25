package ivorius.reccomplex.gui.loot;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.network.PacketGuiAction.ActionHandler;
import ivorius.reccomplex.gui.SlotDynamicIndex;
import ivorius.reccomplex.world.storage.loot.GenericLootTable;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerEditLootTableItems extends Container implements ActionHandler {
   public static final int ITEM_ROWS = 4;
   public static final int ITEM_COLUMNS = 1;
   public static final int ITEMS_PER_PAGE = 4;
   public static final int SEGMENT_WIDTH = 288;
   private final String key;
   private final GenericLootTable.Component component;
   public InventoryGenericLootTable_Single inventory;
   private List<SlotDynamicIndex> scrollableSlots = new ArrayList();

   public ContainerEditLootTableItems(EntityPlayer player, String key, GenericLootTable.Component component) {
      this.inventory = new InventoryGenericLootTable_Single(component.items);
      this.key = key;
      this.component = component;
      InventoryPlayer inventoryplayer = player.inventory;

      for(int col = 0; col < 1; ++col) {
         for(int row = 0; row < 4; ++row) {
            int index = col * 4 + row;
            SlotDynamicIndex slotLeft = new SlotDynamicIndex(this.inventory, index, col * 288, 20 + row * 18);
            this.addSlotToContainer(slotLeft);
            this.scrollableSlots.add(slotLeft);
         }
      }

      int basePlayerY = 105;
      int basePlayerX = 63;

      for(int row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            int index = col + row * 9 + 9;
            this.addSlotToContainer(new Slot(inventoryplayer, index, basePlayerX + col * 18, basePlayerY + row * 18));
         }
      }

      for(int col = 0; col < 9; ++col) {
         this.addSlotToContainer(new Slot(inventoryplayer, col, basePlayerX + col * 18, basePlayerY + 54 + 4));
      }

   }

   public boolean canInteractWith(EntityPlayer var1) {
      return true;
   }

   public void scrollTo(int colShift) {
      for(int col = 0; col < 1; ++col) {
         for(int row = 0; row < 4; ++row) {
            int index = row + (col + colShift) * 4;
            int scrollableSlotsIndex = row + col * 4;
            ((SlotDynamicIndex)this.scrollableSlots.get(scrollableSlotsIndex)).slotIndex = index;
         }
      }

   }

   public void handleAction(String context, ByteBuf buffer) {
      if ("igSelectCol".equals(context)) {
         this.scrollTo(buffer.readInt());
      }

   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(slotIndex);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         int generatorIndexMax = 4;
         if (slotIndex < generatorIndexMax) {
            if (!this.mergeItemStack(itemstack1, generatorIndexMax, generatorIndexMax + 36, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, generatorIndexMax, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, itemstack1);
      }

      return itemstack;
   }
}
