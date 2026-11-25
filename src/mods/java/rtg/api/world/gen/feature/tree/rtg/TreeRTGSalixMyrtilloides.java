package rtg.api.world.gen.feature.tree.rtg;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rtg.RTGConfig;

public class TreeRTGSalixMyrtilloides extends TreeRTG {
   protected IBlockState trunkLog;

   public TreeRTGSalixMyrtilloides() {
      this.setLogBlock(Blocks.LOG.getDefaultState()).setLeavesBlock(Blocks.LEAVES.getDefaultState());
   }

   public boolean generate(World world, Random rand, BlockPos pos) {
      if (!this.isGroundValid(world, pos, RTGConfig.treesCanGenerateOnSand())) {
         return false;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         this.trunkLog = this.getTrunkLog(this.logBlock);
         int height = 13;
         int leaveheight = 5;
         int branches = 6;
         int branchLenght = 6;

         for(int i = 0; i < height; ++i) {
            this.placeLogBlock(world, new BlockPos(x, y + i, z), this.logBlock, this.generateFlag);
         }

         this.createLeavesAroundBranch(world, rand, x, y + height, z, 3, 2);
         this.createTrunk(world, rand, x, y, z);
         int dir = rand.nextInt((int)(360.0F / (float)branches));

         for(int b = 0; b < branches; ++b) {
            float c = 0.0F;
            float hd = (float)height - rand.nextFloat() * (float)leaveheight - 2.0F;
            dir += (int)(360.0F / (float)branches);
            float xd = (float)Math.cos((double)dir * 3.141592653589793D / 180.0D);
            float yd = (float)Math.sin((double)dir * 3.141592653589793D / 180.0D);
            boolean m = false;

            while(c < (float)branchLenght) {
               if (c > (float)(branchLenght / 2) && !m) {
                  m = true;
                  this.createLeavesAroundBranch(world, rand, x + (int)(c * xd), y + (int)hd, z + (int)(c * yd), 2, 1);
               }

               ++c;
               hd += 0.5F;
               this.placeLogBlock(world, new BlockPos(x + (int)(c * xd), y + (int)hd, z + (int)(c * yd)), this.trunkLog, this.generateFlag);
            }

            this.createLeavesAroundBranch(world, rand, x + (int)(c * xd), y + (int)hd, z + (int)(c * yd), 2, 1);
         }

         return true;
      }
   }

   private void createLeavesAroundBranch(World world, Random rand, int x, int y, int z, int s, int c) {
      int t = (int)Math.pow((double)s, 2.0D);

      for(int i = -s; i <= s; ++i) {
         for(int j = -s; j <= s; ++j) {
            for(int k = -s; k <= s; ++k) {
               int l = i * i + j * j + k * k;
               if (l <= t && (l < t - c || rand.nextBoolean()) && !this.noLeaves) {
                  this.placeLeavesBlock(world, new BlockPos(x + i, y + j, z + k), this.leavesBlock, this.generateFlag);
                  if (j < -(s - 2) && rand.nextInt(3) != 0) {
                     this.createVine(world, rand, x + i, y + j, z + k);
                  }
               }
            }
         }
      }

   }

   private void createVine(World world, Random rand, int x, int y, int z) {
      int r = rand.nextInt(3) + 5;

      for(int i = -1; i > -r; --i) {
         this.placeLeavesBlock(world, new BlockPos(x, y + i, z), this.leavesBlock, this.generateFlag);
      }

   }

   private void createTrunk(World world, Random rand, int x, int y, int z) {
      int[] pos = new int[]{0, 0, 1, 0, 0, 1, -1, 0, 0, -1};

      for(int t = 0; t < 5; ++t) {
         for(int sh = rand.nextInt(3) + y; sh > y - 3; --sh) {
            this.placeLogBlock(world, new BlockPos(x + pos[t * 2], sh, z + pos[t * 2 + 1]), this.trunkLog, this.generateFlag);
         }
      }

   }

   protected boolean isGroundValid(World world, BlockPos trunkPos, boolean sandAllowed) {
      int x = trunkPos.getX();
      int y = trunkPos.getY();
      int z = trunkPos.getZ();
      boolean earth = false;
      boolean water = false;

      for(int c1 = -2; c1 <= 2; ++c1) {
         for(int c3 = -2; c3 <= 2; ++c3) {
            for(int c2 = -1; c2 <= 1; ++c2) {
               BlockPos posTemp = new BlockPos(x + c1, y + c2, z + c3);
               IBlockState cb = world.getBlockState(posTemp);
               if (this.validGroundBlocks.contains(cb)) {
                  earth = true;
               } else if (cb == Blocks.WATER.getDefaultState()) {
                  water = true;
               }
            }
         }
      }

      if (earth && water) {
         return true;
      } else {
         return false;
      }
   }
}
