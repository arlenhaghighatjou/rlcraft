package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteBush extends WorldGenParasiteGenAbstract {
   private final IBlockState tallGrassState;
   private int type;
   private boolean parasite;

   public WorldGenParasiteBush(boolean notify, BlockParasiteBush.EnumType variant, int type) {
      super(notify);
      this.tallGrassState = SRPBlocks.ParasiteBush.getDefaultState().withProperty(BlockParasiteBush.VARIANT, variant);
      this.type = type;
      this.parasite = true;
   }

   public WorldGenParasiteBush(boolean notify, BlockInfestedBush.EnumType variant, int type) {
      super(notify);
      this.tallGrassState = SRPBlocks.InfestedBush.getDefaultState().withProperty(BlockInfestedBush.VARIANT, variant);
      this.type = type;
      this.parasite = false;
   }

   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (this.type == 3) {
         this.flowerGen(worldIn, rand, position);
         return true;
      } else if (this.type == 4) {
         for(int i = 0; i < 128; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (this.parasite) {
               if (worldIn.getBlockState(blockpos).getBlock() == Blocks.AIR && SRPBlocks.ParasiteBush.canBlockStay(worldIn, blockpos, this.tallGrassState) && this.checkFloor(worldIn, blockpos)) {
                  this.VinGen(worldIn, rand, blockpos);
               }
            } else if (worldIn.getBlockState(blockpos).getBlock() == Blocks.AIR && SRPBlocks.InfestedBush.canBlockStay(worldIn, blockpos, this.tallGrassState) && this.checkFloor(worldIn, blockpos)) {
               this.VinGen(worldIn, rand, blockpos);
            }
         }

         this.VinGen(worldIn, rand, position);
         return true;
      } else {
         for(IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock() == Blocks.AIR || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position)) {
            position = position.down();
         }

         switch(this.type) {
         case 1:
            for(int i = 0; i < 128; ++i) {
               BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
               if (this.parasite) {
                  if (worldIn.getBlockState(blockpos).getBlock() == Blocks.AIR && SRPBlocks.ParasiteBush.canBlockStay(worldIn, blockpos, this.tallGrassState) && this.checkFloor(worldIn, blockpos)) {
                     worldIn.setBlockState(blockpos, this.tallGrassState, 2);
                  }
               } else if (worldIn.getBlockState(blockpos).getBlock() == Blocks.AIR && SRPBlocks.InfestedBush.canBlockStay(worldIn, blockpos, this.tallGrassState) && this.checkFloor(worldIn, blockpos)) {
                  worldIn.setBlockState(blockpos, this.tallGrassState, 2);
               }
            }

            return true;
         case 2:
            for(int i = 0; i < 4; ++i) {
               BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
               if (this.parasite) {
                  if (worldIn.getBlockState(blockpos).getBlock() == Blocks.AIR && SRPBlocks.ParasiteBush.canBlockStay(worldIn, blockpos, this.tallGrassState) && this.checkFloor(worldIn, blockpos)) {
                     worldIn.setBlockState(blockpos, this.tallGrassState, 2);
                  }
               } else if (worldIn.getBlockState(blockpos).getBlock() == Blocks.AIR && SRPBlocks.InfestedBush.canBlockStay(worldIn, blockpos, this.tallGrassState) && this.checkFloor(worldIn, blockpos)) {
                  worldIn.setBlockState(blockpos, this.tallGrassState, 2);
               }
            }
         }

         return true;
      }
   }

   public boolean flowerGen(World worldIn, Random rand, BlockPos position) {
      for(int i = 0; i < 64; ++i) {
         BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (this.parasite) {
            if (worldIn.getBlockState(blockpos).getBlock() == Blocks.AIR && blockpos.getY() < 255 && SRPBlocks.ParasiteBush.canBlockStay(worldIn, blockpos, this.tallGrassState) && this.checkFloor(worldIn, blockpos)) {
               worldIn.setBlockState(blockpos, this.tallGrassState, 2);
            }
         } else if (worldIn.getBlockState(blockpos).getBlock() == Blocks.AIR && blockpos.getY() < 255 && SRPBlocks.InfestedBush.canBlockStay(worldIn, blockpos, this.tallGrassState) && this.checkFloor(worldIn, blockpos)) {
            worldIn.setBlockState(blockpos, this.tallGrassState, 2);
         }
      }

      return true;
   }

   public boolean VinGen(World worldIn, Random rand, BlockPos position) {
      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.getBlockState(blockpos).getBlock() == Blocks.AIR) {
            int j = 1 + rand.nextInt(rand.nextInt(3) + 3);

            for(int k = 0; k < j; ++k) {
               if (this.parasite) {
                  if (SRPBlocks.ParasiteBush.canBlockStay(worldIn, blockpos.up(k), this.tallGrassState) && worldIn.getBlockState(blockpos.up(k)).getBlock() == Blocks.AIR) {
                     worldIn.setBlockState(blockpos.up(k), this.tallGrassState, 2);
                  }
               } else if (SRPBlocks.InfestedBush.canBlockStay(worldIn, blockpos, this.tallGrassState) && worldIn.getBlockState(blockpos.up(k)).getBlock() == Blocks.AIR) {
                  worldIn.setBlockState(blockpos.up(k), this.tallGrassState, 2);
               }
            }
         }
      }

      return true;
   }

   private boolean checkFloor(World worldIn, BlockPos position) {
      return worldIn.getBlockState(position.down()).isFullCube();
   }
}
