package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import java.util.Random;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HippogryphAIAirTarget extends EntityAIBase {
   private EntityHippogryph hippogryph;

   public HippogryphAIAirTarget(EntityHippogryph dragon) {
      this.hippogryph = dragon;
   }

   public boolean shouldExecute() {
      if (this.hippogryph != null) {
         if (!this.hippogryph.isFlying() && !this.hippogryph.isHovering()) {
            return false;
         } else if (this.hippogryph.isSitting()) {
            return false;
         } else if (this.hippogryph.isChild()) {
            return false;
         } else if (this.hippogryph.getOwner() != null && this.hippogryph.getPassengers().contains(this.hippogryph.getOwner())) {
            return false;
         } else {
            if (this.hippogryph.airTarget != null && this.hippogryph.getDistanceSquared(new Vec3d((double)this.hippogryph.airTarget.getX(), this.hippogryph.posY, (double)this.hippogryph.airTarget.getZ())) > 3.0F) {
               this.hippogryph.airTarget = null;
            }

            if (this.hippogryph.airTarget != null) {
               return false;
            } else {
               Vec3d vec = this.findAirTarget();
               if (vec == null) {
                  return false;
               } else {
                  this.hippogryph.airTarget = new BlockPos(vec.x, vec.y, vec.z);
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public boolean continueExecuting() {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this.hippogryph, StoneEntityProperties.class);
      if (!this.hippogryph.isFlying() && !this.hippogryph.isHovering()) {
         return false;
      } else if (this.hippogryph.isSitting()) {
         return false;
      } else if (this.hippogryph.isChild()) {
         return false;
      } else if (this.hippogryph.isChild()) {
         return false;
      } else if (properties != null && properties.isStone) {
         return false;
      } else {
         return this.hippogryph.airTarget != null;
      }
   }

   public Vec3d findAirTarget() {
      return new Vec3d(this.getNearbyAirTarget());
   }

   public BlockPos getNearbyAirTarget() {
      Random random = this.hippogryph.getRNG();
      if (this.hippogryph.getAttackTarget() == null) {
         for(int i = 0; i < 10; ++i) {
            BlockPos pos = DragonUtils.getBlockInViewHippogryph(this.hippogryph);
            if (pos != null && this.hippogryph.world.getBlockState(pos).getMaterial() == Material.AIR) {
               return pos;
            }
         }
      } else {
         BlockPos pos = new BlockPos((int)this.hippogryph.getAttackTarget().posX, (int)this.hippogryph.getAttackTarget().posY, (int)this.hippogryph.getAttackTarget().posZ);
         if (this.hippogryph.world.getBlockState(pos).getMaterial() == Material.AIR) {
            return pos;
         }
      }

      return this.hippogryph.getPosition();
   }
}
