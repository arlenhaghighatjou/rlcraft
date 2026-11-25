package com.ferreusveritas.dynamictrees.systems.featuregen;

import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.api.IPreGenFeature;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class FeatureGenMound implements IPreGenFeature, IPostGenFeature {
   private static final SimpleVoxmap moundMap = (new SimpleVoxmap(5, 4, 5, new byte[]{0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 2, 0, 0, 1, 1, 1, 0, 1, 2, 2, 2, 1, 1, 2, 2, 2, 1, 1, 2, 2, 2, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0})).setCenter(new BlockPos(2, 3, 2));
   private final int moundCutoffRadius;

   public FeatureGenMound(int moundCutoffRadius) {
      this.moundCutoffRadius = moundCutoffRadius;
   }

   public BlockPos preGeneration(World world, BlockPos rootPos, Species species, int radius, EnumFacing facing, SafeChunkBounds safeBounds, JoCode joCode) {
      if (radius >= this.moundCutoffRadius && safeBounds != SafeChunkBounds.ANY) {
         IBlockState initialDirtState = world.getBlockState(rootPos);
         IBlockState initialUnderState = world.getBlockState(rootPos.down());
         if (initialUnderState.getMaterial() == Material.AIR || initialUnderState.getMaterial() != Material.GROUND && initialUnderState.getMaterial() != Material.ROCK) {
            Biome biome = world.getBiome(rootPos);
            initialUnderState = biome.fillerBlock;
         }

         rootPos = rootPos.up();

         for(SimpleVoxmap.Cell cell : moundMap.getAllNonZeroCells()) {
            IBlockState placeState = cell.getValue() == 1 ? initialDirtState : initialUnderState;
            world.setBlockState(rootPos.add(cell.getPos()), placeState);
         }
      }

      return rootPos;
   }

   public boolean postGeneration(World world, BlockPos rootPos, Species species, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
      if (radius < this.moundCutoffRadius && safeBounds != SafeChunkBounds.ANY) {
         BlockPos treePos = rootPos.up();
         IBlockState belowState = world.getBlockState(rootPos.down());
         IBlockState branchState = world.getBlockState(treePos);
         if (TreeHelper.getTreePart(branchState).getRadius(branchState) > 8) {
            for(CoordUtils.Surround dir : CoordUtils.Surround.values()) {
               BlockPos dPos = rootPos.add(dir.getOffset());
               world.setBlockState(dPos, initialDirtState);
               world.setBlockState(dPos.down(), belowState);
            }

            return true;
         }
      }

      return false;
   }
}
