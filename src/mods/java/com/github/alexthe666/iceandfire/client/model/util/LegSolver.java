package com.github.alexthe666.iceandfire.client.model.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LegSolver {
   public final LegSolver.Leg[] legs;

   public LegSolver(LegSolver.Leg... legs) {
      this.legs = legs;
   }

   public final void update(EntityLivingBase entity) {
      double sideTheta = (double)entity.renderYawOffset / 57.29577951308232D;
      double sideX = Math.cos(sideTheta);
      double sideZ = Math.sin(sideTheta);
      double forwardTheta = sideTheta + 1.5707963267948966D;
      double forwardX = Math.cos(forwardTheta);
      double forwardZ = Math.sin(forwardTheta);

      for(LegSolver.Leg leg : this.legs) {
         leg.update(entity, sideX, sideZ, forwardX, forwardZ);
      }

   }

   public static final class Leg {
      public final float forward;
      public final float side;
      private float height;
      private float prevHeight;

      public Leg(float forward, float side) {
         this.forward = forward;
         this.side = side;
      }

      public float getHeight(float delta) {
         return this.prevHeight + (this.height - this.prevHeight) * delta;
      }

      public void update(EntityLivingBase entity, double sideX, double sideZ, double forwardX, double forwardZ) {
         this.prevHeight = this.height;
         this.height = this.settle(entity, entity.posX + sideX * (double)this.side + forwardX * (double)this.forward, entity.posY, entity.posZ + sideZ * (double)this.side + forwardZ * (double)this.forward, this.height);
      }

      private float settle(EntityLivingBase entity, double x, double y, double z, float height) {
         BlockPos pos = new BlockPos(x, y + 0.001D, z);
         float dist = this.getDistance(entity.world, pos);
         if ((double)(1.0F - dist) < 0.001D) {
            dist = this.getDistance(entity.world, pos.down()) + (float)y % 1.0F;
         } else {
            dist = (float)((double)dist - (1.0D - y % 1.0D));
         }

         if (entity.onGround && height <= dist) {
            return height == dist ? height : Math.min(height + 0.3F, dist);
         } else {
            return height > 0.0F ? Math.max(height - 0.4F, dist) : height;
         }
      }

      private float getDistance(World world, BlockPos pos) {
         IBlockState state = world.getBlockState(pos);
         AxisAlignedBB aabb = state.getCollisionBoundingBox(world, pos);
         return aabb == null ? 1.0F : 1.0F - Math.min((float)aabb.maxY, 1.0F);
      }
   }
}
