package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenParasiteColonyBase extends WorldGenParasiteGenAbstract {
   protected int type;
   protected IBlockState floor = SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.DIRT);
   protected IBlockState tacle = SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FEELER);
   protected IBlockState wall = SRPBlocks.ParasiteRubbleDense.getDefaultState().withProperty(BlockParasiteRubbleDense.VARIANT, BlockParasiteRubbleDense.EnumType.WALL);
   protected IBlockState floorColony = SRPBlocks.ParasiteRubble.getDefaultState().withProperty(BlockParasiteRubble.VARIANT, BlockParasiteRubble.EnumType.FLESH);

   public WorldGenParasiteColonyBase(boolean notify, int stage) {
      super(notify);
      this.type = stage;
   }

   protected void placeBlock(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockAndNotifyAdequately(worldIn, pos, state);
   }

   protected void placeVine(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteBush.getDefaultState().withProperty(BlockParasiteBush.VARIANT, BlockParasiteBush.EnumType.BINE));
   }

   protected void placeReplacement(World worldIn, BlockPos pos, IBlockState in) {
      this.setBlockAndNotifyAdequately(worldIn, pos, in);
   }

   protected BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
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

   protected BlockPos placeColumn(World worldIn, BlockPos pos, int in, EaglercraftRandom rand, double extraChance, IBlockState state) {
      int current = pos.getY();
      int atm = current;
      int times = 0;

      BlockPos newPos;
      for(newPos = pos; current < atm + in; ++times) {
         this.placeBlock(worldIn, newPos, state);
         newPos = newPos.down();
         ++current;
      }

      this.placeBlock(worldIn, newPos, state);
      return newPos;
   }

   protected BlockPos directionToGrow(BlockPos atm, int choice, boolean sideCurse) {
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

   protected void addVines(World worldIn, BlockPos position, EaglercraftRandom rand, int longer) {
      if (worldIn.getBlockState(position).getBlock() == Blocks.AIR) {
         this.placeVine(worldIn, position);

         for(int chance = longer; worldIn.getBlockState(position.down()).getBlock() == Blocks.AIR && worldIn.getBlockState(position.down(2)).getBlock() == Blocks.AIR; --chance) {
            if (rand.nextInt(chance) == 0) {
               return;
            }

            position = position.down();
            this.placeVine(worldIn, position);
         }

      }
   }

   protected void replaceLayer(World worldIn, BlockPos position, int range, IBlockState toReplace, IBlockState in) {
      int xx = position.getX();
      int zz = position.getZ();
      int yy = position.getY();

      for(int x = xx - range; x <= xx + range; ++x) {
         for(int z = zz - range; z <= zz + range; ++z) {
            BlockPos neww = new BlockPos(x, yy, z);
            if (worldIn.getBlockState(neww) == toReplace) {
               this.placeReplacement(worldIn, neww, in);
            }
         }
      }

   }

   protected void addFloor(World worldIn, BlockPos position, int range, boolean fill) {
      if (fill) {
         this.placeReplacement(worldIn, position, this.floorColony);
      }

      int offsetN = 2;

      for(BlockPos atm = position.north(); !(worldIn.getBlockState(atm).getBlock() instanceof BlockBase); offsetN += 2) {
         this.placeReplacement(worldIn, atm, this.floorColony);
         this.genFloorFloor(worldIn, atm.down(), 15, fill);
         this.placeReplacement(worldIn, atm.south(offsetN), this.floorColony);
         this.genFloorFloor(worldIn, atm.south(offsetN).down(), 15, fill);
         atm = atm.north();
      }

      for(BlockPos here = position.east(1); !(worldIn.getBlockState(here).getBlock() instanceof BlockBase); here = here.east()) {
         int offsetN = 2;

         for(BlockPos atm = here.north(); !(worldIn.getBlockState(atm).getBlock() instanceof BlockBase); offsetN += 2) {
            this.placeReplacement(worldIn, atm, this.floorColony);
            this.genFloorFloor(worldIn, atm.down(), 15, fill);
            this.placeReplacement(worldIn, atm.south(offsetN), this.floorColony);
            this.genFloorFloor(worldIn, atm.south(offsetN).down(), 15, fill);
            atm = atm.north();
         }

         this.placeReplacement(worldIn, here, this.floorColony);
         this.genFloorFloor(worldIn, here.down(), 15, fill);
      }

      for(BlockPos var9 = position.west(1); !(worldIn.getBlockState(var9).getBlock() instanceof BlockBase); var9 = var9.west()) {
         int offsetN = 2;

         for(BlockPos atm = var9.north(); !(worldIn.getBlockState(atm).getBlock() instanceof BlockBase); offsetN += 2) {
            this.placeReplacement(worldIn, atm, this.floorColony);
            this.genFloorFloor(worldIn, atm.down(), 15, fill);
            this.placeReplacement(worldIn, atm.south(offsetN), this.floorColony);
            this.genFloorFloor(worldIn, atm.south(offsetN).down(), 15, fill);
            atm = atm.north();
         }

         this.placeReplacement(worldIn, var9, this.floorColony);
         this.genFloorFloor(worldIn, var9.down(), 15, fill);
      }

      if (!fill) {
         this.addFloorSpace(worldIn, position);
      }

   }

   private void genFloorFloor(World worldIn, BlockPos position, int range, boolean fill) {
      if (fill) {
         for(BlockPos filler = position; (worldIn.getBlockState(filler).getBlock() == Blocks.AIR || worldIn.getBlockState(filler).getBlock() == SRPBlocks.ParasiteBush || worldIn.getBlockState(filler).getBlock() instanceof BlockLeaves || worldIn.getBlockState(filler).getBlock() instanceof BlockBush) && range > 0; --range) {
            this.placeReplacement(worldIn, filler, this.floor);
            filler = filler.down();
         }
      }

   }

   private void addFloorSpace(World worldIn, BlockPos position) {
      int xx = position.getX();
      int zz = position.getZ();
      int yy = position.getY();
      int range = 1;

      for(int x = xx - range; x <= xx + range; ++x) {
         for(int z = zz - range; z <= zz + range; ++z) {
            BlockPos neww = new BlockPos(x, yy, z);
            if (worldIn.getBlockState(neww) == this.floorColony) {
               this.placeReplacement(worldIn, neww, Blocks.AIR.getDefaultState());
            }
         }
      }

   }

   protected void addMobSpawner(World worldIn, BlockPos position, int type, double chance) {
   }
}
