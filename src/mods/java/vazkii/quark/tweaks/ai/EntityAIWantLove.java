package vazkii.quark.tweaks.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.tweaks.feature.PatTheDogs;

public class EntityAIWantLove extends EntityAIBase {
   private static final String PET_TIME = "quark:PetTime";
   private final EntityTameable creature;
   private EntityLivingBase leapTarget;
   public final float leapUpMotion;

   public static void setPetTime(EntityTameable entity) {
      entity.getEntityData().setLong("quark:PetTime", entity.world.getTotalWorldTime());
   }

   public static boolean needsPets(EntityTameable entity) {
      if (PatTheDogs.dogsWantLove > 0 && entity.isTamed()) {
         long lastPetAt = entity.getEntityData().getLong("quark:PetTime");
         long sinceLastPet = entity.world.getTotalWorldTime() - lastPetAt;
         return sinceLastPet > (long)PatTheDogs.dogsWantLove;
      } else {
         return false;
      }
   }

   public EntityAIWantLove(EntityTameable creature, float leapMotion) {
      this.creature = creature;
      this.leapUpMotion = leapMotion;
      this.setMutexBits(5);
   }

   public boolean shouldExecute() {
      if (!needsPets(this.creature)) {
         return false;
      } else {
         this.leapTarget = this.creature.getOwner();
         if (this.leapTarget == null) {
            return false;
         } else {
            double distanceToTarget = this.creature.getDistanceSq(this.leapTarget);
            return 4.0D <= distanceToTarget && distanceToTarget <= 16.0D && this.creature.onGround && this.creature.getRNG().nextInt(5) == 0;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (!needsPets(this.creature)) {
         return false;
      } else {
         return !this.creature.onGround;
      }
   }

   public void startExecuting() {
      double dX = this.leapTarget.posX - this.creature.posX;
      double dZ = this.leapTarget.posZ - this.creature.posZ;
      float leapMagnitude = MathHelper.sqrt(dX * dX + dZ * dZ);
      if ((double)leapMagnitude >= 1.0E-4D) {
         this.creature.motionX += dX / (double)leapMagnitude * 0.4D + this.creature.motionX * 0.2D;
         this.creature.motionZ += dZ / (double)leapMagnitude * 0.4D + this.creature.motionZ * 0.2D;
      }

      this.creature.motionY = (double)this.leapUpMotion;
   }
}
