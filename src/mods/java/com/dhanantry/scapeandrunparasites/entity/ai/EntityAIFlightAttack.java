package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class EntityAIFlightAttack extends EntityAIBase {
   private final EntityParasiteBase parent;
   private int delay;
   private double distance;
   private boolean xRay;
   private int fromF;

   public EntityAIFlightAttack(EntityParasiteBase in, double distance) {
      this.parent = in;
      this.delay = 0;
      this.distance = distance * distance;
   }

   public EntityAIFlightAttack(EntityParasiteBase in, double distance, boolean ignoreSee, int floorS) {
      this(in, distance);
      this.xRay = ignoreSee;
      this.fromF = floorS;
   }

   public boolean shouldExecute() {
      return this.parent.getWait() == 0 && this.parent.ticksExisted % 20 == 0;
   }

   public void updateTask() {
      if (this.parent.getAttackTarget() != null) {
         if (this.parent.getAttackTarget() instanceof EntityParasiteBase) {
            this.parent.setAttackTarget((EntityLivingBase)null);
            this.delay = 0;
            this.parent.setParasiteStatus(0);
            if (this.parent.getParasiteType() != 61) {
               this.parent.getMoveHelper().setMoveTo(this.parent.posX, this.parent.posY, this.parent.posZ, 1.0D);
            }

            return;
         }

         if (this.parent.world.getEntityByID(this.parent.getAttackTarget().getEntityId()) == null) {
            this.parent.setAttackTarget((EntityLivingBase)null);
            this.delay = 0;
            this.parent.setParasiteStatus(0);
            if (this.parent.getParasiteType() != 61) {
               this.parent.getMoveHelper().setMoveTo(this.parent.posX, this.parent.posY, this.parent.posZ, 1.0D);
            }

            return;
         }

         EntityLivingBase target = this.parent.getAttackTarget();
         if (target instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)target)) {
            this.parent.setAttackTarget((EntityLivingBase)null);
            this.delay = 0;
            this.parent.setParasiteStatus(0);
         }

         if (!this.parent.canEntityBeSeen(target)) {
            if (this.xRay) {
               if (this.parent.world.rand.nextInt(5) == 0) {
                  ++this.delay;
               }
            } else {
               ++this.delay;
            }
         } else if (target.getDistanceSq(this.parent) >= this.distance) {
            ++this.delay;
         } else {
            this.delay = 0;
         }

         if (this.delay >= 6) {
            this.parent.setAttackTarget((EntityLivingBase)null);
            this.parent.setParasiteStatus(0);
            this.delay = 0;
            if (this.parent.getParasiteType() != 61) {
               this.parent.getMoveHelper().setMoveTo(this.parent.posX, this.parent.posY, this.parent.posZ, 1.0D);
            }
         }
      } else {
         this.attackSurr();
      }

   }

   protected void attackSurr() {
      BlockPos posi = this.parent.getPosition();
      if (this.fromF != 0 && this.parent.world.rand.nextInt(this.fromF) == 0) {
         posi = ParasiteEventEntity.getFloor(this.parent.world, posi, 25);
         if (posi == null) {
            posi = this.parent.getPosition();
         }
      }

      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(posi)).grow(this.parent.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue());
      List<EntityLivingBase> moblist = this.parent.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
      Iterator var4 = moblist.iterator();

      EntityLivingBase mob;
      while(true) {
         if (!var4.hasNext()) {
            return;
         }

         mob = (EntityLivingBase)var4.next();
         if (mob != null) {
            if (mob instanceof EntityPlayer) {
               if (this.canAttackPlayer((EntityPlayer)mob)) {
                  break;
               }
            } else if (mob instanceof EntityLiving && !ParasiteEventEntity.checkEntity((EntityLiving)mob, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite) && (this.fromF == 0 || mob.getDistanceSq(this.parent) <= this.distance) && mob != this.parent && this.canTargetEntity(mob) && (this.parent.canEntityBeSeen(mob) || this.xRay) && mob.getDistanceSq(this.parent) < 1024.0D && mob.isEntityAlive() && this.parent.canAttackClass(mob.getClass())) {
               this.parent.setAttackTarget(mob);
               this.parent.setParasiteStatus(1);
               return;
            }
         }
      }

      this.parent.setAttackTarget(mob);
   }

   private boolean canTargetEntity(EntityLivingBase in) {
      return !(in instanceof EntityParasiteBase) && !(in instanceof EntityAnimal) && !(in instanceof EntityCreeper) && !(in instanceof EntityWaterMob);
   }

   private boolean canAttackPlayer(EntityPlayer in) {
      return in.capabilities.disableDamage ? false : EntityAITarget.isSuitableTarget(this.parent, in, false, true);
   }
}
