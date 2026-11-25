package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityWraamon extends TameableCreatureEntity implements IMob {
   public EntityWraamon(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextPriorityGoalIndex++, (new StealthGoal(this)).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0) {
         this.leap(6.0F, 0.6D, this.getAttackTarget());
      }

   }

   public boolean attackMelee(Entity target, double damageScale) {
      double targetKnockbackResistance = 0.0D;
      if (target instanceof EntityLivingBase) {
         targetKnockbackResistance = ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
         ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      }

      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if (target instanceof EntityLivingBase) {
            ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
         }

         return true;
      }
   }

   public boolean canClimb() {
      return false;
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

   public float getFallResistance() {
      return 10.0F;
   }
}
