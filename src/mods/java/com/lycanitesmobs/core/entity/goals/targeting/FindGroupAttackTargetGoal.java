package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureRelationshipEntry;
import com.lycanitesmobs.core.info.CreatureGroup;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;

public class FindGroupAttackTargetGoal extends FindAttackTargetGoal {
   public FindGroupAttackTargetGoal(BaseCreatureEntity setHost) {
      super(setHost);

      label35:
      for(CreatureGroup group : setHost.creatureInfo.groups) {
         for(CreatureGroup targetGroup : group.huntGroups) {
            if (targetGroup.humanoids) {
               this.targetPlayers = true;
               break;
            }
         }

         Iterator var6 = group.packGroups.iterator();

         while(true) {
            if (!var6.hasNext()) {
               continue label35;
            }

            CreatureGroup targetGroup = (CreatureGroup)var6.next();
            if (targetGroup.humanoids) {
               break;
            }
         }

         this.targetPlayers = true;
      }

   }

   public FindGroupAttackTargetGoal setCheckSight(boolean bool) {
      this.checkSight = bool;
      return this;
   }

   public FindGroupAttackTargetGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public FindGroupAttackTargetGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   public FindGroupAttackTargetGoal setRange(double range) {
      this.targetingRange = range;
      return this;
   }

   public FindGroupAttackTargetGoal setHelpCall(boolean setHelp) {
      this.callForHelp = setHelp;
      return this;
   }

   public FindGroupAttackTargetGoal setTameTargetting(boolean setTargetting) {
      this.tameTargeting = setTargetting;
      return this;
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      if (!this.tameTargeting && this.host.isTamed()) {
         return false;
      } else if (!this.host.shouldCreatureGroupHunt(target)) {
         return false;
      } else if (!this.host.canAttackClass(target.getClass())) {
         return false;
      } else if (!this.host.canAttackEntity(target)) {
         return false;
      } else {
         CreatureRelationshipEntry relationshipEntry = this.host.relationships.getEntry(target);
         if (relationshipEntry != null && !relationshipEntry.canHunt()) {
            return false;
         } else {
            return this.host.rollAttackTargetChance(target);
         }
      }
   }

   public EntityLivingBase getNewTarget(double rangeX, double rangeY, double rangeZ) {
      return super.getNewTarget(rangeX, rangeY, rangeZ);
   }
}
