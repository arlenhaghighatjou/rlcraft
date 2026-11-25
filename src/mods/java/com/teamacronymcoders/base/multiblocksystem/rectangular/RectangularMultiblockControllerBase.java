package com.teamacronymcoders.base.multiblocksystem.rectangular;

import com.teamacronymcoders.base.multiblocksystem.MultiblockControllerBase;
import com.teamacronymcoders.base.multiblocksystem.validation.IMultiblockValidator;
import com.teamacronymcoders.base.multiblocksystem.validation.ValidationError;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class RectangularMultiblockControllerBase extends MultiblockControllerBase {
   protected RectangularMultiblockControllerBase(World world) {
      super(world);
   }

   protected boolean isMachineWhole(IMultiblockValidator validatorCallback) {
      if (this.connectedParts.size() < this.getMinimumNumberOfBlocksForAssembledMachine()) {
         validatorCallback.setLastError(ValidationError.VALIDATION_ERROR_TOO_FEW_PARTS);
         return false;
      } else {
         BlockPos maximumCoord = this.getMaximumCoord();
         BlockPos minimumCoord = this.getMinimumCoord();
         int minX = minimumCoord.getX();
         int minY = minimumCoord.getY();
         int minZ = minimumCoord.getZ();
         int maxX = maximumCoord.getX();
         int maxY = maximumCoord.getY();
         int maxZ = maximumCoord.getZ();
         int deltaX = maxX - minX + 1;
         int deltaY = maxY - minY + 1;
         int deltaZ = maxZ - minZ + 1;
         int maxXSize = this.getMaximumXSize();
         int maxYSize = this.getMaximumYSize();
         int maxZSize = this.getMaximumZSize();
         int minXSize = this.getMinimumXSize();
         int minYSize = this.getMinimumYSize();
         int minZSize = this.getMinimumZSize();
         if (maxXSize > 0 && deltaX > maxXSize) {
            validatorCallback.setLastError("base.api.multiblock.validation.machine_too_large", maxXSize, "X");
            return false;
         } else if (maxYSize > 0 && deltaY > maxYSize) {
            validatorCallback.setLastError("base.api.multiblock.validation.machine_too_large", maxYSize, "Y");
            return false;
         } else if (maxZSize > 0 && deltaZ > maxZSize) {
            validatorCallback.setLastError("base.api.multiblock.validation.machine_too_large", maxZSize, "Z");
            return false;
         } else if (deltaX < minXSize) {
            validatorCallback.setLastError("base.api.multiblock.validation.machine_too_small", minXSize, "X");
            return false;
         } else if (deltaY < minYSize) {
            validatorCallback.setLastError("base.api.multiblock.validation.machine_too_small", minYSize, "Y");
            return false;
         } else if (deltaZ < minZSize) {
            validatorCallback.setLastError("base.api.multiblock.validation.machine_too_small", minZSize, "Z");
            return false;
         } else {
            Class<? extends RectangularMultiblockControllerBase> myClass = this.getClass();

            for(int x = minX; x <= maxX; ++x) {
               for(int y = minY; y <= maxY; ++y) {
                  for(int z = minZ; z <= maxZ; ++z) {
                     TileEntity te = this.WORLD.getTileEntity(new BlockPos(x, y, z));
                     RectangularMultiblockTileEntityBase<?> part;
                     if (te instanceof RectangularMultiblockTileEntityBase) {
                        part = (RectangularMultiblockTileEntityBase)te;
                        if (!myClass.equals(part.getMultiblockControllerType())) {
                           validatorCallback.setLastError("base.api.multiblock.validation.invalid_part", x, y, z);
                           return false;
                        }
                     } else {
                        part = null;
                     }

                     int extremes = 0;
                     if (x == minX) {
                        ++extremes;
                     }

                     if (y == minY) {
                        ++extremes;
                     }

                     if (z == minZ) {
                        ++extremes;
                     }

                     if (x == maxX) {
                        ++extremes;
                     }

                     if (y == maxY) {
                        ++extremes;
                     }

                     if (z == maxZ) {
                        ++extremes;
                     }

                     if (extremes >= 2) {
                        boolean isPartValid = part != null ? part.isGoodForFrame(validatorCallback) : this.isBlockGoodForFrame(this.WORLD, x, y, z, validatorCallback);
                        if (!isPartValid) {
                           if (null == validatorCallback.getLastError()) {
                              validatorCallback.setLastError("base.api.multiblock.validation.invalid_part_for_frame", x, y, z);
                           }

                           return false;
                        }
                     } else if (extremes == 1) {
                        if (y == maxY) {
                           boolean isPartValid = part != null ? part.isGoodForTop(validatorCallback) : this.isBlockGoodForTop(this.WORLD, x, y, z, validatorCallback);
                           if (!isPartValid) {
                              if (null == validatorCallback.getLastError()) {
                                 validatorCallback.setLastError("base.api.multiblock.validation.invalid_part_for_top", x, y, z);
                              }

                              return false;
                           }
                        } else if (y == minY) {
                           boolean isPartValid = part != null ? part.isGoodForBottom(validatorCallback) : this.isBlockGoodForBottom(this.WORLD, x, y, z, validatorCallback);
                           if (!isPartValid) {
                              if (null == validatorCallback.getLastError()) {
                                 validatorCallback.setLastError("base.api.multiblock.validation.invalid_part_for_bottom", x, y, z);
                              }

                              return false;
                           }
                        } else {
                           boolean isPartValid = part != null ? part.isGoodForSides(validatorCallback) : this.isBlockGoodForSides(this.WORLD, x, y, z, validatorCallback);
                           if (!isPartValid) {
                              if (null == validatorCallback.getLastError()) {
                                 validatorCallback.setLastError("base.api.multiblock.validation.invalid_part_for_sides", x, y, z);
                              }

                              return false;
                           }
                        }
                     } else {
                        boolean isPartValid = part != null ? part.isGoodForInterior(validatorCallback) : this.isBlockGoodForInterior(this.WORLD, x, y, z, validatorCallback);
                        if (!isPartValid) {
                           if (null == validatorCallback.getLastError()) {
                              validatorCallback.setLastError("base.api.multiblock.validation.invalid_part_for_interior", x, y, z);
                           }

                           return false;
                        }
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}
