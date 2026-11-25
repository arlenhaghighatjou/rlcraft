package com.lycanitesmobs.core.spawner;

import java.util.Comparator;
import net.minecraft.util.math.BlockPos;

public class CoordSorterNearest implements Comparator {
   public BlockPos coord;

   public CoordSorterNearest(BlockPos coord) {
      this.coord = coord;
   }

   public int compare(Object targetA, Object targetB) {
      double distanceA = this.getDistanceSqCoord((BlockPos)targetA);
      double distanceB = this.getDistanceSqCoord((BlockPos)targetB);
      return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
   }

   public double getDistanceSqCoord(BlockPos targetCoord) {
      return this.coord.distanceSq((double)targetCoord.getX(), (double)targetCoord.getY(), (double)targetCoord.getZ());
   }
}
