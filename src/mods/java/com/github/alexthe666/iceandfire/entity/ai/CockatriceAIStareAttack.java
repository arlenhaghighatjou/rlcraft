package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CockatriceAIStareAttack extends EntityAIBase {
   private final EntityCockatrice entity;
   private final double moveSpeedAmp;
   private final float maxAttackDistance;
   private int attackCooldown;
   private int attackTime = -1;
   private int seeTime;
   private boolean strafingClockwise;
   private boolean strafingBackwards;
   private int strafingTime = -1;
   private BlockPos target = null;
   private int walkingTime = -1;
   private float prevYaw;

   public CockatriceAIStareAttack(EntityCockatrice cockatrice, double speedAmplifier, int delay, float maxDistance) {
      this.entity = cockatrice;
      this.moveSpeedAmp = speedAmplifier;
      this.attackCooldown = delay;
      this.maxAttackDistance = maxDistance * maxDistance;
      this.setMutexBits(3);
   }

   public void setAttackCooldown(int cooldown) {
      this.attackCooldown = cooldown;
   }

   public boolean shouldExecute() {
      return this.entity.getAttackTarget() != null;
   }

   public boolean shouldContinueExecuting() {
      return this.shouldExecute();
   }

   public void resetTask() {
      super.resetTask();
      this.seeTime = 0;
      this.attackTime = -1;
      this.entity.resetActiveHand();
      this.entity.getNavigator().clearPath();
      this.target = null;
   }

   public void updateTask() {
      EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
      if (entitylivingbase != null) {
         if (EntityGorgon.isStoneMob(entitylivingbase) || entitylivingbase.isDead) {
            this.entity.setAttackTarget((EntityLivingBase)null);
            this.entity.setTargetedEntity(0);
            this.resetTask();
            return;
         }

         if (!isEntityLookingAt(entitylivingbase, this.entity, 0.6000000238418579D) || entitylivingbase.prevPosX != this.entity.posX || entitylivingbase.prevPosY != this.entity.posY || entitylivingbase.prevPosZ != this.entity.posZ) {
            this.entity.getNavigator().clearPath();
            this.prevYaw = entitylivingbase.rotationYaw;
            BlockPos pos = DragonUtils.getBlockInTargetsViewCockatrice(this.entity, entitylivingbase);
            if (this.target == null || pos.distanceSq(this.target) > 4.0D) {
               this.target = pos;
            }
         }

         this.entity.setTargetedEntity(entitylivingbase.getEntityId());
         double d0 = this.entity.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
         boolean flag = this.entity.getEntitySenses().canSee(entitylivingbase);
         boolean flag1 = this.seeTime > 0;
         if (flag != flag1) {
            this.seeTime = 0;
         }

         if (flag) {
            ++this.seeTime;
         } else {
            --this.seeTime;
         }

         if (this.target != null && this.entity.getDistance((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ()) > 4.0D && !isEntityLookingAt(entitylivingbase, this.entity, 0.6000000238418579D)) {
            this.entity.getNavigator().tryMoveToXYZ((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ(), this.moveSpeedAmp);
         }

         this.entity.getLookHelper().setLookPosition(entitylivingbase.posX, entitylivingbase.posY + (double)entitylivingbase.getEyeHeight(), entitylivingbase.posZ, (float)this.entity.getHorizontalFaceSpeed(), (float)this.entity.getVerticalFaceSpeed());
      }

   }

   public static boolean isEntityLookingAt(EntityLivingBase looker, EntityLivingBase seen, double degree) {
      Vec3d vec3d = looker.getLook(1.0F).normalize();
      Vec3d vec3d1 = new Vec3d(seen.posX - looker.posX, seen.getEntityBoundingBox().minY + (double)seen.getEyeHeight() - (looker.posY + (double)looker.getEyeHeight()), seen.posZ - looker.posZ);
      double d0 = vec3d1.lengthVector();
      vec3d1 = vec3d1.normalize();
      double d1 = vec3d.dotProduct(vec3d1);
      return d1 > 1.0D - degree / d0;
   }
}
