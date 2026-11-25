package com.endertech.minecraft.forge.data;

import com.endertech.minecraft.forge.ForgeGui;
import com.endertech.minecraft.forge.ForgeMod;
import java.awt.Dimension;
import javax.annotation.Nullable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TileInventory extends ItemStackHandler {
   public final TileEntity tile;

   public TileInventory(TileEntity tile, int size) {
      super(size);
      this.tile = tile;
   }

   protected void onContentsChanged(int slot) {
      this.tile.markDirty();
   }

   @SideOnly(Side.CLIENT)
   public static class ClientContainer extends GuiContainer {
      public final TileInventory.ServerContainer serverContainer;
      public final ForgeGui.GuiTexture texture;

      public ClientContainer(TileInventory.ServerContainer container, ForgeGui.GuiTexture texture) {
         super(container);
         this.serverContainer = container;
         this.texture = texture;
         this.xSize = texture.size.width;
         this.ySize = texture.size.height;
      }

      protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
         this.mc.getTextureManager().bindTexture(this.texture.path);
         this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      }

      public void drawScreen(int mouseX, int mouseY, float partialTicks) {
         super.drawDefaultBackground();
         super.drawScreen(mouseX, mouseY, partialTicks);
         super.renderHoveredToolTip(mouseX, mouseY);
      }
   }

   public abstract static class GuiHandler implements IGuiHandler {
      public GuiHandler(ForgeMod mod) {
         mod.getRegistrator().addGuiHandler(this);
      }

      public abstract boolean isValidTile(TileEntity var1);

      public abstract TileInventory.ServerContainer createServerContainer(World var1, BlockPos var2, TileEntity var3, EntityPlayer var4);

      @SideOnly(Side.CLIENT)
      public abstract GuiContainer createClientContainer(World var1, BlockPos var2, TileEntity var3, EntityPlayer var4);

      public static void openContainerFor(ForgeMod mod, World world, BlockPos pos, EntityPlayer player) {
         if (!player.isSneaking() && !world.isRemote) {
            player.openGui(mod, 0, world, pos.getX(), pos.getY(), pos.getZ());
         }

      }

      @Nullable
      public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
         BlockPos pos = new BlockPos(x, y, z);
         TileEntity tile = world.getTileEntity(pos);
         return tile != null && this.isValidTile(tile) ? this.createServerContainer(world, pos, tile, player) : null;
      }

      @Nullable
      public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
         BlockPos pos = new BlockPos(x, y, z);
         TileEntity tile = world.getTileEntity(pos);
         return tile != null && this.isValidTile(tile) ? this.createClientContainer(world, pos, tile, player) : null;
      }
   }

   public static class ItemSlot extends SlotItemHandler {
      public final TileInventory tileInventory;

      public ItemSlot(TileInventory tileInventory, int index, int xPosition, int yPosition) {
         super(tileInventory, index, xPosition, yPosition);
         this.tileInventory = tileInventory;
      }
   }

   public static class ServerContainer extends Container {
      public static final Dimension PLAYER_INVENTORY = new Dimension(9, 3);
      public static final Dimension SLOT_SIZE = new Dimension(18, 18);
      public final TileInventory tileInventory;

      public ServerContainer(TileInventory tileInventory) {
         this.tileInventory = tileInventory;
      }

      public TileInventory.ServerContainer addPlayerSlots(InventoryPlayer playerInventory, int startX, int startY) {
         for(int row = 0; row < PLAYER_INVENTORY.height; ++row) {
            for(int col = 0; col < PLAYER_INVENTORY.width; ++col) {
               int x = startX + col * this.slotSize().width;
               int y = startY + row * this.slotSize().height;
               this.addSlotToContainer(new Slot(playerInventory, PLAYER_INVENTORY.width + col + row * PLAYER_INVENTORY.width, x, y));
            }
         }

         for(int col = 0; col < PLAYER_INVENTORY.width; ++col) {
            int x = startX + col * this.slotSize().width;
            int y = startY + 58;
            this.addSlotToContainer(new Slot(playerInventory, col, x, y));
         }

         return this;
      }

      public Dimension slotSize() {
         return SLOT_SIZE;
      }

      public TileInventory.ItemSlot addSlot(TileInventory.ItemSlot slot) {
         this.addSlotToContainer(slot);
         return slot;
      }

      public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
         ItemStack itemstack = ItemStack.EMPTY;
         Slot slot = (Slot)this.inventorySlots.get(index);
         if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            int size = this.tileInventory.getSlots();
            if (index < size) {
               if (!this.mergeItemStack(itemstack1, size, this.inventorySlots.size(), true)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.mergeItemStack(itemstack1, 0, size, false)) {
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

      public boolean canInteractWith(EntityPlayer player) {
         return player.getDistanceSq(this.tileInventory.tile.getPos()) < 64.0D;
      }
   }
}
