package reborncore.common.fluids;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import reborncore.common.tile.RebornMachineTile;

public class RebornFluidHandler implements IFluidHandler {
   private final RebornMachineTile tile;
   private final EnumFacing side;

   public RebornFluidHandler(RebornMachineTile tile, EnumFacing side) {
      this.tile = tile;
      this.side = side;
   }

   public IFluidTankProperties[] getTankProperties() {
      List<IFluidTankProperties> props = new ArrayList(1);
      RebornFluidTank tank = this.tile.getTank();
      if (tank != null && (tank.canFill(this.side) || tank.canDrain(this.side))) {
         props.add(tank.getTankProperties(this.side));
      }

      return (IFluidTankProperties[])props.toArray(new IFluidTankProperties[0]);
   }

   public int fill(FluidStack resource, boolean doFill) {
      if (resource != null && resource.amount > 0) {
         int total = 0;
         FluidStack missing = resource.copy();
         RebornFluidTank tank = this.tile.getTank();
         if (tank != null && tank.canFill(this.side)) {
            total += tank.fill(missing, doFill);
            missing.amount = resource.amount - total;
         }

         return total;
      } else {
         return 0;
      }
   }

   @Nullable
   public FluidStack drain(FluidStack resource, boolean doDrain) {
      if (resource != null && resource.amount > 0) {
         FluidStack ret = new FluidStack(resource.getFluid(), 0);
         RebornFluidTank tank = this.tile.getTank();
         if (tank != null && tank.canDrain(this.side)) {
            FluidStack inTank = tank.getFluid();
            if (inTank != null && inTank.getFluid() == resource.getFluid()) {
               FluidStack add = tank.drain(resource.amount - ret.amount, doDrain);
               if (add != null) {
                  ret.amount += add.amount;
               }
            }
         }

         return ret.amount == 0 ? null : ret;
      } else {
         return null;
      }
   }

   @Nullable
   public FluidStack drain(int maxDrain, boolean doDrain) {
      RebornFluidTank tank = this.tile.getTank();
      if (tank != null) {
         if (!tank.canDrain(this.side)) {
            return null;
         }

         FluidStack stack = tank.drain(maxDrain, false);
         if (stack != null) {
            stack.amount = maxDrain;
            return this.drain(stack, doDrain);
         }
      }

      return null;
   }
}
