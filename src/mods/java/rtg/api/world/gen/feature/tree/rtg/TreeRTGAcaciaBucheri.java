package rtg.api.world.gen.feature.tree.rtg;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeRTGAcaciaBucheri extends TreeRTG {
   public TreeRTGAcaciaBucheri() {
      this.setLogBlock(Blocks.LOG2.getDefaultState());
      this.setLeavesBlock(Blocks.LEAVES2.getDefaultState());
      this.trunkSize = 10;
   }

   public boolean generate(World world, EaglercraftRandom rand, BlockPos pos) {
      if (!this.isGroundValid(world, pos)) {
         return false;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         int h = this.trunkSize;
         int bh = h - 3;

         for(int i = 0; i < h; ++i) {
            this.placeLogBlock(world, new BlockPos(x, y + i, z), this.logBlock, this.generateFlag);
         }

         this.genLeaves(world, rand, x, y + h, z);

         for(int a = 1 + rand.nextInt(2); a > -1; --a) {
            int sh = bh + rand.nextInt(2);
            int var10000 = h - (int)((float)(h - sh) * 0.25F);
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

   public void genLeaves(World world, EaglercraftRandom rand, int x, int y, int z) {
      if (!this.noLeaves) {
         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               this.placeLeavesBlock(world, new BlockPos(x + i, y + 1, z + j), this.leavesBlock, this.generateFlag);
            }
         }

         for(int var8 = -2; var8 <= 2; ++var8) {
            for(int j = -2; j <= 2; ++j) {
               if (Math.abs(var8) + Math.abs(j) < 4) {
                  this.placeLeavesBlock(world, new BlockPos(x + var8, y, z + j), this.leavesBlock, this.generateFlag);
               }
            }
         }
      }

      this.placeLogBlock(world, new BlockPos(x, y, z), this.logBlock, this.generateFlag);
   }
}
