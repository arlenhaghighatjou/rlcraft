package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISkill extends EntityAIBase {
   private final EntityParasiteBase parentEntity;
   private int sCooldown;
   private int attackTimer;
   private int attacking;
   private boolean checV;
   private int distanceC;
   private int distanceL;
   private byte attID;

   public EntityAISkill(EntityParasiteBase para, int cooldown, int miniDistance, boolean needVisual, int attackID) {
      this.attackTimer = 0;
      this.attacking = 0;
      this.parentEntity = para;
      this.sCooldown = cooldown;
      this.distanceC = miniDistance * miniDistance;
      this.checV = needVisual;
      this.attID = (byte)attackID;
      this.distanceL = 0;
   }

   public EntityAISkill(EntityParasiteBase para, int cooldown, int miniDistance, int maxDistance, boolean needVisual, int attackID) {
      this(para, cooldown, miniDistance, needVisual, attackID);
      this.distanceL = maxDistance * maxDistance;
   }

   public boolean shouldExecute() {
      return this.parentEntity.getParasiteStatus() > 0 && this.parentEntity.getParasiteStatus() < 3 || this.attacking >= 1;
   }

   public void startExecuting() {
   }

   public void resetTask() {
   }

   public void updateTask() {
      if (this.attacking >= 1) {
         ++this.attacking;
         this.parentEntity.doSpecialSkill(this.attID);
         if (this.parentEntity.getFinished(this.attID)) {
            this.attacking = 0;
            this.attackTimer = 0;
            this.parentEntity.setFinished(this.attID, false);
         }
      } else if (this.parentEntity.getAttackTarget() != null) {
         EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
         boolean flag = false;
         double dis = this.parentEntity.getDistanceSq(entitylivingbase);
         if (dis < (double)this.distanceC && dis >= (double)this.distanceL) {
            if (this.checV) {
               if (this.parentEntity.canEntityBeSeen(entitylivingbase)) {
                  ++this.attackTimer;
               }
            } else {
               ++this.attackTimer;
            }
         }

         if (this.parentEntity.isPotionActive(SRPPotions.RAGE_E)) {
            ++this.attackTimer;
         }

         if (this.attackTimer >= this.sCooldown) {
            ++this.attacking;
         }
      }

   }
}
