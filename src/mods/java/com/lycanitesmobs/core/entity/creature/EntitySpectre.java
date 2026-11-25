package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntitySpectre extends TameableCreatureEntity implements IMob, IGroupHeavy {
   protected int pullRange = 6;
   protected int pullEnergy = 0;
   protected int pullEnergyMax = 40;
   protected int pullEnergyRecharge = 0;
   protected int pullEnergyRechargeMax = 80;
   protected boolean pullRecharging = true;

   public EntitySpectre(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.spawnsInWater = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextPriorityGoalIndex++, (new StealthGoal(this)).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (this.pullRecharging && ++this.pullEnergyRecharge >= this.pullEnergyRechargeMax) {
            this.pullRecharging = false;
            this.pullEnergy = this.pullEnergyMax;
            this.pullEnergyRecharge = 0;
         }

         this.pullEnergy = Math.min(this.pullEnergy, this.pullEnergyMax);
         if (this.canPull()) {
            Iterator i = this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, (double)this.pullRange).iterator();

            label85:
            while(true) {
               EntityLivingBase entity;
               EntityPlayerMP player;
               while(true) {
                  if (!i.hasNext()) {
                     if (--this.pullEnergy <= 0) {
                        this.pullRecharging = true;
                        this.pullEnergyRecharge = 0;
                     }
                     break label85;
                  }

                  entity = (EntityLivingBase)i.next();
                  if (entity != this && entity != this.getControllingPassenger() && !(entity instanceof IGroupBoss) && !(entity instanceof IGroupHeavy) && !entity.isPotionActive(ObjectManager.getEffect("weight")) && this.canAttackEntity(entity)) {
                     player = null;
                     if (!(entity instanceof EntityPlayerMP)) {
                        break;
                     }

                     player = (EntityPlayerMP)entity;
                     if (!player.capabilities.isCreativeMode) {
                        break;
                     }
                  }
               }

               double xDist = this.posX - entity.posX;
               double zDist = this.posZ - entity.posZ;
               double xzDist = (double)MathHelper.sqrt(xDist * xDist + zDist * zDist);
               double factor = 0.1D;
               double motionCap = 10.0D;
               if (entity.motionX < motionCap && entity.motionX > -motionCap && entity.motionZ < motionCap && entity.motionZ > -motionCap) {
                  entity.addVelocity(xDist / xzDist * factor + entity.motionX * factor, 0.0D, zDist / xzDist * factor + entity.motionZ * factor);
               }

               if (player != null) {
                  player.connection.sendPacket(new SPacketEntityVelocity(entity));
               }
            }
         }
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public boolean extraAnimation01() {
      return this.getEntityWorld().isRemote ? super.extraAnimation01() : this.canPull();
   }

   public boolean canPull() {
      if (this.getEntityWorld().isRemote) {
         return this.extraAnimation01();
      } else {
         return !this.pullRecharging && this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) <= (float)(this.pullRange * 3);
      }
   }

   public boolean isFlying() {
      return true;
   }

   public boolean isStrongSwimmer() {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("inWall") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }
}
