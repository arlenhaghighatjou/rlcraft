package vazkii.quark.oddities.inventory;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import vazkii.quark.oddities.tile.TileMatrixEnchanter;

public class ContainerMatrixEnchanting extends Container {
   private final List<ItemStack> lapisTypes = OreDictionary.getOres("gemLapis");
   public final TileMatrixEnchanter enchanter;

   public ContainerMatrixEnchanting(InventoryPlayer playerInv, TileMatrixEnchanter tile) {
      this.enchanter = tile;
      this.addSlotToContainer(new Slot(tile, 0, 15, 20) {
         public int getSlotStackLimit() {
            return 1;
         }
      });
      this.addSlotToContainer(new Slot(tile, 1, 15, 44) {
         public boolean isItemValid(ItemStack stack) {
            return ContainerMatrixEnchanting.this.isLapis(stack);
         }
      });
      this.addSlotToContainer(new Slot(tile, 2, 59, 32) {
         public boolean isItemValid(ItemStack stack) {
            return false;
         }

         @Nonnull
         public ItemStack onTake(EntityPlayer thePlayer, @Nonnull ItemStack stack) {
            ContainerMatrixEnchanting.this.finish(thePlayer, stack);
            return super.onTake(thePlayer, stack);
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
      }

   }

   private boolean isLapis(ItemStack stack) {
      for(ItemStack ore : this.lapisTypes) {
         if (OreDictionary.itemMatches(ore, stack, false)) {
            return true;
         }
      }

      return false;
   }

   private void finish(EntityPlayer player, ItemStack stack) {
      this.enchanter.setInventorySlotContents(0, ItemStack.EMPTY);
      player.addStat(StatList.ITEM_ENCHANTED);
      if (player instanceof EntityPlayerMP) {
         CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP)player, stack, 1);
      }

      player.world.playSound((EntityPlayer)null, this.enchanter.getPos(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, player.world.rand.nextFloat() * 0.1F + 0.9F);
   }

   public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
      World world = this.enchanter.getWorld();
      BlockPos pos = this.enchanter.getPos();
      if (world.getBlockState(pos).getBlock() != Blocks.ENCHANTING_TABLE) {
         return false;
      } else {
         return playerIn.getDistanceSq((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
      }
   }

   @Nonnull
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack originalStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stackInSlot = slot.getStack();
         originalStack = stackInSlot.copy();
         if (index < 3) {
            if (!this.mergeItemStack(stackInSlot, 3, 39, true)) {
               return ItemStack.EMPTY;
            }
         } else if (this.isLapis(stackInSlot)) {
            if (!this.mergeItemStack(stackInSlot, 1, 2, true)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (((Slot)this.inventorySlots.get(0)).getHasStack() || !((Slot)this.inventorySlots.get(0)).isItemValid(stackInSlot)) {
               return ItemStack.EMPTY;
            }

            if (stackInSlot.hasTagCompound()) {
               ((Slot)this.inventorySlots.get(0)).putStack(stackInSlot.splitStack(1));
            } else if (!stackInSlot.isEmpty()) {
               ((Slot)this.inventorySlots.get(0)).putStack(new ItemStack(stackInSlot.getItem(), 1, stackInSlot.getMetadata()));
               stackInSlot.shrink(1);
            }
         }

         if (stackInSlot.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (stackInSlot.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(playerIn, stackInSlot);
      }

      return originalStack;
   }
}
