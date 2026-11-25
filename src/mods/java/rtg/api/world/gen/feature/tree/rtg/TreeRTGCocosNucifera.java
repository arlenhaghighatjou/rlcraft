package rtg.api.world.gen.feature.tree.rtg;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rtg.api.util.BlockUtil;

public class TreeRTGCocosNucifera extends TreeRTG {
   private static int leavesLength = 133;
   private static int[] leaves = new int[]{1, 0, 0, 2, 0, 0, 3, -1, 0, 3, -2, 0, -1, 0, 0, -2, 0, 0, -3, -1, 0, -3, -2, 0, 0, 0, 1, 0, 0, 2, 0, -1, 3, 0, -2, 3, 0, 0, -1, 0, 0, -2, 0, -1, -3, 0, -2, -3, 0, 1, 0, 1, 1, 1, -1, 1, -1, -1, 1, 1, 1, 1, -1, 1, 2, 0, -1, 2, 0, 0, 2, 1, 0, 2, -1, 2, 3, 0, 3, 3, 0, 4, 2, 0, -2, 3, 0, -3, 3, 0, -4, 2, 0, 0, 3, 2, 0, 3, 3, 0, 2, 4, 0, 3, -2, 0, 3, -3, 0, 2, -4, 2, 2, -2, -2, 2, 2, -2, 2, -2, 2, 2, 2, 3, 2, -3, -3, 2, 3, -3, 2, -3, 3, 2, 3};
   private static int cocoasLength = 16;
   private static int[] cocoas = new int[]{2, 0, -2, 1, 1, 1, -2, 0, 0, 0, -2, -1, 3, -1, -2, 0};
   protected IBlockState trunkLog;

   public TreeRTGCocosNucifera() {
      this.setLogBlock(BlockUtil.getStateLog(EnumType.JUNGLE));
      this.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.JUNGLE));
      this.trunkSize = 8;
      this.crownSize = 7;
   }

   public boolean generate(World world, EaglercraftRandom rand, BlockPos pos) {
      if (!this.isGroundValid(world, pos, true)) {
         return false;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         this.trunkLog = this.getTrunkLog(this.logBlock);
         double horDir = this.getRandomDir(rand);
         float verDir = 0.3F + rand.nextFloat() * 0.4F;
         float length = (float)(this.trunkSize + this.crownSize);
         float posX = (float)x;
         float posY = (float)y;
         float posZ = (float)z;
         float c = 0.0F;
         float loss = 0.0F;
         if (verDir < 0.0F) {
            verDir = -verDir;
         }

         float velY = 1.0F - verDir;
         if (verDir > 1.0F) {
            verDir = 1.0F - (verDir - 1.0F);
         }

         float velX = (float)Math.cos(horDir * 3.141592653589793D / 180.0D) * verDir;

         for(float velZ = (float)Math.sin(horDir * 3.141592653589793D / 180.0D) * verDir; c < length; ++c) {
            this.placeLogBlock(world, new BlockPos((int)posX, (int)posY, (int)posZ), this.trunkLog, this.generateFlag);
            if (c < length - 3.0F) {
               loss = Math.abs(velX) + Math.abs(velZ);
               posX += velX *= 0.9F;
               posZ += velZ *= 0.9F;
               loss = loss - (Math.abs(velX) + Math.abs(velZ));
               posY += velY += loss;
            } else {
               posY += velY;
            }
         }

         x = (int)posX;
         y = (int)posY - 1;
         z = (int)posZ;
         if (!this.noLeaves) {
            for(int j = 0; j < leavesLength; j += 3) {
               this.placeLeavesBlock(world, new BlockPos(x + leaves[j], y + leaves[j + 1], z + leaves[j + 2]), this.leavesBlock, this.generateFlag);
            }
         }

         for(int k = 0; k < cocoasLength; k += 4) {
            if (rand.nextInt(20) == 0) {
               world.setBlockState(new BlockPos(x + cocoas[k + 1], y + cocoas[k + 2], z + cocoas[k + 3]), Blocks.COCOA.getDefaultState(), this.generateFlag);
            }
         }

         return true;
      }
   }

   public double getRandomDir(EaglercraftRandom rand) {
      switch(rand.nextInt(4)) {
      case 0:
         return -180.0D;
      case 1:
         return -90.0D;
      case 2:
         return 0.0D;
      case 3:
         return 90.0D;
      default:
         return 0.0D;
      }
   }
}
