package com.charles445.aireducer.ai.myrmexold;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateOldMyrmexAlternate extends PathNavigateGround {
   private IAttributeInstance searchRange;

   public PathNavigateOldMyrmexAlternate(EntityLiving entitylivingIn, World worldIn) {
      super(entitylivingIn, worldIn);
      this.searchRange = entitylivingIn.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
   }

   protected PathFinder getPathFinder() {
      this.nodeProcessor = new WalkNodeProcessor();
      this.nodeProcessor.setCanEnterDoors(true);
      this.nodeProcessor.setCanSwim(true);
      return new PathFinder(this.nodeProcessor);
   }

   public Path getPathToPos(BlockPos pos) {
      double distSq = this.entity.getPosition().distanceSq(pos);
      double baseVal = this.searchRange.getAttributeValue();
      if (distSq > baseVal * baseVal) {
         return this.getStuntedPathToPos(pos);
      } else {
         Path path = super.getPathToPos(pos);
         if (path == null) {
            path = this.getStuntedPathToPos(pos);
         }

         return path;
      }
   }

   public Path getStuntedPathToPos(BlockPos pos) {
      BlockPos b = pos.subtract(this.entity.getPosition());
      BlockPos c = this.entity.getPosition().add(b.getX() / 4, b.getY() / 4, b.getZ() / 4);
      return super.getPathToPos(c);
   }

   protected void pathFollow() {
      Vec3d vec3d = this.getEntityPosition();
      int i = this.currentPath.getCurrentPathLength();

      for(int j = this.currentPath.getCurrentPathIndex(); j < this.currentPath.getCurrentPathLength(); ++j) {
         if ((double)this.currentPath.getPathPointFromIndex(j).y != Math.floor(vec3d.y)) {
            i = j;
            break;
         }
      }

      this.maxDistanceToWaypoint = this.entity.width;
      Vec3d vec3d1 = this.currentPath.getCurrentPos();
      if (MathHelper.abs((float)(this.entity.posX - (vec3d1.x + 0.5D))) < this.maxDistanceToWaypoint && MathHelper.abs((float)(this.entity.posZ - (vec3d1.z + 0.5D))) < this.maxDistanceToWaypoint && (float)Math.abs(this.entity.posY - vec3d1.y) <= 1.1F) {
         this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
      }

      int k = MathHelper.ceil(this.entity.width);
      int l = MathHelper.ceil(this.entity.height);
      int i1 = k;

      for(int j1 = i - 1; j1 >= this.currentPath.getCurrentPathIndex(); --j1) {
         if (this.isDirectPathBetweenPoints(vec3d, this.currentPath.getVectorFromIndex(this.entity, j1), k, l, i1)) {
            this.currentPath.setCurrentPathIndex(j1);
            break;
         }
      }

      this.checkForStuck(vec3d);
   }
}
