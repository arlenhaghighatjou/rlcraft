package rustic.common.inventory;

import com.google.common.base.Objects;
import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import rustic.common.blocks.BlockCabinet;
import rustic.common.tileentity.TileEntityCabinet;

public class DoubleCabinetItemHandler extends WeakReference<TileEntityCabinet> implements IItemHandlerModifiable {
   public static final DoubleCabinetItemHandler NO_ADJACENT_CABINETS_INSTANCE = new DoubleCabinetItemHandler((TileEntityCabinet)null, (TileEntityCabinet)null, false);
   private final boolean mainCabinetIsUpper;
   private final TileEntityCabinet mainCabinet;
   private final int hashCode;

   public DoubleCabinetItemHandler(@Nullable TileEntityCabinet mainCabinet, @Nullable TileEntityCabinet other, boolean mainCabinetIsUpper) {
      super(other);
      this.mainCabinet = mainCabinet;
      this.mainCabinetIsUpper = mainCabinetIsUpper;
      this.hashCode = Objects.hashCode(new Object[]{mainCabinetIsUpper ? mainCabinet : other}) * 31 + Objects.hashCode(new Object[]{!mainCabinetIsUpper ? mainCabinet : other});
   }

   @Nullable
   public static DoubleCabinetItemHandler get(TileEntityCabinet cabinet) {
      World world = cabinet.getWorld();
      BlockPos pos = cabinet.getPos();
      if (world != null && pos != null && world.isBlockLoaded(pos)) {
         Block blockType = cabinet.getBlockType();
         if (((Boolean)world.getBlockState(pos).getValue(BlockCabinet.TOP)).booleanValue()) {
            BlockPos blockpos = pos.down();
            Block block = world.getBlockState(blockpos).getBlock();
            if (block == blockType) {
               TileEntity otherTE = world.getTileEntity(blockpos);
               if (otherTE instanceof TileEntityCabinet) {
                  TileEntityCabinet otherCabinet = (TileEntityCabinet)otherTE;
                  return new DoubleCabinetItemHandler(cabinet, otherCabinet, true);
               }
            }
         } else if (((Boolean)world.getBlockState(pos).getBlock().getActualState(world.getBlockState(pos), world, pos).getValue(BlockCabinet.BOTTOM)).booleanValue()) {
            BlockPos blockpos = pos.up();
            Block block = world.getBlockState(blockpos).getBlock();
            if (block == blockType) {
               TileEntity otherTE = world.getTileEntity(blockpos);
               if (otherTE instanceof TileEntityCabinet) {
                  TileEntityCabinet otherCabinet = (TileEntityCabinet)otherTE;
                  return new DoubleCabinetItemHandler(cabinet, otherCabinet, false);
               }
            }
         }

         return NO_ADJACENT_CABINETS_INSTANCE;
      } else {
         return null;
      }
   }

   @Nullable
   public TileEntityCabinet getCabinet(boolean accessingUpper) {
      return accessingUpper == this.mainCabinetIsUpper ? this.mainCabinet : this.getOtherCabinet();
   }

   @Nullable
   public TileEntityCabinet getOtherCabinet() {
      TileEntityCabinet tileEntityCabinet = (TileEntityCabinet)this.get();
      return tileEntityCabinet != null && !tileEntityCabinet.isInvalid() ? tileEntityCabinet : null;
   }

   public int getSlots() {
      return 54;
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      boolean accessingUpperCabinet = slot < 27;
      int targetSlot = accessingUpperCabinet ? slot : slot - 27;
      TileEntityCabinet cabinet = this.getCabinet(accessingUpperCabinet);
      return cabinet != null ? cabinet.getStackInSlot(targetSlot) : ItemStack.EMPTY;
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      boolean accessingUpperCabinet = slot < 27;
      int targetSlot = accessingUpperCabinet ? slot : slot - 27;
      TileEntityCabinet cabinet = this.getCabinet(accessingUpperCabinet);
      if (cabinet != null) {
         cabinet.setInventorySlotContents(targetSlot, stack);
      }

   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      boolean accessingUpperCabinet = slot < 27;
      int targetSlot = accessingUpperCabinet ? slot : slot - 27;
      TileEntityCabinet cabinet = this.getCabinet(accessingUpperCabinet);
      return cabinet != null ? cabinet.getSingleCabinetHandler().insertItem(targetSlot, stack, simulate) : stack;
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      boolean accessingUpperCabinet = slot < 27;
      int targetSlot = accessingUpperCabinet ? slot : slot - 27;
      TileEntityCabinet cabinet = this.getCabinet(accessingUpperCabinet);
      return cabinet != null ? cabinet.getSingleCabinetHandler().extractItem(targetSlot, amount, simulate) : ItemStack.EMPTY;
   }

   public int getSlotLimit(int slot) {
      boolean accessingUpperCabinet = slot < 27;
      return this.getCabinet(accessingUpperCabinet).getInventoryStackLimit();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DoubleCabinetItemHandler that = (DoubleCabinetItemHandler)o;
         if (this.hashCode != that.hashCode) {
            return false;
         } else {
            TileEntityCabinet otherCabinet = this.getOtherCabinet();
            if (this.mainCabinetIsUpper == that.mainCabinetIsUpper) {
               return Objects.equal(this.mainCabinet, that.mainCabinet) && Objects.equal(otherCabinet, that.getOtherCabinet());
            } else {
               return Objects.equal(this.mainCabinet, that.getOtherCabinet()) && Objects.equal(otherCabinet, that.mainCabinet);
            }
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.hashCode;
   }

   public boolean needsRefresh() {
      if (this == NO_ADJACENT_CABINETS_INSTANCE) {
         return false;
      } else {
         TileEntityCabinet tileEntityCabinet = (TileEntityCabinet)this.get();
         return tileEntityCabinet == null || tileEntityCabinet.isInvalid();
      }
   }
}
