package reborncore.common.multiblock.rectangular;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import reborncore.common.multiblock.MultiblockControllerBase;
import reborncore.common.multiblock.MultiblockTileEntityBase;
import reborncore.common.multiblock.MultiblockValidationException;

public abstract class RectangularMultiblockTileEntityBase extends MultiblockTileEntityBase {
   PartPosition position = PartPosition.Unknown;
   EnumFacing outwards = null;

   public RectangularMultiblockTileEntityBase() {
   }

   public EnumFacing getOutwardsDir() {
      return this.outwards;
   }

   public PartPosition getPartPosition() {
      return this.position;
   }

   public void onAttached(MultiblockControllerBase newController) {
      super.onAttached(newController);
      this.recalculateOutwardsDirection(newController.getMinimumCoord(), newController.getMaximumCoord());
   }

   public void onMachineAssembled(MultiblockControllerBase controller) {
      BlockPos maxCoord = controller.getMaximumCoord();
      BlockPos minCoord = controller.getMinimumCoord();
      this.recalculateOutwardsDirection(minCoord, maxCoord);
   }

   public void onMachineBroken() {
      this.position = PartPosition.Unknown;
      this.outwards = null;
   }

   public void recalculateOutwardsDirection(BlockPos minCoord, BlockPos maxCoord) {
      this.outwards = null;
      this.position = PartPosition.Unknown;
      int facesMatching = 0;
      if (maxCoord.getX() == this.getPos().getX() || minCoord.getX() == this.getPos().getX()) {
         ++facesMatching;
      }

      if (maxCoord.getY() == this.getPos().getY() || minCoord.getY() == this.getPos().getY()) {
         ++facesMatching;
      }

      if (maxCoord.getZ() == this.getPos().getZ() || minCoord.getZ() == this.getPos().getZ()) {
         ++facesMatching;
      }

      if (facesMatching <= 0) {
         this.position = PartPosition.Interior;
      } else if (facesMatching >= 3) {
         this.position = PartPosition.FrameCorner;
      } else if (facesMatching == 2) {
         this.position = PartPosition.Frame;
      } else if (maxCoord.getX() == this.getPos().getX()) {
         this.position = PartPosition.EastFace;
         this.outwards = EnumFacing.EAST;
      } else if (minCoord.getX() == this.getPos().getX()) {
         this.position = PartPosition.WestFace;
         this.outwards = EnumFacing.WEST;
      } else if (maxCoord.getZ() == this.getPos().getZ()) {
         this.position = PartPosition.SouthFace;
         this.outwards = EnumFacing.SOUTH;
      } else if (minCoord.getZ() == this.getPos().getZ()) {
         this.position = PartPosition.NorthFace;
         this.outwards = EnumFacing.NORTH;
      } else if (maxCoord.getY() == this.getPos().getY()) {
         this.position = PartPosition.TopFace;
         this.outwards = EnumFacing.UP;
      } else {
         this.position = PartPosition.BottomFace;
         this.outwards = EnumFacing.DOWN;
      }

   }

   public abstract void isGoodForFrame() throws MultiblockValidationException;

   public abstract void isGoodForSides() throws MultiblockValidationException;

   public abstract void isGoodForTop() throws MultiblockValidationException;

   public abstract void isGoodForBottom() throws MultiblockValidationException;

   public abstract void isGoodForInterior() throws MultiblockValidationException;
}
