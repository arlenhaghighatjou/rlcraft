package net.silentchaos512.lib.tile;

import net.minecraft.nbt.NBTTagCompound;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.silentchaos512.lib.util.InventoryUtils;
import net.silentchaos512.lib.util.MathUtils;

public abstract class TileEntityItemProcessor extends TileSidedInventorySL implements ITickable {
   private static final int LONG_UPDATE_DELAY = 20;
   @SyncVariable(
      name = "Progress"
   )
   private int progress;
   @SyncVariable(
      name = "Burner"
   )
   @Nullable
   private IFuelBurner burner;
   private NonNullList<ItemStack> inputs;
   private NonNullList<ItemStack> fuels;
   private NonNullList<ItemStack> outputs;
   private Collection<ItemStack> overflow;
   private int timer;

   protected void readSyncVars(NBTTagCompound tags) {
      this.progress = tags.getInteger("Progress");
   }

   protected NBTTagCompound writeSyncVars(NBTTagCompound tags, SyncVariable.Type syncType) {
      tags.setInteger("Progress", this.progress);
      return tags;
   }

   public TileEntityItemProcessor(int inputCount, int fuelCount, int outputCount) {
      this(inputCount, fuelCount, outputCount, new FurnaceFuelBurner(FurnaceFuelBurner.BurnCondition.STANDARD));
   }

   public TileEntityItemProcessor(int inputCount, int fuelCount, int outputCount, @Nullable IFuelBurner burner) {
      this.progress = 0;
      this.overflow = ImmutableList.of();
      this.timer = MathUtils.nextInt(20);
      if (inputCount < 1) {
         throw new IllegalArgumentException("Input slot count must be greater than zero");
      } else if (outputCount < 1) {
         throw new IllegalArgumentException("Output slot count must be greater than zero");
      } else {
         this.inputs = NonNullList.withSize(inputCount, ItemStack.EMPTY);
         this.fuels = NonNullList.withSize(fuelCount, ItemStack.EMPTY);
         this.outputs = NonNullList.withSize(outputCount, ItemStack.EMPTY);
         this.burner = burner;
      }
   }

   protected abstract int getWorkTime(Collection<ItemStack> var1);

   protected abstract boolean canWork(Collection<ItemStack> var1);

   protected abstract Collection<ItemStack> getWorkResult(Collection<ItemStack> var1);

   protected abstract void consumeIngredients(Collection<ItemStack> var1);

   protected abstract boolean isInputItem(ItemStack var1);

   protected abstract boolean isFuelItem(ItemStack var1);

   private boolean hasSpaceForOutputs() {
      return this.overflow.isEmpty();
   }

   private ItemStack getFuelStack() {
      int start = this.getInputSlotCount();
      return this.getStackFromSlotRange(start, start + this.getFuelSlotCount());
   }

   /** @deprecated */
   @Deprecated
   private ItemStack getStackFromSlotRange(int startInclusive, int endExclusive) {
      for(int i = startInclusive; i < endExclusive; ++i) {
         ItemStack stack = this.getStackInSlot(i);
         if (!stack.isEmpty()) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   protected boolean doWork() {
      boolean requiresClientSync = false;
      boolean processing = false;
      if (!this.inputs.isEmpty() && this.canWork(this.inputs) && this.hasSpaceForOutputs() && (this.burner == null || this.burner.hasFuel())) {
         int workTime = this.getWorkTime(this.inputs);
         if (this.progress < workTime) {
            ++this.progress;
            requiresClientSync = true;
            processing = true;
         }

         if (this.progress >= workTime) {
            Collection<ItemStack> result = this.getWorkResult(this.inputs);
            int slotStart = this.getInputSlotCount() + this.getFuelSlotCount();
            result = InventoryUtils.mergeItems(this, slotStart, slotStart + this.getOutputSlotCount(), result);
            if (!result.isEmpty()) {
               this.overflow = result;
            }

            this.progress = 0;
            requiresClientSync = true;
         }
      }

      if (this.burner != null && this.burner.tickFuel(processing) && !this.burner.hasFuel()) {
         ItemStack fuel = this.getFuelStack();
         if (!fuel.isEmpty()) {
            this.burner.feedFuel(fuel);
            fuel.shrink(1);
         }
      }

      return requiresClientSync;
   }

   public void update() {
      ++this.timer;
      if (!this.world.isRemote) {
         boolean requiresClientSync = this.doWork();
         if (this.timer % 20 == 0 && !this.overflow.isEmpty()) {
            int oldCount = this.overflow.size();
            int start = this.getInputSlotCount() + this.getFuelSlotCount();
            this.overflow = InventoryUtils.mergeItems(this, start, start + this.getOutputSlotCount(), this.overflow);
            requiresClientSync |= oldCount != this.overflow.size();
         }

         if (requiresClientSync) {
            IBlockState state = this.world.getBlockState(this.pos);
            this.world.notifyBlockUpdate(this.pos, state, state, 3);
         }
      }

   }

   private int getInputSlotCount() {
      return this.inputs.size();
   }

   private int getFuelSlotCount() {
      return this.fuels.size();
   }

   private int getOutputSlotCount() {
      return this.outputs.size();
   }

   public int getSizeInventory() {
      return this.getInputSlotCount() + this.getFuelSlotCount() + this.getOutputSlotCount();
   }

   public ItemStack decrStackSize(int index, int count) {
      if (index >= this.getSizeInventory()) {
         return ItemStack.EMPTY;
      } else if (index >= this.getInputSlotCount() + this.getFuelSlotCount()) {
         return ItemStackHelper.getAndSplit(this.outputs, index - (this.getInputSlotCount() + this.getFuelSlotCount()), count);
      } else {
         return index >= this.getInputSlotCount() && this.getFuelSlotCount() > 0 ? ItemStackHelper.getAndSplit(this.fuels, index - this.getInputSlotCount(), count) : ItemStackHelper.getAndSplit(this.inputs, index, count);
      }
   }

   public ItemStack getStackInSlot(int index) {
      if (index >= this.getSizeInventory()) {
         return ItemStack.EMPTY;
      } else if (index >= this.getInputSlotCount() + this.getFuelSlotCount()) {
         return (ItemStack)this.outputs.get(index - (this.getInputSlotCount() + this.getFuelSlotCount()));
      } else {
         return index >= this.getInputSlotCount() && this.getFuelSlotCount() > 0 ? (ItemStack)this.fuels.get(index - this.getInputSlotCount()) : (ItemStack)this.inputs.get(index);
      }
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      if (index >= this.getInputSlotCount() + this.getFuelSlotCount()) {
         return false;
      } else {
         return index >= this.getInputSlotCount() && this.getFuelSlotCount() > 0 ? this.isFuelItem(stack) : this.isInputItem(stack);
      }
   }

   public ItemStack removeStackFromSlot(int index) {
      if (index >= this.getSizeInventory()) {
         return ItemStack.EMPTY;
      } else if (index >= this.getInputSlotCount() + this.getFuelSlotCount()) {
         return ItemStackHelper.getAndRemove(this.outputs, index - (this.getInputSlotCount() + this.getFuelSlotCount()));
      } else {
         return index >= this.getInputSlotCount() && this.getFuelSlotCount() > 0 ? ItemStackHelper.getAndRemove(this.fuels, index - this.getInputSlotCount()) : ItemStackHelper.getAndRemove(this.inputs, index);
      }
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      if (index < this.getSizeInventory()) {
         if (index >= this.getInputSlotCount() + this.getFuelSlotCount()) {
            this.outputs.set(index - (this.getInputSlotCount() + this.getFuelSlotCount()), stack);
         } else if (index >= this.getInputSlotCount() && this.getFuelSlotCount() > 0) {
            this.fuels.set(index - this.getInputSlotCount(), stack);
         } else {
            this.inputs.set(index, stack);
         }

      }
   }
}
