package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DeathWormAIFindSandTarget extends EntityAIBase {
   private EntityDeathWorm mob;
   private int range;
   private boolean avoidAttacker;

   public DeathWormAIFindSandTarget(EntityDeathWorm mob, int range) {
      this.mob = mob;
      this.range = range;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.mob.isInSand() && !this.mob.isRiding() && !this.mob.isBeingRidden()) {
         if (this.mob.getRNG().nextFloat() < 0.5F) {
            Path path = this.mob.getNavigator().getPath();
            if (path != null || !this.mob.getNavigator().noPath() && !this.isDirectPathBetweenPoints(this.mob, this.mob.getPositionVector(), new Vec3d((double)path.getFinalPathPoint().x, (double)path.getFinalPathPoint().y, (double)path.getFinalPathPoint().z))) {
               this.mob.getNavigator().clearPath();
            }

            if (this.mob.getNavigator().noPath()) {
               BlockPos vec3 = this.findSandTarget();
               if (vec3 != null) {
                  this.mob.getNavigator().tryMoveToXYZ((double)vec3.getX(), (double)vec3.getY(), (double)vec3.getZ(), 1.0D);
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return false;
   }

   public BlockPos findSandTarget() {
      if (this.mob.getAttackTarget() != null && !this.mob.getAttackTarget().isDead) {
         BlockPos blockpos1 = new BlockPos(this.mob.getAttackTarget());
         return new BlockPos((double)blockpos1.getX(), (double)blockpos1.getY() - 1.0D, (double)blockpos1.getZ());
      } else {
         List<BlockPos> sand = new ArrayList();
         if (this.mob.isTamed() && this.mob.getWormHome() != null) {
            this.range = 25;

            for(int x = this.mob.getWormHome().getX() - this.range; x < this.mob.getWormHome().getX() + this.range; ++x) {
               for(int y = this.mob.getWormHome().getY() - this.range; y < this.mob.getWormHome().getY() + this.range; ++y) {
                  for(int z = this.mob.getWormHome().getZ() - this.range; z < this.mob.getWormHome().getZ() + this.range; ++z) {
                     if (this.mob.world.getBlockState(new BlockPos(x, y, z)).getMaterial() == Material.SAND && this.isDirectPathBetweenPoints(this.mob, this.mob.getPositionVector(), new Vec3d((double)x, (double)y, (double)z))) {
                        sand.add(new BlockPos(x, y, z));
                     }
                  }
               }
            }
         } else {
            for(int x = (int)this.mob.posX - this.range; x < (int)this.mob.posX + this.range; ++x) {
               for(int y = (int)this.mob.posY - this.range; y < (int)this.mob.posY + this.range; ++y) {
                  for(int z = (int)this.mob.posZ - this.range; z < (int)this.mob.posZ + this.range; ++z) {
                     if (this.mob.world.getBlockState(new BlockPos(x, y, z)).getMaterial() == Material.SAND && this.isDirectPathBetweenPoints(this.mob, this.mob.getPositionVector(), new Vec3d((double)x, (double)y, (double)z))) {
                        sand.add(new BlockPos(x, y, z));
                     }
                  }
               }
            }
         }

         return !sand.isEmpty() ? (BlockPos)sand.get(this.mob.getRNG().nextInt(sand.size())) : null;
      }
   }

   public boolean isDirectPathBetweenPoints(Entity entity, Vec3d vec1, Vec3d vec2) {
      return true;
   }
}
