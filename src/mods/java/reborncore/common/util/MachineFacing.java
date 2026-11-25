package reborncore.common.util;

import net.minecraft.util.EnumFacing;
import reborncore.common.tile.RebornMachineTile;

public enum MachineFacing {
   FRONT,
   BACK,
   UP,
   DOWN,
   LEFT,
   RIGHT;

   private MachineFacing() {
   }

   public EnumFacing getFacing(RebornMachineTile machineBase) {
      if (this == FRONT) {
         return machineBase.getFacing();
      } else if (this == BACK) {
         return machineBase.getFacing().getOpposite();
      } else if (this == RIGHT) {
         int i = machineBase.getFacing().getOpposite().getHorizontalIndex() + 1;
         if (i > 3) {
            i = 0;
         }

         if (i < 0) {
            i = 3;
         }

         return EnumFacing.HORIZONTALS[i];
      } else if (this == LEFT) {
         int i = machineBase.getFacing().getOpposite().getHorizontalIndex() - 1;
         if (i > 3) {
            i = 0;
         }

         if (i < 0) {
            i = 3;
         }

         return EnumFacing.HORIZONTALS[i];
      } else if (this == UP) {
         return EnumFacing.UP;
      } else {
         return this == DOWN ? EnumFacing.DOWN : EnumFacing.NORTH;
      }
   }
}
