package reborncore.common.fluids;

import java.util.function.Predicate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.RebornMachineTile;

public class RebornFluidTank extends FluidTank {
   protected final String identifier;
   protected final Predicate<Fluid> acceptedFluids;

   public RebornFluidTank(String identifier, int capacity, TileEntity tile) {
      this(identifier, capacity, tile, (fluid) -> {
         return true;
      });
   }

   public RebornFluidTank(String identifier, int capacity, TileEntity tile, Predicate<Fluid> acceptedFluids) {
      super(capacity);
      this.identifier = identifier;
      this.tile = tile;
      this.acceptedFluids = acceptedFluids;
   }

   public boolean canFillFluidType(FluidStack fluid) {
      return fluid != null && this.acceptsFluid(fluid.getFluid());
   }

   public boolean canDrainFluidType(FluidStack fluid) {
      return fluid != null && this.acceptsFluid(fluid.getFluid());
   }

   public boolean isEmpty() {
      return this.getFluid() == null || this.getFluid().amount <= 0;
   }

   public boolean isFull() {
      return this.getFluid() != null && this.getFluid().amount >= this.getCapacity();
   }

   public Fluid getFluidType() {
      return this.getFluid() != null ? this.getFluid().getFluid() : null;
   }

   public void setFluidAmount(int amount) {
      if (this.fluid != null) {
         this.fluid.amount = amount;
      }

   }

   public boolean acceptsFluid(Fluid fluid) {
      return this.acceptedFluids.test(fluid);
   }

   public boolean canFill(EnumFacing side) {
      if (this.tile instanceof RebornMachineTile) {
         RebornMachineTile machine = (RebornMachineTile)this.tile;
         if (machine.fluidConfiguration != null) {
            FluidConfiguration.FluidConfig fluidConfig = machine.fluidConfiguration.getSideDetail(side);
            return fluidConfig == null ? super.canFill() : fluidConfig.getIoConfig().isInsert();
         }
      }

      return this.canFill();
   }

   public boolean canDrain(EnumFacing side) {
      if (this.tile instanceof RebornMachineTile) {
         RebornMachineTile machine = (RebornMachineTile)this.tile;
         if (machine.fluidConfiguration != null) {
            FluidConfiguration.FluidConfig fluidConfig = machine.fluidConfiguration.getSideDetail(side);
            return fluidConfig == null ? super.canDrain() : fluidConfig.getIoConfig().isExtact();
         }
      }

      return this.canDrain();
   }

   public IFluidTankProperties getTankProperties(final EnumFacing side) {
      return new IFluidTankProperties() {
         public FluidStack getContents() {
            return RebornFluidTank.this.getFluid();
         }

         public int getCapacity() {
            return RebornFluidTank.this.capacity;
         }

         public boolean canFillFluidType(FluidStack fluidStack) {
            return fluidStack != null && fluidStack.amount > 0 && RebornFluidTank.this.acceptsFluid(fluidStack.getFluid()) && (side == null || RebornFluidTank.this.canFill(side));
         }

         public boolean canFill() {
            return RebornFluidTank.this.canFill(side);
         }

         public boolean canDrainFluidType(FluidStack fluidStack) {
            return fluidStack != null && fluidStack.amount > 0 && RebornFluidTank.this.acceptsFluid(fluidStack.getFluid()) && (side == null || RebornFluidTank.this.canDrain(side));
         }

         public boolean canDrain() {
            return RebornFluidTank.this.canDrain(side);
         }
      };
   }
}
