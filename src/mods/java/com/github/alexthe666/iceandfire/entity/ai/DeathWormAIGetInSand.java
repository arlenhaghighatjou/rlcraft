package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DeathWormAIGetInSand extends EntityAIBase {
   private final EntityDeathWorm creature;
   private double shelterX;
   private double shelterY;
   private double shelterZ;
   private final double movementSpeed;
   private final World world;

   public DeathWormAIGetInSand(EntityDeathWorm theCreatureIn, double movementSpeedIn) {
      this.creature = theCreatureIn;
      this.movementSpeed = movementSpeedIn;
      this.world = theCreatureIn.world;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (!this.creature.isBeingRidden() && !this.creature.isInSand() && (this.creature.getAttackTarget() == null || this.creature.getAttackTarget().isInWater())) {
         Vec3d vec3d = this.findPossibleShelter();
         if (vec3d == null) {
            return false;
         } else {
            this.shelterX = vec3d.x;
            this.shelterY = vec3d.y;
            this.shelterZ = vec3d.z;
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.creature.getNavigator().noPath();
   }

   public void startExecuting() {
      this.creature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
   }

   @Nullable
   private Vec3d findPossibleShelter() {
      EaglercraftRandom random = this.creature.getRNG();
      BlockPos blockpos = new BlockPos(this.creature.posX, this.creature.getEntityBoundingBox().minY, this.creature.posZ);

      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
         if (this.world.getBlockState(blockpos1).getMaterial() == Material.SAND) {
            return new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
         }
      }

      return null;
   }
}
