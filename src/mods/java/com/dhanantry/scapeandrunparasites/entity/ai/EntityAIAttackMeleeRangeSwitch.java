package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIAttackMeleeRangeSwitch extends EntityAIBase {
   private EntityParasiteBase parent;
   private float distance;

   public EntityAIAttackMeleeRangeSwitch(EntityParasiteBase in, float meleeDistance) {
      this.parent = in;
      this.distance = meleeDistance;
   }

   public boolean shouldExecute() {
      return this.parent.getAttackTarget() != null;
   }

   public void updateTask() {
      if (this.parent.getAttackTarget() != null) {
         EntityLivingBase entitylivingbase = this.parent.getAttackTarget();
         if (entitylivingbase.getDistance(this.parent) < this.distance && this.parent.canEntityBeSeen(entitylivingbase)) {
            this.parent.setWorkTask(true);
         } else {
            this.parent.setWorkTask(false);
         }
      }

   }
}
