package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityAIAttackMeleeRanged extends EntityAIBase {
   private final EntityParasiteBase parentEntity;
   private double minDistance;
   private double maxDistance;
   private double prepDistance;
   private boolean pulling;
   private float str;

   public EntityAIAttackMeleeRanged(EntityParasiteBase in, double minD, double maxD, double prepD, boolean pull, float str) {
      this.parentEntity = in;
      this.minDistance = minD;
      this.maxDistance = maxD;
      this.prepDistance = prepD;
      this.pulling = pull;
      this.str = str;
   }

   public boolean shouldExecute() {
      return this.parentEntity.getAttackTarget() != null;
   }

   public void resetTask() {
      this.parentEntity.setParasiteStatus(0);
   }

   public void updateTask() {
      if (this.parentEntity.getAttackTarget() == null) {
         this.resetTask();
      } else {
         EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
         if (entitylivingbase.getDistanceSq(this.parentEntity) < this.prepDistance * this.prepDistance && this.parentEntity.canEntityBeSeen(entitylivingbase)) {
            this.parentEntity.setParasiteStatus(15);
            if (entitylivingbase.getDistanceSq(this.parentEntity) < this.maxDistance * this.maxDistance && entitylivingbase.getDistanceSq(this.parentEntity) > this.minDistance * this.minDistance && this.parentEntity.ticksExisted % 20 == 0) {
               Vec3d vec3d = this.parentEntity.getLook(1.0F);
               float f = (float)MathHelper.atan2(entitylivingbase.posZ - this.parentEntity.posZ, entitylivingbase.posX - this.parentEntity.posX);
               EntityDamage entityevokerfangs = new EntityDamage(this.parentEntity.world, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, f, this.parentEntity, 1.0F, this.pulling, 1.0F);
               this.parentEntity.world.spawnEntity(entityevokerfangs);
            }
         } else {
            this.parentEntity.setParasiteStatus(0);
         }
      }

   }
}
