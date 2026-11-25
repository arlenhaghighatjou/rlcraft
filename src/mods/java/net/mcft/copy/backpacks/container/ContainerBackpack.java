package net.mcft.copy.backpacks.container;

import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ChestContainer.RowSizeCallback;
import net.mcft.copy.backpacks.WearableBackpacks;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.misc.BackpackDataItems;
import net.mcft.copy.backpacks.misc.BackpackSize;
import net.mcft.copy.backpacks.network.MessageOpenGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

@ChestContainer
public abstract class ContainerBackpack extends Container {
   public static final String TAG_SIZE = "size";
   public static final String TAG_TITLE = "title";
   public static final String TAG_LOCALIZED = "localized";
   public final EntityPlayer player;
   public final IBackpack backpack;
   public final BackpackDataItems data;
   public final BackpackSize size;
   public final ItemStackHandler items;
   public final String title;
   public final boolean titleLocalized;

   public ContainerBackpack(EntityPlayer player, IBackpack backpack) {
      this.player = player;
      this.backpack = backpack;
      this.data = (BackpackDataItems)backpack.getData();
      this.size = this.data.getSize();
      this.items = this.data.getItems(player.world, player);
      ItemStack stack = backpack.getStack();
      this.title = stack.hasDisplayName() ? stack.getDisplayName() : "container.wearablebackpacks.backpack";
      this.titleLocalized = stack.hasDisplayName();
      this.setupSlots();
   }

   @SideOnly(Side.CLIENT)
   public ContainerBackpack(EntityPlayer player, NBTTagCompound data) {
      this.player = player;
      this.backpack = null;
      this.data = null;
      this.size = BackpackSize.parse(data.getTag("size"));
      this.items = new ItemStackHandler(this.size.getColumns() * this.size.getRows());
      this.title = data.getString("title");
      this.titleLocalized = data.getBoolean("localized");
      this.setupSlots();
   }

   public void open() {
      EntityPlayerMP player = (EntityPlayerMP)this.player;
      player.getNextWindowId();
      player.closeContainer();
      player.openContainer = this;
      this.windowId = player.currentWindowId;
      WearableBackpacks.CHANNEL.sendTo(MessageOpenGui.create(this), player);
      this.addListener(player);
      this.backpack.setPlayersUsing(this.backpack.getPlayersUsing() + 1);
   }

   public int getBorderTop() {
      return 17;
   }

   public int getBorderSide() {
      return 7;
   }

   public int getBorderBottom() {
      return 7;
   }

   public int getBufferInventory() {
      return 13;
   }

   public int getBufferHotbar() {
      return 4;
   }

   public int getMaxColumns() {
      return BackpackSize.MAX.getColumns();
   }

   public int getMaxRows() {
      return BackpackSize.MAX.getRows();
   }

   public int getWidth() {
      return Math.max(this.size.getColumns(), 9) * 18 + this.getBorderSide() * 2;
   }

   public int getHeight() {
      return this.getBorderTop() + this.size.getRows() * 18 + this.getBufferInventory() + 72 + this.getBufferHotbar() + this.getBorderBottom();
   }

   public int getContainerInvWidth() {
      return this.size.getColumns() * 18;
   }

   public int getContainerInvHeight() {
      return this.size.getRows() * 18;
   }

   public int getContainerInvXOffset() {
      return this.getBorderSide() + Math.max(0, (this.getPlayerInvWidth() - this.getContainerInvWidth()) / 2);
   }

   public int getPlayerInvWidth() {
      return 162;
   }

   public int getPlayerInvHeight() {
      return 72 + this.getBufferHotbar();
   }

   public int getPlayerInvXOffset() {
      return this.getBorderSide() + Math.max(0, (this.getContainerInvWidth() - this.getPlayerInvWidth()) / 2);
   }

   protected void setupSlots() {
      this.setupBackpackSlots();
      this.setupPlayerSlots();
   }

   protected void setupBackpackSlots() {
      int xOffset = 1 + this.getContainerInvXOffset();
      int yOffset = 1 + this.getBorderTop();

      for(int y = 0; y < this.size.getRows(); yOffset += 18) {
         for(int x = 0; x < this.size.getColumns(); ++x) {
            this.addSlotToContainer(new SlotItemHandler(this.items, x + y * this.size.getColumns(), xOffset + x * 18, yOffset));
         }

         ++y;
      }

   }

   protected void setupPlayerSlots() {
      int xOffset = 1 + this.getPlayerInvXOffset();
      int yOffset = 1 + this.getBorderTop() + this.getContainerInvHeight() + this.getBufferInventory();

      for(int y = 0; y < 3; yOffset += 18) {
         for(int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(this.player.inventory, x + y * 9 + 9, xOffset + x * 18, yOffset));
         }

         ++y;
      }

      yOffset = yOffset + this.getBufferHotbar();

      for(int x = 0; x < 9; ++x) {
         this.addSlotToContainer(new Slot(this.player.inventory, x, xOffset + x * 18, yOffset));
      }

   }

   public void writeToNBT(NBTTagCompound compound) {
      compound.setTag("size", this.size.serializeNBT());
      compound.setString("title", this.title);
      compound.setBoolean("localized", this.titleLocalized);
   }

   public abstract boolean canInteractWith(EntityPlayer var1);

   public void onContainerClosed(EntityPlayer player) {
      if (this.backpack != null) {
         this.backpack.setPlayersUsing(this.backpack.getPlayersUsing() - 1);
      }

   }

   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot == null) {
         return null;
      } else {
         ItemStack stack = slot.getStack();
         if (stack.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            ItemStack result = stack.copy();
            if (index < this.items.getSlots()) {
               if (!this.mergeItemStack(stack, this.items.getSlots(), this.inventorySlots.size(), true)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.mergeItemStack(stack, 0, this.items.getSlots(), false)) {
               return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
               slot.putStack(ItemStack.EMPTY);
            } else {
               slot.onSlotChanged();
            }

            return result;
         }
      }
   }

   @RowSizeCallback
   public int getRowSize() {
      return this.size.getColumns();
   }
}
