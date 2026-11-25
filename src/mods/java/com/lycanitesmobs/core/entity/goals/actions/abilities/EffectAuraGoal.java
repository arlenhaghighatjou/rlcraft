package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.google.common.base.Predicate;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.BaseGoal;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class EffectAuraGoal extends BaseGoal {
   protected Potion effect;
   protected float auraRange = 10.0F;
   protected int effectSeconds = 5;
   protected int effectAmplifier = 0;
   protected boolean checkSight = true;
   protected float damageAmount = 0.0F;
   protected int duration = 200;
   protected int cooldownDuration = 0;
   protected int tickRate = 40;
   protected byte targetTypes;
   protected String targetCreatureType;
   public int abilityTime;
   public int cooldownTime;

   public EffectAuraGoal(BaseCreatureEntity setHost) {
      super(setHost);
      this.targetTypes = BaseCreatureEntity.TARGET_TYPES.ENEMY.id;
      this.targetCreatureType = null;
      this.abilityTime = 0;
      this.cooldownTime = this.cooldownDuration;
   }

   public EffectAuraGoal setDuration(int duration) {
      this.duration = duration;
      return this;
   }

   public EffectAuraGoal setCooldown(int cooldown) {
      this.cooldownDuration = cooldown;
      this.cooldownTime = cooldown;
      return this;
   }

   public EffectAuraGoal setTickRate(int tickRate) {
      this.tickRate = tickRate;
      return this;
   }

   public EffectAuraGoal setEffect(Potion effect) {
      this.effect = effect;
      return this;
   }

   public EffectAuraGoal setEffect(String effectName) {
      this.effect = ObjectManager.getEffect(effectName);
      return this;
   }

   public EffectAuraGoal setRange(float auraRange) {
      this.auraRange = auraRange;
      return this;
   }

   public EffectAuraGoal setEffectSeconds(int baseEffectSeconds) {
      this.effectSeconds = baseEffectSeconds;
      return this;
   }

   public EffectAuraGoal setAmplifier(int effectAmplifier) {
      this.effectAmplifier = effectAmplifier;
      return this;
   }

   public EffectAuraGoal setCheckSight(boolean checkSight) {
      this.checkSight = checkSight;
      return this;
   }

   public EffectAuraGoal setDamageAmount(float damageAmount) {
      this.damageAmount = damageAmount;
      return this;
   }

   public EffectAuraGoal setTargetTypes(byte targetTypes) {
      this.targetTypes = targetTypes;
      return this;
   }

   public EffectAuraGoal setTargetCreatureType(String creatureTypeName) {
      this.targetCreatureType = creatureTypeName;
      return this;
   }

   public void startExecuting() {
      this.cooldownTime = this.cooldownDuration;
      this.abilityTime = 0;
   }

   public void updateTask() {
      if (this.cooldownTime-- > 0) {
         this.abilityTime = 0;
      } else if (this.abilityTime++ >= this.duration && this.cooldownDuration > 0) {
         this.cooldownTime = this.cooldownDuration;
      } else if (this.abilityTime % this.tickRate == 0) {
         PotionEffect effectInstance = null;
         if (this.effect != null) {
            effectInstance = new PotionEffect(this.effect, this.host.getEffectDuration(this.effectSeconds), this.effectAmplifier);
         }

         List<EntityLivingBase> aoeTargets = this.host.<EntityLivingBase>getNearbyEntities(EntityLivingBase.class, (entity) -> {
            if (!(entity instanceof EntityLivingBase)) {
               return false;
            } else {
               boolean validTarget = false;
               if (entity == this.host) {
                  if ((this.targetTypes & BaseCreatureEntity.TARGET_TYPES.SELF.id) > 0) {
                     validTarget = true;
                  }
               } else {
                  if ((this.targetTypes & BaseCreatureEntity.TARGET_TYPES.ALLY.id) > 0) {
                     if (this.host.isTamed() && this.host instanceof TameableCreatureEntity) {
                        if (entity instanceof TameableCreatureEntity && ((TameableCreatureEntity)entity).getPlayerOwner() == ((TameableCreatureEntity)this.host).getPlayerOwner()) {
                           validTarget = true;
                        }
                     } else if (entity instanceof BaseCreatureEntity && !((BaseCreatureEntity)entity).isTamed()) {
                        validTarget = true;
                     }
                  }

                  if ((this.targetTypes & BaseCreatureEntity.TARGET_TYPES.ENEMY.id) > 0 && (this.host.canAttackClass(entity.getClass()) || this.host.canAttackEntity((EntityLivingBase)entity))) {
                     validTarget = true;
                  }
               }

               if (!validTarget) {
                  return false;
               } else if (this.targetCreatureType != null) {
                  return entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).creatureInfo.creatureType != null ? this.targetCreatureType.equals(((BaseCreatureEntity)entity).creatureInfo.creatureType.getName()) : false;
               } else {
                  return !this.checkSight || this.host.getEntitySenses().canSee(entity);
               }
            }
         }, (double)this.auraRange);
         Iterator var3 = aoeTargets.iterator();

         while(true) {
            EntityLivingBase target;
            while(true) {
               if (!var3.hasNext()) {
                  return;
               }

               target = (EntityLivingBase)var3.next();
               if (effectInstance == null) {
                  break;
               }

               if (target.isPotionApplicable(effectInstance)) {
                  target.addPotionEffect(effectInstance);
                  break;
               }
            }

            if (this.damageAmount != 0.0F) {
               DamageSource damageSource = new EntityDamageSource("mob", this.host);
               damageSource.setDamageIsAbsolute();
               damageSource.setDamageBypassesArmor();
               target.attackEntityFrom(damageSource, this.damageAmount);
               if (this.host.minions.contains(target)) {
                  this.host.onTryToDamageMinion(target, this.damageAmount);
               }
            }
         }
      }
   }
}
