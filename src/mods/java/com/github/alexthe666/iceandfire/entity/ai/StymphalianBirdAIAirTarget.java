package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianBird;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StymphalianBirdAIAirTarget extends EntityAIBase {
   private EntityStymphalianBird bird;
   private World theWorld;

   public StymphalianBirdAIAirTarget(EntityStymphalianBird bird) {
      this.bird = bird;
      this.theWorld = bird.world;
   }

   public boolean shouldExecute() {
      if (this.bird != null) {
         if (!this.bird.isFlying()) {
            return false;
         } else if (this.bird.isChild()) {
            return false;
         } else if (this.bird.doesWantToLand()) {
            return false;
         } else {
            if (this.bird.airTarget != null && this.bird.isTargetBlocked(new Vec3d(this.bird.airTarget))) {
               this.bird.airTarget = null;
            }

            if (this.bird.airTarget != null) {
               return false;
            } else {
               Vec3d vec = this.findAirTarget();
               if (vec == null) {
                  return false;
               } else {
                  this.bird.airTarget = new BlockPos(vec.x, vec.y, vec.z);
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean continueExecuting() {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this.bird, StoneEntityProperties.class);
      if (!this.bird.isFlying()) {
         return false;
      } else if (this.bird.isChild()) {
         return false;
      } else if (properties != null && properties.isStone) {
         return false;
      } else {
         return this.bird.airTarget != null;
      }
   }

   public Vec3d findAirTarget() {
      return new Vec3d(getNearbyAirTarget(this.bird));
   }

   public static BlockPos getNearbyAirTarget(EntityStymphalianBird bird) {
      if (bird.getAttackTarget() == null) {
         BlockPos pos = DragonUtils.getBlockInViewStymphalian(bird);
         if (pos != null && bird.world.getBlockState(pos).getMaterial() == Material.AIR) {
            return pos;
         } else {
            if (bird.flock != null && bird.flock.isLeader(bird)) {
               bird.flock.setTarget(bird.airTarget);
            }

            return bird.getPosition();
         }
      } else {
         return new BlockPos((double)((int)bird.getAttackTarget().posX), (double)((float)((int)bird.getAttackTarget().posY) + bird.getAttackTarget().getEyeHeight()), (double)((int)bird.getAttackTarget().posZ));
      }
   }
}
