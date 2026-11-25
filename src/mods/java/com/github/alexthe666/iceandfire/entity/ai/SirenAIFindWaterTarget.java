package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntitySiren;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SirenAIFindWaterTarget extends EntityAIBase {
   private EntitySiren mob;

   public SirenAIFindWaterTarget(EntitySiren mob) {
      this.mob = mob;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (!this.mob.isInWater()) {
         return false;
      } else {
         if (this.mob.getRNG().nextFloat() < 0.5F) {
            Path path = this.mob.getNavigator().getPath();
            if (path != null && path.getFinalPathPoint() != null || !this.mob.getNavigator().noPath() && !this.mob.isDirectPathBetweenPoints(this.mob.getPositionVector(), new Vec3d((double)path.getFinalPathPoint().x, (double)path.getFinalPathPoint().y, (double)path.getFinalPathPoint().z))) {
               this.mob.getNavigator().clearPath();
            }

            if (this.mob.getNavigator().noPath()) {
               Vec3d vec3 = this.findWaterTarget();
               if (vec3 != null) {
                  this.mob.getNavigator().tryMoveToXYZ(vec3.x, vec3.y, vec3.z, 1.0D);
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return false;
   }

   public Vec3d findWaterTarget() {
      if (this.mob.getAttackTarget() != null && !this.mob.getAttackTarget().isDead) {
         BlockPos blockpos1 = new BlockPos(this.mob.getAttackTarget());
         return new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
      } else {
         List<Vec3d> water = new ArrayList();
         List<Vec3d> singTargets = new ArrayList();

         for(int x = (int)this.mob.posX - 5; x < (int)this.mob.posX + 5; ++x) {
            for(int y = (int)this.mob.posY - 5; y < (int)this.mob.posY + 5; ++y) {
               for(int z = (int)this.mob.posZ - 5; z < (int)this.mob.posZ + 5; ++z) {
                  if (this.mob.wantsToSing() && this.mob.world.getBlockState(new BlockPos(x, y, z)).getMaterial().isSolid() && this.mob.world.isAirBlock(new BlockPos(x, y + 1, z)) && this.mob.isDirectPathBetweenPoints(this.mob.getPositionVector(), new Vec3d((double)x, (double)(y + 1), (double)z))) {
                     singTargets.add(new Vec3d((double)x, (double)(y + 1), (double)z));
                  }

                  if (this.mob.world.getBlockState(new BlockPos(x, y, z)).getMaterial() == Material.WATER && this.mob.isDirectPathBetweenPoints(this.mob.getPositionVector(), new Vec3d((double)x, (double)y, (double)z))) {
                     water.add(new Vec3d((double)x, (double)y, (double)z));
                  }
               }
            }
         }

         if (!singTargets.isEmpty()) {
            return (Vec3d)singTargets.get(this.mob.getRNG().nextInt(singTargets.size()));
         } else if (!water.isEmpty()) {
            return (Vec3d)water.get(this.mob.getRNG().nextInt(water.size()));
         } else {
            return null;
         }
      }
   }
}
