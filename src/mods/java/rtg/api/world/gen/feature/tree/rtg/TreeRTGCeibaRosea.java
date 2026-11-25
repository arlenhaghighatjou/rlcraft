package rtg.api.world.gen.feature.tree.rtg;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rtg.api.util.RTGTreeData;

public class TreeRTGCeibaRosea extends TreeRTG {
   protected float length;
   protected int branch;
   protected float verStart;
   protected float verRand;
   protected IBlockState trunkLog;

   public TreeRTGCeibaRosea() {
      this.length = 16.0F;
      this.branch = 5;
      this.verStart = 0.32F;
      this.verRand = 0.1F;
   }

   public TreeRTGCeibaRosea(float length, int branch, float verStart, float verRand) {
      this();
      this.length = length;
      this.branch = branch;
      this.verStart = verStart;
      this.verRand = verRand;
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
         float r = rand.nextFloat() * 360.0F;
         if (this.trunkSize > 0) {
            for(int k = 0; k < 5; ++k) {
               this.generateBranch(world, rand, (float)x + 0.5F, (float)(y + this.trunkSize), (float)z + 0.5F, (double)((float)(120 * k - 25 + rand.nextInt(50)) + r), 1.6F + rand.nextFloat() * 0.1F, (float)this.trunkSize * 1.6F, 1.0F, true, treeData);
            }
         }

         for(int i = y + this.trunkSize - 2; i < y + this.crownSize + 2; ++i) {
            this.placeLogBlock(world, new BlockPos(x, i, z), this.logBlock, this.generateFlag, treeData);
            this.placeLogBlock(world, new BlockPos(x + 1, i, z + 1), this.logBlock, this.generateFlag, treeData);
         }

         for(int j = 0; j < this.branch; ++j) {
            float horDir = (float)(80 * j - 40 + rand.nextInt(80));
            float verDir = this.verStart + rand.nextFloat() * this.verRand;
            this.generateBranch(world, rand, (float)x + 0.5F, (float)(y + this.crownSize), (float)z + 0.5F, (double)horDir, verDir, this.length, 1.0F, false, treeData);
            int eX = (int)(Math.cos((double)horDir * 3.141592653589793D / 180.0D) * (double)verDir * (double)this.length);
            int eZ = (int)(Math.sin((double)horDir * 3.141592653589793D / 180.0D) * (double)verDir * (double)this.length);
            int eY = (int)((1.0F - verDir) * this.length);

            for(int m = 0; m < 2; ++m) {
               this.generateLeaves(world, rand, (float)(x + eX - 2 + rand.nextInt(5)), (float)(y + this.crownSize + eY + rand.nextInt(2)), (float)(z + eZ - 2 + rand.nextInt(5)), 4.0F, 0.8F, treeData);
            }

            eX = (int)((float)eX * 0.8F);
            eY = (int)((float)eY * 0.8F);
            eZ = (int)((float)eZ * 0.8F);
            this.generateLeaves(world, rand, (float)(x + eX), (float)(y + this.crownSize + eY), (float)(z + eZ), 3.0F, 0.8F, treeData);
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

   public void generateLeaves(World world, Random rand, float x, float y, float z, float size, float width, RTGTreeData treeData) {
      int s = (int)(size - 1.0F);
      int w = (int)((size - 1.0F) * width);

      for(int i = -w; i <= w; ++i) {
         for(int j = -s + 1; j <= s; ++j) {
            for(int k = -w; k <= w; ++k) {
               float dist = Math.abs((float)i / width) + (float)Math.abs(j) + Math.abs((float)k / width);
               if (dist <= size - 0.5F || dist <= size && rand.nextBoolean()) {
                  if (dist < 1.3F) {
                     this.placeLogBlock(world, new BlockPos((int)x + i, (int)y + j, (int)z + k), this.logBlock, this.generateFlag, treeData);
                  }

                  if (!this.noLeaves) {
                     this.placeLeavesBlock(world, new BlockPos((int)x + i, (int)y + j, (int)z + k), this.leavesBlock, this.generateFlag, treeData);
                  }
               }
            }
         }
      }

   }

   public TreeRTGCeibaRosea setLength(float length) {
      this.length = length;
      return this;
   }

   public TreeRTGCeibaRosea setBranch(int branch) {
      this.branch = branch;
      return this;
   }

   public TreeRTGCeibaRosea setVerStart(float verStart) {
      this.verStart = verStart;
      return this;
   }

   public TreeRTGCeibaRosea setVerRand(float verRand) {
      this.verRand = verRand;
      return this;
   }
}
