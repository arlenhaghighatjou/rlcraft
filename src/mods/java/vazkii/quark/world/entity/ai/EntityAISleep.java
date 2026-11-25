package vazkii.quark.world.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import vazkii.quark.world.entity.EntityFoxhound;

public class EntityAISleep extends EntityAIBase {
   private final EntityFoxhound foxhound;
   private boolean isSleeping;

   public EntityAISleep(EntityFoxhound foxhound) {
      this.foxhound = foxhound;
      this.setMutexBits(2);
   }

   public boolean shouldExecute() {
      if (this.foxhound.isTamed() && !this.foxhound.isInWater() && this.foxhound.onGround) {
         EntityLivingBase entitylivingbase = this.foxhound.getOwner();
         if (entitylivingbase == null) {
            return true;
         } else {
            return (this.foxhound.getDistanceSq(entitylivingbase) >= 144.0D || entitylivingbase.getRevengeTarget() == null) && this.isSleeping;
         }
      } else {
         return false;
      }
   }

   public void startExecuting() {
      this.foxhound.getNavigator().clearPath();
      this.foxhound.getAISit().setSitting(true);
      this.foxhound.setSitting(true);
      this.foxhound.setSleeping(true);
   }

   public void resetTask() {
      this.foxhound.getAISit().setSitting(false);
      this.foxhound.setSitting(false);
      this.foxhound.setSleeping(false);
   }

   public void setSleeping(boolean sitting) {
      this.isSleeping = sitting;
   }
}
