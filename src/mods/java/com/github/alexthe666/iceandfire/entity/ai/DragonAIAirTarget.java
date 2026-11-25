package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DragonAIAirTarget extends EntityAIBase {
   private EntityDragonBase dragon;
   private World theWorld;

   public DragonAIAirTarget(EntityDragonBase dragon) {
      this.dragon = dragon;
      this.theWorld = dragon.world;
   }

   public boolean shouldExecute() {
      if (this.dragon == null) {
         return false;
      } else if ((this.dragon.isFlying() || this.dragon.isHovering()) && !this.dragon.onGround) {
         if (this.dragon.isSleeping()) {
            return false;
         } else if (this.dragon.isChild()) {
            return false;
         } else if (this.dragon.getOwner() != null && this.dragon.getPassengers().contains(this.dragon.getOwner())) {
            return false;
         } else {
            if (this.dragon.airTarget != null && this.dragon.isTargetBlocked(new Vec3d(this.dragon.airTarget))) {
               this.dragon.airTarget = null;
            }

            if (this.dragon.airTarget != null) {
               return false;
            } else {
               Vec3d vec = this.findAirTarget();
               if (vec == null) {
                  return false;
               } else {
                  this.dragon.airTarget = new BlockPos(vec.x, vec.y, vec.z);
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
      if (!this.dragon.isFlying() && !this.dragon.isHovering()) {
         return false;
      } else if (this.dragon.isSleeping()) {
         return false;
      } else if (this.dragon.isChild()) {
         return false;
      } else if (properties != null && properties.isStone) {
         return false;
      } else {
         return this.dragon.airTarget != null;
      }
   }

   public Vec3d findAirTarget() {
      return new Vec3d(this.getNearbyAirTarget());
   }

   public BlockPos getNearbyAirTarget() {
      if (this.dragon.getAttackTarget() == null) {
         BlockPos pos = DragonUtils.getBlockInView(this.dragon);
         return pos != null && this.dragon.world.getBlockState(pos).getMaterial() == Material.AIR ? pos : this.dragon.getPosition();
      } else {
         return new BlockPos((int)this.dragon.getAttackTarget().posX, (int)this.dragon.getAttackTarget().posY, (int)this.dragon.getAttackTarget().posZ);
      }
   }
}
