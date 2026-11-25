package lykrast.defiledlands.common.world.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import lykrast.defiledlands.common.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class WorldGenTenebra extends WorldGenAbstractTree {
   public WorldGenTenebra(boolean notify) {
      super(notify);
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      int i = rand.nextInt(6) + rand.nextInt(6) + 3;
      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
         for(int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
            int k = 1;
            if (j == position.getY()) {
               k = 0;
            }

            if (j >= position.getY() + 1 + i - 2) {
               k = 2;
            }

            MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

            for(int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
               for(int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                  if (j >= 0 && j < 256) {
                     if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.setPos(l, j, i1))) {
                        flag = false;
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         } else {
            Block soil = worldIn.getBlockState(position.down()).getBlock();
            if ((soil == ModBlocks.grassDefiled || soil == ModBlocks.dirtDefiled) && position.getY() < worldIn.getHeight() - i - 1) {
               this.setDirtAt(worldIn, position.down());
               BlockPos mutable = new BlockPos(position);
               this.placeLogAt(worldIn, mutable);

               for(int j = 1; j <= i; ++j) {
                  mutable = mutable.up();
                  if (rand.nextInt(3) == 0) {
                     mutable = mutable.offset(Plane.HORIZONTAL.random(rand));
                  }

                  if (!this.canGrowInto(worldIn.getBlockState(mutable).getBlock())) {
                     break;
                  }

                  this.placeLogAt(worldIn, mutable);

                  for(EnumFacing e : EnumFacing.HORIZONTALS) {
                     if (rand.nextInt(6) == 0) {
                        this.placeLeafAt(worldIn, mutable.offset(e));
                     }
                  }
               }

               if (rand.nextInt(6) == 0) {
                  this.placeLeafAt(worldIn, mutable.up());
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private void placeLogAt(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, ModBlocks.tenebraLog.getDefaultState());
   }

   private void placeLeafAt(World worldIn, BlockPos pos) {
      IBlockState state = worldIn.getBlockState(pos);
      if (state.getBlock().isAir(state, worldIn, pos) || state.getBlock().isLeaves(state, worldIn, pos)) {
         this.setBlockAndNotifyAdequately(worldIn, pos, ModBlocks.tenebraLeaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)));
      }

   }

   protected boolean canGrowInto(Block blockType) {
      return super.canGrowInto(blockType) || blockType == ModBlocks.dirtDefiled || blockType == ModBlocks.grassDefiled || blockType == ModBlocks.tenebraLog;
   }

   protected void setDirtAt(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() == ModBlocks.grassDefiled) {
         this.setBlockAndNotifyAdequately(worldIn, pos, ModBlocks.dirtDefiled.getDefaultState());
      }

   }
}
