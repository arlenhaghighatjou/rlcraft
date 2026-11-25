package com.teamacronymcoders.base.multiblocksystem.rectangular;

import com.teamacronymcoders.base.multiblocksystem.BlockFacings;
import com.teamacronymcoders.base.multiblocksystem.MultiblockControllerBase;
import com.teamacronymcoders.base.multiblocksystem.MultiblockTileEntityBase;
import com.teamacronymcoders.base.multiblocksystem.validation.IMultiblockValidator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public abstract class RectangularMultiblockTileEntityBase<T extends RectangularMultiblockControllerBase> extends MultiblockTileEntityBase<T> {
   private PartPosition position = PartPosition.UNKNOWN;
   private BlockFacings outwardFacings = BlockFacings.NONE;

   public RectangularMultiblockTileEntityBase() {
   }

   @Nonnull
   public BlockFacings getOutwardsDir() {
      return this.outwardFacings;
   }

   @Nonnull
   public PartPosition getPartPosition() {
      return this.position;
   }

   @Nullable
   public EnumFacing getOutwardFacing() {
      EnumFacing facing = null != this.position ? this.position.getFacing() : null;
      if (null == facing) {
         BlockFacings out = this.getOutwardsDir();
         if (!out.none() && 1 == out.countFacesIf(true)) {
            facing = out.firstIf(true);
         }
      }

      return facing;
   }

   @Nullable
   public EnumFacing getOutwardFacingFromWorldPosition() {
      BlockFacings facings = null;
      MultiblockControllerBase controller = this.getMultiblockController();
      if (null != controller) {
         BlockPos position = this.getWorldPosition();
         BlockPos min = controller.getMinimumCoord();
         BlockPos max = controller.getMaximumCoord();
         int x = position.getX();
         int y = position.getY();
         int z = position.getZ();
         facings = BlockFacings.from(min.getY() == y, max.getY() == y, min.getZ() == z, max.getZ() == z, min.getX() == x, max.getX() == x);
      }

      return null != facings && !facings.none() && 1 == facings.countFacesIf(true) ? facings.firstIf(true) : null;
   }

   public void onAttached(MultiblockControllerBase newController) {
      super.onAttached(newController);
      this.recalculateOutwardsDirection(newController.getMinimumCoord(), newController.getMaximumCoord());
   }

   public void onMachineAssembled(MultiblockControllerBase controller) {
      this.recalculateOutwardsDirection(controller.getMinimumCoord(), controller.getMaximumCoord());
   }

   public void onMachineBroken() {
      this.position = PartPosition.UNKNOWN;
      this.outwardFacings = BlockFacings.NONE;
   }

   public void recalculateOutwardsDirection(BlockPos minCoord, BlockPos maxCoord) {
      BlockPos myPosition = this.getPos();
      int myX = myPosition.getX();
      int myY = myPosition.getY();
      int myZ = myPosition.getZ();
      int facesMatching = 0;
      boolean downFacing = myY == minCoord.getY();
      boolean upFacing = myY == maxCoord.getY();
      boolean northFacing = myZ == minCoord.getZ();
      boolean southFacing = myZ == maxCoord.getZ();
      boolean westFacing = myX == minCoord.getX();
      boolean eastFacing = myX == maxCoord.getX();
      this.outwardFacings = BlockFacings.from(downFacing, upFacing, northFacing, southFacing, westFacing, eastFacing);
      if (eastFacing || westFacing) {
         ++facesMatching;
      }

      if (upFacing || downFacing) {
         ++facesMatching;
      }

      if (southFacing || northFacing) {
         ++facesMatching;
      }

      if (facesMatching <= 0) {
         this.position = PartPosition.INTERIOR;
      } else if (facesMatching >= 3) {
         if (upFacing) {
            if (southFacing) {
               if (eastFacing) {
                  this.position = PartPosition.FRAME_CORNER_SOUTH_EAST_TOP;
               } else if (westFacing) {
                  this.position = PartPosition.FRAME_CORNER_SOUTH_WEST_TOP;
               }
            } else if (northFacing) {
               if (eastFacing) {
                  this.position = PartPosition.FRAME_CORNER_NORTH_EAST_TOP;
               } else if (westFacing) {
                  this.position = PartPosition.FRAME_CORNER_NORTH_WEST_TOP;
               }
            }
         } else if (downFacing) {
            if (southFacing) {
               if (eastFacing) {
                  this.position = PartPosition.FRAME_CORNER_SOUTH_EAST_BOTTOM;
               } else if (westFacing) {
                  this.position = PartPosition.FRAME_CORNER_SOUTH_WEST_BOTTOM;
               }
            } else if (northFacing) {
               if (eastFacing) {
                  this.position = PartPosition.FRAME_CORNER_NORTH_EAST_BOTTOM;
               } else if (westFacing) {
                  this.position = PartPosition.FRAME_CORNER_NORTH_WEST_BOTTOM;
               }
            }
         }
      } else if (facesMatching == 2) {
         if (!eastFacing && !westFacing) {
            if (upFacing) {
               if (northFacing) {
                  this.position = PartPosition.FRAME_NORTH_TOP;
               } else if (southFacing) {
                  this.position = PartPosition.FRAME_SOUTH_TOP;
               }
            } else if (downFacing) {
               if (northFacing) {
                  this.position = PartPosition.FRAME_NORTH_BOTTOM;
               } else if (southFacing) {
                  this.position = PartPosition.FRAME_SOUTH_BOTTOM;
               }
            }
         } else if (!southFacing && !northFacing) {
            if (upFacing) {
               if (eastFacing) {
                  this.position = PartPosition.FRAME_EAST_TOP;
               } else if (westFacing) {
                  this.position = PartPosition.FRAME_WEST_TOP;
               }
            } else if (downFacing) {
               if (eastFacing) {
                  this.position = PartPosition.FRAME_EAST_BOTTOM;
               } else if (westFacing) {
                  this.position = PartPosition.FRAME_WEST_BOTTOM;
               }
            }
         } else if (southFacing) {
            if (eastFacing) {
               this.position = PartPosition.FRAME_VERTICAL_SOUTH_EAST;
            } else if (westFacing) {
               this.position = PartPosition.FRAME_VERTICAL_SOUTH_WEST;
            }
         } else if (northFacing) {
            if (eastFacing) {
               this.position = PartPosition.FRAME_VERTICAL_NORTH_EAST;
            } else if (westFacing) {
               this.position = PartPosition.FRAME_VERTICAL_NORTH_WEST;
            }
         }
      } else if (eastFacing) {
         this.position = PartPosition.EAST_FACE;
      } else if (westFacing) {
         this.position = PartPosition.WEST_FACE;
      } else if (southFacing) {
         this.position = PartPosition.SOUTH_FACE;
      } else if (northFacing) {
         this.position = PartPosition.NORTH_FACE;
      } else if (upFacing) {
         this.position = PartPosition.TOP_FACE;
      } else {
         this.position = PartPosition.BOTTOM_FACE;
      }

   }

   public abstract boolean isGoodForFrame(IMultiblockValidator var1);

   public abstract boolean isGoodForSides(IMultiblockValidator var1);

   public abstract boolean isGoodForTop(IMultiblockValidator var1);

   public abstract boolean isGoodForBottom(IMultiblockValidator var1);

   public abstract boolean isGoodForInterior(IMultiblockValidator var1);
}
