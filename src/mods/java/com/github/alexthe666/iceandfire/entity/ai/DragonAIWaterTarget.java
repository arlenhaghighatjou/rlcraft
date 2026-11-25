package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DragonAIWaterTarget extends EntityAIBase {
   private EntityIceDragon dragon;

   public DragonAIWaterTarget(EntityIceDragon dragon) {
      this.dragon = dragon;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.dragon != null) {
         if (this.dragon.isSleeping()) {
            return false;
         } else if (!this.dragon.isInWater()) {
            return false;
         } else if (this.dragon.getOwner() != null && this.dragon.getPassengers().contains(this.dragon.getOwner())) {
            return false;
         } else {
            if (this.dragon.waterTarget != null && this.dragon.isTargetBlocked(new Vec3d(this.dragon.waterTarget))) {
               this.dragon.waterTarget = null;
            }

            if (this.dragon.waterTarget != null) {
               return false;
            } else {
               Vec3d vec = this.findWaterTarget();
               if (vec == null) {
                  return false;
               } else {
                  this.dragon.waterTarget = new BlockPos(vec.x, vec.y, vec.z);
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean continueExecuting() {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this.dragon, StoneEntityProperties.class);
      if (!this.dragon.isInWater()) {
         return false;
      } else if (this.dragon.isSleeping()) {
         return false;
      } else if (properties != null && properties.isStone) {
         return false;
      } else {
         return this.dragon.airTarget != null;
      }
   }

   public Vec3d findWaterTarget() {
      return new Vec3d(this.getNearbyWaterTarget());
   }

   public BlockPos getNearbyWaterTarget() {
      if (this.dragon.getAttackTarget() == null) {
         BlockPos pos = DragonUtils.getWaterBlockInView(this.dragon);
         return pos != null && this.dragon.world.getBlockState(pos).getMaterial() == Material.WATER ? pos : this.dragon.getPosition();
      } else {
         return new BlockPos((int)this.dragon.getAttackTarget().posX, (int)this.dragon.getAttackTarget().posY, (int)this.dragon.getAttackTarget().posZ);
      }
   }
}
