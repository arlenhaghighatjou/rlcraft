package com.lycanitesmobs.core.entity.goals;

import java.util.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class TargetSorterNearest implements Comparator {
   private final Entity host;

   public TargetSorterNearest(Entity setHost) {
      this.host = setHost;
   }

   public int compare(Object objectA, Object objectB) {
      if (objectA instanceof Entity && objectB instanceof Entity) {
         return this.compareDistanceSq((Entity)objectA, (Entity)objectB);
      } else {
         return objectA instanceof BlockPos && objectB instanceof BlockPos ? this.compareDistanceSq((BlockPos)objectA, (BlockPos)objectB) : 0;
      }
   }

   public int compareDistanceSq(Entity targetA, Entity targetB) {
      double distanceA = (double)this.host.getDistance(targetA);
      double distanceB = (double)this.host.getDistance(targetB);
      return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
   }

   public int compareDistanceSq(BlockPos targetA, BlockPos targetB) {
      BlockPos hostCoords = new BlockPos((int)this.host.posX, (int)this.host.posY, (int)this.host.posZ);
      double distanceA = hostCoords.getDistance(targetA.getX(), targetA.getY(), targetA.getZ());
      double distanceB = hostCoords.getDistance(targetB.getX(), targetB.getY(), targetB.getZ());
      return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
   }
}
