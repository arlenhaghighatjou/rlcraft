package pl.asie.foamfix.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityFasterHopper extends TileEntityHopper {
   private boolean empty;
   private boolean full;
   private boolean dirtyEmpty;
   private boolean dirtyFull;

   public TileEntityFasterHopper() {
   }

   public void validate() {
      super.validate();
      this.dirtyEmpty = true;
      this.dirtyFull = true;
   }

   public void updateFlagEmpty() {
      this.empty = true;

      for(ItemStack stack : this.getItems()) {
         if (!stack.isEmpty()) {
            this.empty = false;
            return;
         }
      }

   }

   public void updateFlagFull() {
      this.full = true;

      for(ItemStack stack : this.getItems()) {
         if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
            this.full = false;
            return;
         }
      }

   }

   public ItemStack removeStackFromSlot(int index) {
      ItemStack stack = super.removeStackFromSlot(index);
      if (!stack.isEmpty()) {
         this.dirtyEmpty = true;
         this.full = false;
      }

      return stack;
   }

   public ItemStack decrStackSize(int index, int count) {
      ItemStack stack = super.decrStackSize(index, count);
      if (!stack.isEmpty()) {
         this.dirtyEmpty = true;
         this.full = false;
      }

      return stack;
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      super.setInventorySlotContents(index, stack);
      this.dirtyEmpty = true;
      this.dirtyFull = true;
   }

   protected boolean isInventoryEmpty() {
      if (this.dirtyEmpty) {
         this.updateFlagEmpty();
         this.dirtyEmpty = false;
      }

      return this.empty;
   }

   protected boolean isFull() {
      if (this.dirtyFull) {
         this.updateFlagFull();
         this.dirtyFull = false;
      }

      return this.full;
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
      return oldState.getBlock() != newState.getBlock();
   }
}
