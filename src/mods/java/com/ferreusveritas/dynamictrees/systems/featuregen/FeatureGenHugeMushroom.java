package com.ferreusveritas.dynamictrees.systems.featuregen;

import com.ferreusveritas.dynamictrees.api.IFullGenFeature;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.BlockBounds;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import com.google.common.collect.Iterables;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.BlockHugeMushroom.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class FeatureGenHugeMushroom implements IFullGenFeature {
   private final Block mushroomType;
   private int height = -1;
   static final SimpleVoxmap brnCap = (new SimpleVoxmap(7, 1, 7, new byte[]{0, 1, 2, 2, 2, 3, 0, 1, 5, 5, 5, 5, 5, 3, 4, 5, 5, 5, 5, 5, 6, 4, 5, 5, 5, 5, 5, 6, 4, 5, 5, 5, 5, 5, 6, 7, 5, 5, 5, 5, 5, 9, 0, 7, 8, 8, 8, 9, 0})).setCenter(new BlockPos(3, 0, 3));
   static final SimpleVoxmap brnCapMedium = (new SimpleVoxmap(5, 1, 5, new byte[]{0, 1, 2, 3, 0, 1, 5, 5, 5, 3, 4, 5, 5, 5, 6, 7, 5, 5, 5, 9, 0, 7, 8, 9, 0})).setCenter(new BlockPos(2, 0, 2));
   static final SimpleVoxmap brnCapSmall = (new SimpleVoxmap(3, 1, 3, new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9})).setCenter(new BlockPos(1, 0, 1));
   static final SimpleVoxmap redCap = (new SimpleVoxmap(5, 4, 5, new byte[]{0, 1, 2, 3, 0, 1, 0, 0, 0, 3, 4, 0, 10, 0, 6, 7, 0, 0, 0, 9, 0, 7, 8, 9, 0, 0, 1, 2, 3, 0, 1, 0, 0, 0, 3, 4, 0, 10, 0, 6, 7, 0, 0, 0, 9, 0, 7, 8, 9, 0, 0, 1, 2, 3, 0, 1, 0, 0, 0, 3, 4, 0, 10, 0, 6, 7, 0, 0, 0, 9, 0, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 0, 0, 4, 5, 6, 0, 0, 7, 8, 9, 0, 0, 0, 0, 0, 0})).setCenter(new BlockPos(2, 3, 2));
   static final SimpleVoxmap redCapShort = (new SimpleVoxmap(5, 3, 5, new byte[]{0, 1, 2, 3, 0, 1, 0, 0, 0, 3, 4, 0, 10, 0, 6, 7, 0, 0, 0, 9, 0, 7, 8, 9, 0, 0, 1, 2, 3, 0, 1, 0, 0, 0, 3, 4, 0, 10, 0, 6, 7, 0, 0, 0, 9, 0, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 0, 0, 4, 5, 6, 0, 0, 7, 8, 9, 0, 0, 0, 0, 0, 0})).setCenter(new BlockPos(2, 2, 2));
   static final SimpleVoxmap redCapSmall = (new SimpleVoxmap(3, 2, 3, new byte[]{1, 2, 3, 4, 10, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9})).setCenter(new BlockPos(1, 1, 1));

   public FeatureGenHugeMushroom(Block block) {
      this.mushroomType = block;
   }

   public FeatureGenHugeMushroom() {
      this.mushroomType = null;
   }

   public FeatureGenHugeMushroom setHeight(int height) {
      this.height = height;
      return this;
   }

   protected SimpleVoxmap getCapForHeight(Block mushroomBlock, int height) {
      if (mushroomBlock == Blocks.BROWN_MUSHROOM_BLOCK) {
         switch(height) {
         case 2:
         case 3:
            return brnCapSmall;
         case 4:
         case 5:
            return brnCapMedium;
         default:
            return brnCap;
         }
      } else {
         switch(height) {
         case 2:
            return brnCapSmall;
         case 3:
            return redCapSmall;
         case 4:
            return redCapShort;
         default:
            return redCap;
         }
      }
   }

   protected int getMushroomHeight(World world, BlockPos rootPos, Biome biome, Random random, int radius, SafeChunkBounds safeBounds) {
      return this.height > 0 ? this.height : random.nextInt(9) + 2;
   }

   public boolean generate(World world, BlockPos rootPos, Species species, Biome biome, Random random, int radius, SafeChunkBounds safeBounds) {
      BlockPos genPos = rootPos.up();
      int height = this.getMushroomHeight(world, rootPos, biome, random, radius, safeBounds);
      IBlockState soilState = world.getBlockState(rootPos);
      if (species.isAcceptableSoilForWorldgen(world, rootPos, soilState)) {
         Block mushroomBlock = this.mushroomType;
         if (mushroomBlock == null) {
            mushroomBlock = random.nextBoolean() ? Blocks.BROWN_MUSHROOM_BLOCK : Blocks.RED_MUSHROOM_BLOCK;
         }

         SimpleVoxmap capMap = this.getCapForHeight(mushroomBlock, height);
         BlockPos capPos = genPos.up(height - 1);
         BlockBounds capBounds = capMap.getBounds().move(capPos);
         if (safeBounds.inBounds(capBounds, true)) {
            for(MutableBlockPos mutPos : Iterables.concat(BlockPos.getAllInBoxMutable(BlockPos.ORIGIN.down(capMap.getLenY()), BlockPos.ORIGIN.down(height - 1)), capMap.getAllNonZero())) {
               BlockPos dPos = mutPos.add(capPos);
               IBlockState state = world.getBlockState(dPos);
               if (!state.getBlock().canBeReplacedByLeaves(state, world, dPos) || !state.getBlock().isReplaceable(world, dPos)) {
                  return false;
               }
            }

            for(SimpleVoxmap.Cell cell : capMap.getAllNonZeroCells()) {
               EnumType mushroomType = EnumType.byMetadata(cell.getValue());
               world.setBlockState(capPos.add(cell.getPos()), mushroomBlock.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, mushroomType));
            }

            int stemLen = height - capMap.getLenY();
            IBlockState stemBlock = mushroomBlock.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, EnumType.STEM);

            for(int y = 0; y < stemLen; ++y) {
               world.setBlockState(genPos.up(y), stemBlock);
            }

            return true;
         }
      }

      return false;
   }
}
