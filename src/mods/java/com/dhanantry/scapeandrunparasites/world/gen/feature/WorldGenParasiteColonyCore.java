package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockColonyCore;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteColonyCore extends WorldGenParasiteGenAbstract {
   public WorldGenParasiteColonyCore(boolean notify) {
      super(notify);
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      int core = 1;
      switch(core) {
      case 1:
         this.placeDirt(worldIn, position.down());
         this.placeCore(worldIn, position, core);

         for(int i = 0; i <= 3; ++i) {
            BlockPos helper = this.directionToGrow(position, i, false);
            if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
               this.placeTrunk(worldIn, helper.down());
            }

            this.placeTrunk(worldIn, helper);
            BlockPos rootH = helper;

            for(int o = 0; o < 2; ++o) {
               if (o == 0) {
                  helper = this.directionToGrow(rootH, (i + 1) % 4, false);
               } else {
                  helper = this.directionToGrow(rootH, (i + 3) % 4, false);
               }

               if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
                  this.placeTrunk(worldIn, helper.down());
               }

               this.placeTrunk(worldIn, helper);
            }

            helper = this.directionToGrow(rootH, i, false);
            if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
               this.placeTrunk(worldIn, helper.down());
            }

            this.placeTrunk(worldIn, helper);
            rootH = helper;

            for(int o = 0; o < 2; ++o) {
               if (o == 0) {
                  helper = this.directionToGrow(rootH, (i + 1) % 4, false);
               } else {
                  helper = this.directionToGrow(rootH, (i + 3) % 4, false);
               }

               if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
                  this.placeTrunk(worldIn, helper.down());
               }

               this.placeTrunk(worldIn, helper);
            }

            helper = this.directionToGrow(rootH, i, false);
            if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
               this.placeTrunk(worldIn, helper.down());
            }

            this.placeTrunk(worldIn, helper);
         }

         position = position.up();
         this.placeTrunk(worldIn, position);
         this.placePeri(worldIn, position);
         position = position.up();
         this.placeTrunk(worldIn, position);
      case 2:
      case 3:
      default:
         return true;
      }
   }

   private void placeTen(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FEELER));
   }

   private void placeTrunk(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteRubbleDense.getDefaultState().withProperty(BlockParasiteRubbleDense.VARIANT, BlockParasiteRubbleDense.EnumType.COLONY));
   }

   private void placeCore(World worldIn, BlockPos pos, int stage) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ColonyHeart.getDefaultState().withProperty(BlockColonyCore.ACTIVE, Integer.valueOf(stage)));
   }

   private void placeDirt(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.getDefaultState());
   }

   private void placeLiquid(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.DeadBlood.getDefaultState());
   }

   private BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
      switch(direction) {
      case 0:
         return center.north(times);
      case 1:
         return center.east(times);
      case 2:
         return center.south(times);
      default:
         return center.west(times);
      }
   }

   private BlockPos placeColumn(World worldIn, BlockPos pos, int in, EaglercraftRandom rand, double extraChance) {
      int current = pos.getY();
      int atm = current;
      int times = 0;

      BlockPos newPos;
      for(newPos = pos; current < atm + in; ++times) {
         this.placeTen(worldIn, newPos);
         newPos = newPos.down();
         ++current;
      }

      this.placeTen(worldIn, newPos);
      return newPos;
   }

   private void placePeri(World worldIn, BlockPos position) {
      for(int i = 0; i <= 3; ++i) {
         BlockPos helper = this.directionToGrow(position, i, false);
         if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
            this.placeTrunk(worldIn, helper.down());
         }

         this.placeTrunk(worldIn, helper);
         BlockPos rootH = helper;

         for(int o = 0; o < 2; ++o) {
            if (o == 0) {
               helper = this.directionToGrow(rootH, (i + 1) % 4, false);
            } else {
               helper = this.directionToGrow(rootH, (i + 3) % 4, false);
            }

            if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
               this.placeTrunk(worldIn, helper.down());
            }

            this.placeTrunk(worldIn, helper);
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
            atm = atm.south();
            break;
         case 11:
            atm = atm.east();
            atm = atm.north();
            break;
         case 20:
            atm = atm.south();
            atm = atm.west();
            break;
         case 21:
            atm = atm.south();
            atm = atm.east();
            break;
         case 30:
            atm = atm.west();
            atm = atm.north();
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
         default:
            atm = atm.south();
            break;
         case 3:
            atm = atm.west();
         }

         return atm;
      }
   }

   private void positionSides(int choice) {
   }
}
