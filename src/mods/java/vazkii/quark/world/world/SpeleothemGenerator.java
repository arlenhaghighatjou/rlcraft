package vazkii.quark.world.world;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.block.BlockSpeleothem;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen;
import vazkii.quark.world.feature.Speleothems;

public class SpeleothemGenerator implements IWorldGenerator {
   public SpeleothemGenerator() {
   }

   public void generate(EaglercraftRandom random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (Speleothems.dimensionConfig.canSpawnHere(world)) {
         int x = chunkX * 16 + 8;
         int z = chunkZ * 16 + 8;
         int spread = 10;
         int tries = Speleothems.tries;
         int innerSpread = 6;
         int innerTries = Speleothems.clusterCount;
         int upperBound = Speleothems.maxHeight;
         int offset = 6;
         if (world.provider.isNether()) {
            upperBound = 128;
            offset = 0;
            tries = Speleothems.netherTries;
            innerTries = Speleothems.netherClusterCount;
         }

         if (upperBound > 0) {
            for(int i = 0; i < tries; ++i) {
               BlockPos target = new BlockPos(x + random.nextInt(spread), random.nextInt(upperBound) + offset, z + random.nextInt(spread));
               if (this.placeSpeleothemCluster(random, world, target, innerSpread, innerTries)) {
                  ++i;
               }
            }
         }

      }
   }

   private boolean placeSpeleothemCluster(EaglercraftRandom random, World world, BlockPos pos, int spread, int tries) {
      if (!this.findAndPlaceSpeleothem(random, world, pos)) {
         return false;
      } else {
         for(int i = 0; i < tries; ++i) {
            BlockPos target = pos.add(random.nextInt(spread * 2 + 1) - spread, random.nextInt(spread + 1) - spread, random.nextInt(spread * 2 + 1) - spread);
            this.findAndPlaceSpeleothem(random, world, target);
         }

         return true;
      }
   }

   private boolean findAndPlaceSpeleothem(EaglercraftRandom random, World world, BlockPos pos) {
      if (!world.isAirBlock(pos)) {
         return false;
      } else {
         int off = world.provider.isNether() ? -1000 : 0;
         boolean up = random.nextBoolean();
         EnumFacing diff = up ? EnumFacing.UP : EnumFacing.DOWN;
         if (!up && world.canBlockSeeSky(pos)) {
            return false;
         } else {
            IBlockState stateAt;
            while(true) {
               pos = pos.offset(diff);
               stateAt = world.getBlockState(pos);
               ++off;
               if (pos.getY() <= 4 || pos.getY() >= 200 || stateAt.isFullBlock() || off >= 10) {
                  break;
               }
            }

            Block type = this.getSpeleothemType(stateAt);
            this.placeSpeleothem(random, world, pos, type, !up);
            return true;
         }
      }
   }

   private void placeSpeleothem(EaglercraftRandom random, World world, BlockPos pos, Block type, boolean up) {
      if (type != null) {
         EnumFacing diff = up ? EnumFacing.UP : EnumFacing.DOWN;
         int size = random.nextInt(3) == 0 ? 2 : 3;
         if (!up && random.nextInt(20) == 0) {
            size = 1;
         }

         for(int i = 0; i < size; ++i) {
            pos = pos.offset(diff);
            if (!world.isAirBlock(pos)) {
               return;
            }

            BlockSpeleothem.EnumSize sizeType = BlockSpeleothem.EnumSize.values()[size - i - 1];
            IBlockState targetBlock = type.getDefaultState().withProperty(BlockSpeleothem.SIZE, sizeType);
            world.setBlockState(pos, targetBlock);
         }

      }
   }

   private Block getSpeleothemType(IBlockState state) {
      Block block = state.getBlock();
      if (block == Blocks.NETHERRACK) {
         return Speleothems.netherrack_speleothem;
      } else {
         if (block == Blocks.STONE) {
            switch((EnumType)state.getValue(BlockStone.VARIANT)) {
            case STONE:
               return Speleothems.stone_speleothem;
            case ANDESITE:
               return Speleothems.andesite_speleothem;
            case GRANITE:
               return Speleothems.granite_speleothem;
            case DIORITE:
               return Speleothems.diorite_speleothem;
            }
         } else {
            if (block == Basalt.basalt) {
               return Speleothems.basalt_speleothem;
            }

            if (block == RevampStoneGen.marble) {
               return Speleothems.marble_speleothem;
            }

            if (block == RevampStoneGen.limestone) {
               return Speleothems.limestone_speleothem;
            }

            if (block == RevampStoneGen.jasper) {
               return Speleothems.jasper_speleothem;
            }

            if (block == RevampStoneGen.slate) {
               return Speleothems.slate_speleothem;
            }
         }

         return null;
      }
   }
}
