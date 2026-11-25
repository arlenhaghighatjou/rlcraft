package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.PlayerControlGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityPinky extends RideableCreatureEntity {
   PlayerControlGoal playerControlAI;

   public EntityPinky(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = true;
      this.spreadFire = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityPigZombie.class).setSpeed(1.5D).setDamageScale(8.0D).setRange(2.5D));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityEnderman.class).setSpeed(1.5D).setDamageScale(8.0D).setRange(2.5D));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setSpeed(1.5D));
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindAttackTargetGoal(this)).addTargets(EntityPigZombie.class));
      this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindAttackTargetGoal(this)).addTargets(EntityEnderman.class));
   }

   public boolean shouldCreatureGroupFlee(EntityLivingBase target) {
      return false;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getEntityWorld().getWorldType().getId() != -1) {
         this.setFarmed();
      }

   }

   public void riderEffects(EntityLivingBase rider) {
      if (rider.isPotionActive(MobEffects.WITHER)) {
         rider.removePotionEffect(MobEffects.WITHER);
      }

      if (rider.isBurning()) {
         rider.setFire(0);
      }

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
      return 20.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 100;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if ((target instanceof EntityAnimal || target instanceof BaseCreatureEntity && ((BaseCreatureEntity)target).creatureInfo.isFarmable()) && target.height >= 1.0F) {
            this.breed();
         }

         return true;
      }
   }

   public void specialAttack() {
      double distance = 5.0D;
      List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), (possibleTargetx) -> {
         return possibleTarget.isEntityAlive() && possibleTarget != this && !this.isRidingOrBeingRiddenBy(possibleTarget) && !this.isOnSameTeam(possibleTarget) && this.canAttackClass(possibleTarget.getClass()) && this.canAttackEntity(possibleTarget);
      });
      if (!possibleTargets.isEmpty()) {
         for(EntityLivingBase possibleTarget : possibleTargets) {
            boolean doDamage = true;
            if (this.getRider() instanceof EntityPlayer && MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)this.getRider(), possibleTarget))) {
               doDamage = false;
            }

            if (doDamage) {
               possibleTarget.addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 0));
            }
         }
      }

      this.playAttackSound();
      this.triggerAttackCooldown();
   }

   public boolean canBeTempted() {
      return this.isChild();
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

   public float getFallResistance() {
      return 10.0F;
   }

   public boolean isBreedingItem(ItemStack itemStack) {
      return false;
   }
}
