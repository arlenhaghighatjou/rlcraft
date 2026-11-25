package rtg.api.world.gen.feature.tree.rtg;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeRTGPinusNigra extends TreeRTG {
   protected IBlockState trunkLog;

   public TreeRTGPinusNigra() {
   }

   public boolean generate(World world, Random rand, BlockPos pos) {
      if (!this.isGroundValid(world, pos)) {
         return false;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         this.trunkLog = this.getTrunkLog(this.logBlock);
         int height = this.trunkSize;
         int leafheight = this.crownSize;
         float branchIncrease = 0.25F;

         for(int i = 0; i <= height; ++i) {
            this.placeLogBlock(world, new BlockPos(x, y + i, z), this.logBlock, this.generateFlag);
         }

         this.buildLeaves(world, rand, x, y + height, z, 2);
         this.buildTrunk(world, rand, x, y, z);
         int dir = 0;
         float bl = 1.0F;

         for(int j = height; j >= height - leafheight; --j) {
            bl += branchIncrease;
            int var17 = dir + rand.nextInt(180) + 90;
            dir = var17 - (var17 > 360 ? 360 : 0);
            float xd = (float)Math.cos((double)dir * 3.141592653589793D / 180.0D);
            float yd = (float)Math.sin((double)dir * 3.141592653589793D / 180.0D);

            int b;
            for(b = 0; (float)b <= bl; ++b) {
               this.placeLogBlock(world, new BlockPos(x + (int)((float)b * xd), y + j, z + (int)((float)b * yd)), this.trunkLog, this.generateFlag);
            }

            this.buildLeaves(world, rand, x, y + j, z, 2);
            this.buildLeaves(world, rand, x + (int)((float)b * xd), y + j, z + (int)((float)b * yd), 2);
         }

         return true;
      }
   }

   public void buildLeaves(World world, Random rand, int x, int y, int z, int size) {
      if (!this.noLeaves) {
         int t = (int)Math.pow((double)size, 2.0D);

         for(int i = -size; i <= size; ++i) {
            for(int j = -size; j <= size; ++j) {
               for(int k = -size; k <= size; ++k) {
                  int l = i * i + j * j + k * k;
                  if (l <= t && (l < t / 2 || rand.nextBoolean())) {
                     this.placeLeavesBlock(world, new BlockPos(x + i, y + j, z + k), this.leavesBlock, this.generateFlag);
                  }
               }
            }
         }
      }

   }

   public void buildTrunk(World world, Random rand, int x, int y, int z) {
      int[] pos = new int[]{0, 0, 1, 0, 0, 1, -1, 0, 0, -1};

      for(int t = 0; t < 5; ++t) {
         for(int sh = rand.nextInt(3) + y; sh > y - 3 && world.getBlockState(new BlockPos(x + pos[t * 2], sh, z + pos[t * 2 + 1])) != Blocks.DIRT.getDefaultState(); --sh) {
            this.placeLogBlock(world, new BlockPos(x + pos[t * 2], sh, z + pos[t * 2 + 1]), this.trunkLog, this.generateFlag);
         }
      }

   }
}
