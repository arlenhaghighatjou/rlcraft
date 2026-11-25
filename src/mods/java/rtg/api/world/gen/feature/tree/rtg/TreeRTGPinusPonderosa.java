package rtg.api.world.gen.feature.tree.rtg;

import java.util.ArrayList;
import java.util.Arrays;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeRTGPinusPonderosa extends TreeRTG {
   protected IBlockState trunkLog;

   public TreeRTGPinusPonderosa() {
   }

   public boolean generate(World world, EaglercraftRandom rand, BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      this.trunkLog = this.getTrunkLog(this.logBlock);
      if (!this.isGroundValid(world, new ArrayList(Arrays.asList(new BlockPos(x + 1, y, z), new BlockPos(x - 1, y, z), new BlockPos(x, y, z + 1), new BlockPos(x, y, z - 1))))) {
         return false;
      } else {
         this.buildTrunk(world, rand, x + 1, y, z);
         this.buildTrunk(world, rand, x - 1, y, z);
         this.buildTrunk(world, rand, x, y, z + 1);
         this.buildTrunk(world, rand, x, y, z - 1);

         for(int i = 0; i < this.trunkSize; ++i) {
            this.placeLogBlock(world, new BlockPos(x, y, z), this.logBlock, this.generateFlag);
            if (i > 5 && rand.nextInt(7) == 0) {
               int dX = -1 + rand.nextInt(3);
               int dZ = -1 + rand.nextInt(3);
               if (dX == 0 && dZ == 0) {
                  dX = -1 + rand.nextInt(3);
                  dZ = -1 + rand.nextInt(3);
               }

               this.buildBranch(world, rand, x, y, z, dX, dZ, 1, 1);
            }

            ++y;
         }

         int pX = 0;
         int pZ = 0;

         for(int var13 = 0; var13 < this.crownSize; ++var13) {
            if (rand.nextInt(var13 < this.crownSize - 12 && var13 > 2 ? 2 : 1) == 0 && var13 < this.crownSize - 2) {
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
               this.buildBranch(world, rand, x, y, z, dX, dZ, var13 < this.crownSize - 12 && var13 > 3 ? 3 : (var13 < this.crownSize - 8 ? 2 : 1), var13 < this.crownSize - 5 ? 2 : 1);
            }

            this.placeLogBlock(world, new BlockPos(x, y, z), this.logBlock, this.generateFlag);
            if (var13 < this.crownSize - 2) {
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

   public void buildTrunk(World world, EaglercraftRandom rand, int x, int y, int z) {
      int h = (int)Math.ceil((double)((float)this.trunkSize / 4.0F));
      h = h + rand.nextInt(h * 2);

      for(int i = -1; i < h; ++i) {
         this.placeLogBlock(world, new BlockPos(x, y + i, z), this.trunkLog, this.generateFlag);
      }

   }

   public void buildBranch(World world, EaglercraftRandom rand, int x, int y, int z, int dX, int dZ, int logLength, int leaveSize) {
      if (logLength == 3 && Math.abs(dX) + Math.abs(dZ) == 2) {
         --logLength;
      }

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
