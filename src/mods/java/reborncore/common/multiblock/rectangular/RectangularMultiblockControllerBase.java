package reborncore.common.multiblock.rectangular;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.common.multiblock.MultiblockControllerBase;
import reborncore.common.multiblock.MultiblockValidationException;

public abstract class RectangularMultiblockControllerBase extends MultiblockControllerBase {
   protected RectangularMultiblockControllerBase(World world) {
      super(world);
   }

   protected void isMachineWhole() throws MultiblockValidationException {
      if (this.connectedParts.size() < this.getMinimumNumberOfBlocksForAssembledMachine()) {
         throw new MultiblockValidationException("Machine is too small.");
      } else {
         BlockPos maximumCoord = this.getMaximumCoord();
         BlockPos minimumCoord = this.getMinimumCoord();
         int deltaX = maximumCoord.getX() - minimumCoord.getX() + 1;
         int deltaY = maximumCoord.getY() - minimumCoord.getY() + 1;
         int deltaZ = maximumCoord.getZ() - minimumCoord.getZ() + 1;
         int maxX = this.getMaximumXSize();
         int maxY = this.getMaximumYSize();
         int maxZ = this.getMaximumZSize();
         int minX = this.getMinimumXSize();
         int minY = this.getMinimumYSize();
         int minZ = this.getMinimumZSize();
         if (maxX > 0 && deltaX > maxX) {
            throw new MultiblockValidationException(String.format("Machine is too large, it may be at most %d blocks in the X dimension", maxX));
         } else if (maxY > 0 && deltaY > maxY) {
            throw new MultiblockValidationException(String.format("Machine is too large, it may be at most %d blocks in the Y dimension", maxY));
         } else if (maxZ > 0 && deltaZ > maxZ) {
            throw new MultiblockValidationException(String.format("Machine is too large, it may be at most %d blocks in the Z dimension", maxZ));
         } else if (deltaX < minX) {
            throw new MultiblockValidationException(String.format("Machine is too small, it must be at least %d blocks in the X dimension", minX));
         } else if (deltaY < minY) {
            throw new MultiblockValidationException(String.format("Machine is too small, it must be at least %d blocks in the Y dimension", minY));
         } else if (deltaZ < minZ) {
            throw new MultiblockValidationException(String.format("Machine is too small, it must be at least %d blocks in the Z dimension", minZ));
         } else {
            Class<? extends RectangularMultiblockControllerBase> myClass = this.getClass();

            for(int x = minimumCoord.getX(); x <= maximumCoord.getX(); ++x) {
               for(int y = minimumCoord.getY(); y <= maximumCoord.getY(); ++y) {
                  for(int z = minimumCoord.getZ(); z <= maximumCoord.getZ(); ++z) {
                     TileEntity te = this.worldObj.getTileEntity(new BlockPos(x, y, z));
                     RectangularMultiblockTileEntityBase part;
                     if (te instanceof RectangularMultiblockTileEntityBase) {
                        part = (RectangularMultiblockTileEntityBase)te;
                        if (!myClass.equals(part.getMultiblockControllerType())) {
                           throw new MultiblockValidationException(String.format("Part @ %d, %d, %d is incompatible with machines of type %s", x, y, z, myClass.getSimpleName()));
                        }
                     } else {
                        part = null;
                     }

                     int extremes = 0;
                     if (x == minimumCoord.getX()) {
                        ++extremes;
                     }

                     if (y == minimumCoord.getY()) {
                        ++extremes;
                     }

                     if (z == minimumCoord.getZ()) {
                        ++extremes;
                     }

                     if (x == maximumCoord.getX()) {
                        ++extremes;
                     }

                     if (y == maximumCoord.getY()) {
                        ++extremes;
                     }

                     if (z == maximumCoord.getZ()) {
                        ++extremes;
                     }

                     if (extremes >= 2) {
                        if (part != null) {
                           part.isGoodForFrame();
                        } else {
                           this.isBlockGoodForFrame(this.worldObj, x, y, z);
                        }
                     } else if (extremes == 1) {
                        if (y == maximumCoord.getY()) {
                           if (part != null) {
                              part.isGoodForTop();
                           } else {
                              this.isBlockGoodForTop(this.worldObj, x, y, z);
                           }
                        } else if (y == minimumCoord.getY()) {
                           if (part != null) {
                              part.isGoodForBottom();
                           } else {
                              this.isBlockGoodForBottom(this.worldObj, x, y, z);
                           }
                        } else if (part != null) {
                           part.isGoodForSides();
                        } else {
                           this.isBlockGoodForSides(this.worldObj, x, y, z);
                        }
                     } else if (part != null) {
                        part.isGoodForInterior();
                     } else {
                        this.isBlockGoodForInterior(this.worldObj, x, y, z);
                     }
                  }
               }
            }

         }
      }
   }
}
