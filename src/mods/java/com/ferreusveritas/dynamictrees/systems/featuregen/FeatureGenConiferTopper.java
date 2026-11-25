package com.ferreusveritas.dynamictrees.systems.featuregen;

import com.ferreusveritas.dynamictrees.api.IPostGenFeature;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class FeatureGenConiferTopper implements IPostGenFeature {
   protected final ILeavesProperties leavesProperties;

   public FeatureGenConiferTopper(ILeavesProperties leavesProperties) {
      this.leavesProperties = leavesProperties;
   }

   public boolean postGeneration(World world, BlockPos rootPos, Species species, Biome biome, int radius, List<BlockPos> endPoints, SafeChunkBounds safeBounds, IBlockState initialDirtState) {
      BlockPos highest = (BlockPos)Collections.max(endPoints, (a, b) -> {
         return (boolean)(a.getY() - b.getY());
      });
      world.setBlockState(highest.up(1), this.leavesProperties.getDynamicLeavesState(4));
      world.setBlockState(highest.up(2), this.leavesProperties.getDynamicLeavesState(3));
      world.setBlockState(highest.up(3), this.leavesProperties.getDynamicLeavesState(1));
      return true;
   }
}
