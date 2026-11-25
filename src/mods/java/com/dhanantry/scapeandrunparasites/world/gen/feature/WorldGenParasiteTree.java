package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteTree extends WorldGenParasiteTreeAbstract {
   public WorldGenParasiteTree(boolean notify) {
      super(notify);
   }

   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = 20;
      boolean flag = true;
      if (worldIn.getBlockState(position.down()).getBlock() == Blocks.AIR) {
         return false;
      } else if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
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
                  if (j >= 0 && j < worldIn.getHeight()) {
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
            int first = rand.nextInt(4);
            BlockPos var11 = this.directionToGrow(position, first, false);
            this.placeTrunk(worldIn, var11);

            while(!worldIn.getBlockState(var11.down()).isFullBlock() && var11.down().getY() >= 1) {
               var11 = var11.down();
               this.placeTrunk(worldIn, var11);
            }

            ++first;
            var11 = this.directionToGrow(position, first, false);
            this.placeTrunk(worldIn, var11);

            while(!worldIn.getBlockState(var11.down()).isFullBlock() && var11.down().getY() >= 1) {
               var11 = var11.down();
               this.placeTrunk(worldIn, var11);
            }

            var11 = position;

            while(!worldIn.getBlockState(var11.down()).isFullBlock() && var11.down().getY() >= 1) {
               var11 = var11.down();
               this.placeTrunk(worldIn, var11);
            }

            var11 = this.placeColumn(worldIn, position, rand.nextInt(3) + 5, rand, -1.0D);
            first = rand.nextInt(4);
            var11 = this.directionToGrow(var11, first, false);
            var11 = this.placeColumn(worldIn, var11, rand.nextInt(3) + 5, rand, 0.3D);
            if (rand.nextDouble() <= 0.3D) {
               return true;
            } else {
               var11 = this.directionToGrow(var11, rand.nextInt(4), false);
               var11 = this.placeColumn(worldIn, var11, rand.nextInt(3) + 5, rand, 0.3D);
               if (rand.nextDouble() <= 0.3D) {
                  return true;
               } else {
                  var11 = this.directionToGrow(var11, rand.nextInt(4), false);
                  this.placeColumn(worldIn, var11, rand.nextInt(3) + 5, rand, 0.3D);
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   private void placeTrunk(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteTrunk.getDefaultState().withProperty(BlockParasiteTrunk.VARIANT, BlockParasiteTrunk.EnumType.TREE));
   }

   private void placeTrunk(World worldIn, BlockPos pos, int direction) {
      switch(direction) {
      case 1:
         this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteTrunk.getDefaultState().withProperty(BlockParasiteTrunk.VARIANT, BlockParasiteTrunk.EnumType.TREE));
      case 2:
      case 3:
      default:
      }
   }

   private void placeCanister(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteCanister.getDefaultState().withProperty(BlockParasiteCanister.VARIANT, BlockParasiteCanister.EnumType.SAC));
   }

   private BlockPos placeColumn(World worldIn, BlockPos pos, int in, Random rand, double extraChance) {
      int current = pos.getY();
      int atm = current;
      int times = 0;
      BlockPos newPos = pos;
      if (worldIn.getBlockState(pos.down()).getBlock() == Blocks.AIR && rand.nextDouble() <= 0.3D + extraChance) {
         this.placeCanister(worldIn, pos.down());
      }

      while(current < atm + in) {
         if (times == 2 && extraChance != -1.0D) {
            int bone = rand.nextInt(4);
            int btwo = rand.nextInt(4);
            if (bone == btwo) {
               ++bone;
            }

            this.addBranchs(worldIn, newPos, rand, rand.nextInt(3) + 1, bone);
            this.addBranchs(worldIn, newPos, rand, rand.nextInt(3) + 1, btwo);
         }

         this.placeTrunk(worldIn, newPos);
         newPos = newPos.up();
         ++current;
         ++times;
      }

      this.placeTrunk(worldIn, newPos);
      return newPos;
   }

   private void addBranchs(World worldIn, BlockPos pos, Random rand, int size, int direction) {
      pos = this.directionToGrow(pos, direction, false);
      this.placeTrunk(worldIn, pos);
      pos = pos.up();
      pos = this.directionToGrow(pos, direction, false);
      int current = 0;

      int curve;
      for(curve = direction * 10 + rand.nextInt(2); current < size; ++current) {
         this.placeTrunk(worldIn, pos);
         if (rand.nextDouble() <= 0.25D) {
            this.placeCanister(worldIn, pos.down());
         }

         if (rand.nextDouble() <= 0.5D) {
            pos = this.directionToGrow(pos, direction, false);
         } else {
            pos = this.directionToGrow(pos, curve, true);
         }
      }

      pos = pos.down();
      this.placeTrunk(worldIn, pos);
      if (rand.nextDouble() <= 0.25D) {
         this.placeCanister(worldIn, pos.down());
      }

      if (rand.nextDouble() <= 0.5D) {
         if (rand.nextDouble() <= 0.5D) {
            pos = this.directionToGrow(pos.down(), direction, false);
         } else {
            pos = this.directionToGrow(pos.down(), curve, true);
         }

         this.placeTrunk(worldIn, pos);
         if (rand.nextDouble() <= 0.25D) {
            this.placeCanister(worldIn, pos.down());
         }
      }

   }

   private BlockPos directionToGrow(BlockPos atm, int choice, boolean sideCurse) {
      if (sideCurse) {
         switch(choice) {
         case 0:
            atm = atm.north();
            atm = atm.east();
            break;
         case 1:
            atm = atm.north();
            atm = atm.west();
            break;
         case 10:
            atm = atm.east();
            atm = atm.north();
            break;
         case 11:
            atm = atm.east();
            atm = atm.south();
            break;
         case 20:
            atm = atm.west();
            atm = atm.north();
            break;
         case 30:
            atm = atm.south();
            atm = atm.east();
            break;
         case 31:
            atm = atm.south();
            atm = atm.west();
            break;
         default:
            atm = atm.west();
            atm = atm.south();
         }

         return atm;
      } else {
         switch(choice) {
         case 0:
            atm = atm.north();
            break;
         case 1:
            atm = atm.east();
            break;
         case 2:
            atm = atm.west();
            break;
         default:
            atm = atm.south();
         }

         return atm;
      }
   }
}
