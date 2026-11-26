package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityShade extends RideableCreatureEntity {
   public EntityShade(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.hasJumpSound = true;
      this.canGrow = false;
      this.setupMob();
      this.stepHeight = 1.0F;
      this.attackCooldownMax = 40;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setSpeed(1.5D));
   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote) {
         if (!this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
               this.specialAttack();
               this.applyStaminaCost();
            }
         }
      }
   }

   public float getStaminaCost() {
      return 100.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 100;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.85D;
   }

   public double getMountedZOffset() {
      return (double)this.width * 0.25D;
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         float leeching = (float)this.getEffectStrength(this.getAttackDamage(damageScale) / 4.0F);
         this.heal(leeching);
         if (this.getRNG().nextFloat() <= 0.1F) {
            this.specialAttack();
         }

         return true;
      }
   }

   public void specialAttack() {
      double distance = 5.0D;
      List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), (possibleTarget) -> {
         return possibleTarget.isEntityAlive() && possibleTarget != this && !this.isRidingOrBeingRiddenBy(possibleTarget) && !this.isOnSameTeam(possibleTarget) && this.canAttackClass(possibleTarget.getClass()) && this.canAttackEntity(possibleTarget);
      });
      if (!possibleTargets.isEmpty()) {
         for(EntityLivingBase possibleTarget : possibleTargets) {
            boolean doDamage = true;
            if (this.getRider() instanceof EntityPlayer && MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)this.getRider(), possibleTarget))) {
               doDamage = false;
            }

            if (doDamage) {
               if (ObjectManager.getEffect("fear") != null) {
                  possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getEffect("fear"), this.getEffectDuration(5), 1));
               } else {
                  possibleTarget.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 0));
               }
            }
         }
      }

      this.playAttackSound();
      this.triggerAttackCooldown();
   }

   public boolean canBeTempted() {
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

   public float getFallResistance() {
      return 10.0F;
   }
}
