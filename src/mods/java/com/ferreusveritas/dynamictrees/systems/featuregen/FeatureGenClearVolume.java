package com.ferreusveritas.dynamictrees.systems.featuregen;

import com.ferreusveritas.dynamictrees.api.IPreGenFeature;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class FeatureGenClearVolume implements IPreGenFeature {
   private final int height;

   public FeatureGenClearVolume(int height) {
      this.height = height;
   }

   public BlockPos preGeneration(World world, BlockPos rootPos, Species species, int radius, EnumFacing facing, SafeChunkBounds safeBounds, JoCode joCode) {
      for(MutableBlockPos pos : BlockPos.getAllInBoxMutable(rootPos.add(new Vec3i(-1, 1, -1)), rootPos.add(new Vec3i(1, this.height, 1)))) {
         world.setBlockToAir(pos);
      }

      return rootPos;
   }
}
