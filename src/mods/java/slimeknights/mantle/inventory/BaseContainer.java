package slimeknights.mantle.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import slimeknights.mantle.util.SlimeknightException;

public abstract class BaseContainer<T extends TileEntity> extends Container {
   protected double maxDist;
   protected T tile;
   protected final Block originalBlock;
   protected final BlockPos pos;
   protected final World world;
   protected final IItemHandler itemHandler;
   public List<Container> subContainers;
   protected int playerInventoryStart;

   public BaseContainer(T tile) {
      this(tile, (EnumFacing)null);
   }

   public BaseContainer(T tile, EnumFacing invDir) {
      this.maxDist = 64.0D;
      this.subContainers = Lists.newArrayList();
      this.playerInventoryStart = -1;
      this.tile = tile;
      if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, invDir)) {
         this.itemHandler = (IItemHandler)tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, invDir);
      } else {
         this.itemHandler = new EmptyHandler();
      }

      this.world = tile.getWorld();
      this.pos = tile.getPos();
      this.originalBlock = this.world.getBlockState(this.pos).getBlock();
   }

   public void syncOnOpen(EntityPlayerMP playerOpened) {
      WorldServer server = playerOpened.getServerWorld();

      for(EntityPlayer player : server.playerEntities) {
         if (player != playerOpened && player.openContainer instanceof BaseContainer && this.sameGui((BaseContainer)player.openContainer)) {
            this.syncWithOtherContainer((BaseContainer)player.openContainer, playerOpened);
            return;
         }
      }

      this.syncNewContainer(playerOpened);
   }

   public T getTile() {
      return this.tile;
   }

   public IItemHandler getItemHandler() {
      return this.itemHandler;
   }

   protected void syncWithOtherContainer(BaseContainer<T> otherContainer, EntityPlayerMP player) {
   }

   protected void syncNewContainer(EntityPlayerMP player) {
   }

   public boolean sameGui(BaseContainer otherContainer) {
      return this.tile == otherContainer.tile;
   }

   public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
      Block block = this.world.getBlockState(this.pos).getBlock();
      if (block != Blocks.AIR && block == this.originalBlock) {
         return playerIn.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= this.maxDist;
      } else {
         return false;
      }
   }

   @Nonnull
   public NonNullList<ItemStack> getInventory() {
      return super.getInventory();
   }

   public String getInventoryDisplayName() {
      IWorldNameable nameable = null;
      if (this.itemHandler instanceof InvWrapper) {
         nameable = ((InvWrapper)this.itemHandler).getInv();
         if (nameable.getDisplayName() == null) {
            nameable = null;
         }
      }

      if (nameable == null && this.tile instanceof IWorldNameable) {
         nameable = (IWorldNameable)this.tile;
      }

      if (nameable != null) {
         ITextComponent textName = nameable.getDisplayName();
         return textName != null ? textName.getFormattedText() : nameable.getName();
      } else {
         return null;
      }
   }

   protected void addPlayerInventory(InventoryPlayer playerInventory, int xCorner, int yCorner) {
      int index = 9;
      int start = this.inventorySlots.size();

      for(int row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInventory, index, xCorner + col * 18, yCorner + row * 18));
            ++index;
         }
      }

      index = 0;

      for(int col = 0; col < 9; ++col) {
         this.addSlotToContainer(new Slot(playerInventory, index, xCorner + col * 18, yCorner + 58));
         ++index;
      }

      this.playerInventoryStart = start;
   }

   @Nonnull
   protected Slot addSlotToContainer(Slot slotIn) {
      if (this.playerInventoryStart >= 0) {
         throw new SlimeknightException("BaseContainer: Player inventory has to be last slots. Add all slots before adding the player inventory.");
      } else {
         return super.addSlotToContainer(slotIn);
      }
   }

   @Nonnull
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      if (this.playerInventoryStart < 0) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemstack = ItemStack.EMPTY;
         Slot slot = (Slot)this.inventorySlots.get(index);
         if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            int end = this.inventorySlots.size();
            if (index < this.playerInventoryStart) {
               if (!this.mergeItemStack(itemstack1, this.playerInventoryStart, end, true)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.mergeItemStack(itemstack1, 0, this.playerInventoryStart, false)) {
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

   protected boolean mergeItemStack(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
      boolean ret = this.mergeItemStackRefill(stack, startIndex, endIndex, useEndIndex);
      if (!stack.isEmpty() && stack.getCount() > 0) {
         ret |= this.mergeItemStackMove(stack, startIndex, endIndex, useEndIndex);
      }

      return ret;
   }

   protected boolean mergeItemStackRefill(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
      if (stack.getCount() <= 0) {
         return false;
      } else {
         boolean flag1 = false;
         int k = startIndex;
         if (useEndIndex) {
            k = endIndex - 1;
         }

         if (stack.isStackable()) {
            while(stack.getCount() > 0 && (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex)) {
               Slot slot = (Slot)this.inventorySlots.get(k);
               ItemStack itemstack1 = slot.getStack();
               if (!itemstack1.isEmpty() && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack1.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack1) && this.canMergeSlot(stack, slot)) {
                  int l = itemstack1.getCount() + stack.getCount();
                  int limit = Math.min(stack.getMaxStackSize(), slot.getItemStackLimit(stack));
                  if (l <= limit) {
                     stack.setCount(0);
                     itemstack1.setCount(l);
                     slot.onSlotChanged();
                     flag1 = true;
                  } else if (itemstack1.getCount() < limit) {
                     stack.shrink(limit - itemstack1.getCount());
                     itemstack1.setCount(limit);
                     slot.onSlotChanged();
                     flag1 = true;
                  }
               }

               if (useEndIndex) {
                  --k;
               } else {
                  ++k;
               }
            }
         }

         return flag1;
      }
   }

   protected boolean mergeItemStackMove(@Nonnull ItemStack stack, int startIndex, int endIndex, boolean useEndIndex) {
      if (stack.getCount() <= 0) {
         return false;
      } else {
         boolean flag1 = false;
         int k;
         if (useEndIndex) {
            k = endIndex - 1;
         } else {
            k = startIndex;
         }

         while(!useEndIndex && k < endIndex || useEndIndex && k >= startIndex) {
            Slot slot = (Slot)this.inventorySlots.get(k);
            ItemStack itemstack1 = slot.getStack();
            if (itemstack1.isEmpty() && slot.isItemValid(stack) && this.canMergeSlot(stack, slot)) {
               int limit = slot.getItemStackLimit(stack);
               ItemStack stack2 = stack.copy();
               if (stack2.getCount() > limit) {
                  stack2.setCount(limit);
                  stack.shrink(limit);
               } else {
                  stack.setCount(0);
               }

               slot.putStack(stack2);
               slot.onSlotChanged();
               flag1 = true;
               if (stack.isEmpty()) {
                  break;
               }
            }

            if (useEndIndex) {
               --k;
            } else {
               ++k;
            }
         }

         return flag1;
      }
   }
}
