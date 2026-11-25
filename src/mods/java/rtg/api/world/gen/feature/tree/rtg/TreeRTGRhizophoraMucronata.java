package rtg.api.world.gen.feature.tree.rtg;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rtg.api.util.RTGTreeData;

public class TreeRTGRhizophoraMucronata extends TreeRTG {
   protected int minBranches;
   protected int maxBranches;
   protected float branchLength;
   protected float verStart;
   protected float verRand;
   protected IBlockState trunkLog;

   public TreeRTGRhizophoraMucronata(int minBranches, int maxBranches, float branchLength, float verStart, float verRand) {
      this();
      this.minBranches = minBranches;
      this.maxBranches = maxBranches;
      this.branchLength = branchLength;
      this.verStart = verStart;
      this.verRand = verRand;
   }

   public TreeRTGRhizophoraMucronata() {
      this.minBranches = 3;
      this.maxBranches = 4;
      this.branchLength = 13.0F;
      this.verStart = 0.32F;
      this.verRand = 0.1F;
   }

   public boolean generate(World world, Random rand, BlockPos pos) {
      if (!this.isGroundValid(world, pos)) {
         return false;
      } else {
         RTGTreeData treeData = new RTGTreeData();
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         this.trunkLog = this.getTrunkLog(this.logBlock);
         int branch = this.minBranches + rand.nextInt(this.maxBranches - this.minBranches + 1);
         if (this.trunkSize > 0) {
            for(int k = 0; k < 3; ++k) {
               this.generateBranch(world, rand, (float)x, (float)(y + this.trunkSize), (float)z, (double)(120 * k - 40 + rand.nextInt(80)), 1.6F + rand.nextFloat() * 0.1F, (float)this.trunkSize * 2.0F, 1.0F, true, treeData);
            }
         }

         for(int i = y + this.trunkSize; i < y + this.crownSize; ++i) {
            this.placeLogBlock(world, new BlockPos(x, i, z), this.logBlock, this.generateFlag, treeData);
         }

         for(int j = 0; j < branch; ++j) {
            float horDir = (float)(120 * j - 60 + rand.nextInt(120));
            float verDir = this.verStart + rand.nextFloat() * this.verRand;
            this.generateBranch(world, rand, (float)x, (float)(y + this.crownSize), (float)z, (double)horDir, verDir, this.branchLength, 1.0F, false, treeData);
            int eX = x + (int)(Math.cos((double)horDir * 3.141592653589793D / 180.0D) * (double)verDir * (double)this.branchLength);
            int eZ = z + (int)(Math.sin((double)horDir * 3.141592653589793D / 180.0D) * (double)verDir * (double)this.branchLength);
            int eY = y + this.crownSize + (int)((1.0F - verDir) * this.branchLength);

            for(int m = 0; m < 1; ++m) {
               this.generateLeaves(world, rand, eX, eY, eZ, 4.0F, 1.2F, treeData);
            }
         }

         return true;
      }
   }

   public void generateBranch(World world, Random rand, float x, float y, float z, double horDir, float verDir, float length, float speed, boolean isTrunk, RTGTreeData treeData) {
      if (verDir < 0.0F) {
         verDir = -verDir;
      }

      float c = 0.0F;
      float velY = 1.0F - verDir;
      if (verDir > 1.0F) {
         verDir = 1.0F - (verDir - 1.0F);
      }

      float velX = (float)Math.cos(horDir * 3.141592653589793D / 180.0D) * verDir;

      for(float velZ = (float)Math.sin(horDir * 3.141592653589793D / 180.0D) * verDir; c < length; c += speed) {
         if (isTrunk) {
            this.placeLogBlock(world, new BlockPos((int)x, (int)y, (int)z), this.trunkLog, this.generateFlag, treeData);
         } else {
            this.placeLogBlock(world, new BlockPos((int)x, (int)y, (int)z), this.logBlock, this.generateFlag, treeData);
         }

         x += velX;
         y += velY;
         z += velZ;
      }

   }

   public void generateLeaves(World world, Random rand, int x, int y, int z, float size, float width, RTGTreeData treeData) {
      int s = (int)(size - 1.0F);
      int w = (int)((size - 1.0F) * width);

      for(int i = -w; i <= w; ++i) {
         for(int j = -s; j <= s; ++j) {
            for(int k = -w; k <= w; ++k) {
               float dist = Math.abs((float)i / width) + (float)Math.abs(j) + Math.abs((float)k / width);
               if (dist <= size - 0.5F || dist <= size && rand.nextBoolean()) {
                  if (dist < 0.6F) {
                     this.placeLogBlock(world, new BlockPos(x + i, y + j, z + k), this.logBlock, this.generateFlag, treeData);
                  }

                  if (!this.noLeaves) {
                     this.placeLeavesBlock(world, new BlockPos(x + i, y + j, z + k), this.leavesBlock, this.generateFlag, treeData);
                  }
               }
            }
         }
      }

   }
}
