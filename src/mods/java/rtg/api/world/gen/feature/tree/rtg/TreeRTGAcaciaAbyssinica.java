package rtg.api.world.gen.feature.tree.rtg;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeRTGAcaciaAbyssinica extends TreeRTG {
   public TreeRTGAcaciaAbyssinica() {
      this.setLogBlock(Blocks.LOG2.getDefaultState());
      this.setLeavesBlock(Blocks.LEAVES2.getDefaultState());
      this.trunkSize = 12;
   }

   public boolean generate(World world, Random rand, BlockPos pos) {
      if (!this.isGroundValid(world, pos)) {
         return false;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         int h = this.trunkSize;
         int bh = h - 6;

         for(int i = 0; i < h; ++i) {
            this.placeLogBlock(world, new BlockPos(x, y + i, z), this.logBlock, this.generateFlag);
         }

         this.genLeaves(world, rand, x, y + h, z);

         for(int a = 6 + rand.nextInt(3); a > -1; --a) {
            int sh = bh + rand.nextInt(4);
            int var10000 = h - (int)((float)(h - sh) * 1.0F);
            int dir = rand.nextInt(360);
            float xd = (float)Math.cos((double)dir * 3.141592653589793D / 180.0D) * 2.0F;
            float yd = (float)Math.sin((double)dir * 3.141592653589793D / 180.0D) * 2.0F;

            float c;
            for(c = 1.0F; sh < h; c += 0.5F) {
               this.placeLogBlock(world, new BlockPos(x + (int)(xd * c), y + sh, z + (int)(yd * c)), this.logBlock, this.generateFlag);
               ++sh;
            }

            this.genLeaves(world, rand, x + (int)(xd * c), y + sh, z + (int)(yd * c));
         }

         return true;
      }
   }

   public void genLeaves(World world, Random rand, int x, int y, int z) {
      if (!this.noLeaves) {
         for(int i = -2; i <= 2; ++i) {
            for(int j = -2; j <= 2; ++j) {
               if (Math.abs(i) + Math.abs(j) < 4) {
                  this.placeLeavesBlock(world, new BlockPos(x + i, y + 1, z + j), this.leavesBlock, this.generateFlag);
               }
            }
         }

         for(int var8 = -3; var8 <= 3; ++var8) {
            for(int j = -3; j <= 3; ++j) {
               if (Math.abs(var8) + Math.abs(j) < 5) {
                  this.placeLeavesBlock(world, new BlockPos(x + var8, y, z + j), this.leavesBlock, this.generateFlag);
               }
            }
         }
      }

      this.placeLogBlock(world, new BlockPos(x, y, z), this.logBlock, this.generateFlag);
   }
}
