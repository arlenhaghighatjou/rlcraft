package rtg.api.world.gen.feature.tree.rtg;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeRTGPiceaPungens extends TreeRTG {
   public TreeRTGPiceaPungens() {
   }

   public boolean generate(World world, Random rand, BlockPos pos) {
      if (!this.isGroundValid(world, pos)) {
         return false;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         int small = (int)Math.ceil((double)(this.crownSize / 2));
         int large = small;

         for(int i = 0; i < this.trunkSize; ++i) {
            this.placeLogBlock(world, new BlockPos(x, y, z), this.logBlock, this.generateFlag);
            ++y;
         }

         for(int var12 = 0; var12 < large; ++var12) {
            if (!this.noLeaves) {
               for(int j = -2; j <= 2; ++j) {
                  for(int k = -2; k <= 2; ++k) {
                     if (Math.abs(j) + Math.abs(k) != 4 && (j > -2 && k > -2 && j < 2 && k < 2 || rand.nextInt(4) != 0)) {
                        this.placeLeavesBlock(world, new BlockPos(x + j, y, z + k), this.leavesBlock, this.generateFlag);
                     }
                  }
               }
            }

            this.placeLogBlock(world, new BlockPos(x, y, z), this.logBlock, this.generateFlag);
            ++y;
         }

         for(int var13 = 0; var13 < small; ++var13) {
            if (!this.noLeaves) {
               for(int j = -1; j <= 1; ++j) {
                  for(int k = -1; k <= 1; ++k) {
                     if (Math.abs(j) + Math.abs(k) < 2 || rand.nextInt(4) != 0) {
                        this.placeLeavesBlock(world, new BlockPos(x + j, y, z + k), this.leavesBlock, this.generateFlag);
                     }
                  }
               }

               if (var13 == 0) {
                  this.placeLeavesBlock(world, new BlockPos(x + 1, y, z), this.leavesBlock, this.generateFlag);
                  this.placeLeavesBlock(world, new BlockPos(x - 1, y, z), this.leavesBlock, this.generateFlag);
                  this.placeLeavesBlock(world, new BlockPos(x, y, z + 1), this.leavesBlock, this.generateFlag);
                  this.placeLeavesBlock(world, new BlockPos(x, y, z - 1), this.leavesBlock, this.generateFlag);
                  this.placeLeavesBlock(world, new BlockPos(x + 2, y, z), this.leavesBlock, this.generateFlag);
                  this.placeLeavesBlock(world, new BlockPos(x - 2, y, z), this.leavesBlock, this.generateFlag);
                  this.placeLeavesBlock(world, new BlockPos(x, y, z + 2), this.leavesBlock, this.generateFlag);
                  this.placeLeavesBlock(world, new BlockPos(x, y, z - 2), this.leavesBlock, this.generateFlag);
               }
            }

            this.placeLogBlock(world, new BlockPos(x, y, z), this.logBlock, this.generateFlag);
            ++y;
         }

         this.placeLogBlock(world, new BlockPos(x, y, z), this.logBlock, this.generateFlag);
         if (!this.noLeaves) {
            this.placeLeavesBlock(world, new BlockPos(x + 1, y, z), this.leavesBlock, this.generateFlag);
            this.placeLeavesBlock(world, new BlockPos(x - 1, y, z), this.leavesBlock, this.generateFlag);
            this.placeLeavesBlock(world, new BlockPos(x, y, z + 1), this.leavesBlock, this.generateFlag);
            this.placeLeavesBlock(world, new BlockPos(x, y, z - 1), this.leavesBlock, this.generateFlag);
            this.placeLeavesBlock(world, new BlockPos(x, y + 1, z), this.leavesBlock, this.generateFlag);
            this.placeLeavesBlock(world, new BlockPos(x, y + 2, z), this.leavesBlock, this.generateFlag);
         }

         return true;
      }
   }

   public void buildBranch(World world, Random rand, int x, int y, int z, int dX, int dZ, int logLength, int leaveSize) {
      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = 0; k < 2; ++k) {
               if (Math.abs(i) + Math.abs(j) + Math.abs(k) < leaveSize + 1) {
                  this.buildLeaves(world, x + i + dX * logLength, y + k, z + j + dZ * logLength);
               }
            }
         }
      }

      for(int m = 1; m <= logLength; ++m) {
         this.placeLogBlock(world, new BlockPos(x + dX * m, y, z + dZ * m), this.logBlock, this.generateFlag);
      }

   }

   public void buildLeaves(World world, int x, int y, int z) {
      if (!this.noLeaves) {
         this.placeLeavesBlock(world, new BlockPos(x, y, z), this.leavesBlock, this.generateFlag);
      }

   }
}
