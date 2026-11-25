package rtg.api.world.gen.feature.tree.rtg;

import java.util.Random;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rtg.api.util.BlockUtil;

public class TreeRTGPiceaSitchensis extends TreeRTG {
   public TreeRTGPiceaSitchensis() {
      this.setLogBlock(BlockUtil.getStateLog(EnumType.SPRUCE));
      this.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE));
      this.trunkSize = 8;
      this.crownSize = 10;
      this.setNoLeaves(false);
   }

   public boolean generate(World world, Random rand, BlockPos pos) {
      if (!this.isGroundValid(world, pos)) {
         return false;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();

         for(int i = 0; i < this.trunkSize; ++i) {
            this.placeLogBlock(world, new BlockPos(x, y, z), this.logBlock, this.generateFlag);
            ++y;
         }

         int pX = 0;
         int pZ = 0;

         for(int var12 = 0; var12 < this.crownSize; ++var12) {
            if (rand.nextInt(2) == 0 && var12 < this.crownSize - 2) {
               int dX = -1 + rand.nextInt(3);
               int dZ = -1 + rand.nextInt(3);
               if (dX == 0 && dZ == 0) {
                  dX = -1 + rand.nextInt(3);
                  dZ = -1 + rand.nextInt(3);
               }

               if (pX == dX && rand.nextBoolean()) {
                  dX = -dX;
               }

               if (pZ == dZ && rand.nextBoolean()) {
                  dZ = -dZ;
               }

               pX = dX;
               pZ = dZ;
               this.buildBranch(world, rand, x, y, z, dX, dZ, var12 < this.crownSize - 10 ? 2 : 1, var12 < this.crownSize - 6 ? 2 : 1);
            }

            this.placeLogBlock(world, new BlockPos(x, y, z), this.logBlock, this.generateFlag);
            if (var12 < this.crownSize - 2) {
               if (rand.nextBoolean()) {
                  this.buildLeaves(world, x, y, z + 1);
               }

               if (rand.nextBoolean()) {
                  this.buildLeaves(world, x, y, z - 1);
               }

               if (rand.nextBoolean()) {
                  this.buildLeaves(world, x + 1, y, z);
               }

               if (rand.nextBoolean()) {
                  this.buildLeaves(world, x - 1, y, z);
               }
            }

            ++y;
         }

         this.buildLeaves(world, x, y - 1, z + 1);
         this.buildLeaves(world, x, y - 1, z - 1);
         this.buildLeaves(world, x + 1, y - 1, z);
         this.buildLeaves(world, x - 1, y - 1, z);
         this.buildLeaves(world, x, y, z);
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
