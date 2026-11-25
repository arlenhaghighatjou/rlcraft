package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal;
import com.lycanitesmobs.core.entity.goals.targeting.DefendVillageGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityAegis extends TameableCreatureEntity implements IFusable {
   protected IFusable fusionTarget;

   public EntityAegis(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true));
      this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendVillageGoal(this));
      this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, EntityVillager.class));
      this.targetTasks.addTask(this.nextSpecialTargetIndex++, new DefendEntitiesGoal(this, EntityIronGolem.class));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && !this.hasAttackTarget() && this.currentBlockingTime < 2) {
         this.setBlocking();
      }

   }

   public boolean canBeTargetedBy(EntityLivingBase entity) {
      return !(entity instanceof EntityIronGolem) && !(entity instanceof EntityVillager) ? super.canBeTargetedBy(entity) : false;
   }

   public boolean shouldCreatureGroupHunt(EntityLivingBase target) {
      return target instanceof TameableCreatureEntity && ((TameableCreatureEntity)target).isTamed() ? false : super.shouldCreatureGroupHunt(target);
   }

   public boolean isFlying() {
      return true;
   }

   public boolean canAttackWhileBlocking() {
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
      return !type.equals("cactus") && !type.equals("inWall") ? super.isDamageTypeApplicable(type, source, damage) : false;
   }

   public void onDamage(DamageSource damageSrc, float damage) {
      if (this.getRNG().nextDouble() > 0.5D && this.getHealth() / this.getMaxHealth() > 0.25F) {
         this.setBlocking();
      }

      if (damageSrc.getTrueSource() != null && damageSrc.getTrueSource() instanceof EntityMob) {
         damage *= 0.5F;
      }

      super.onDamage(damageSrc, damage);
   }

   public void setBlocking() {
      this.currentBlockingTime = this.blockingTime + this.getRNG().nextInt(this.blockingTime / 2);
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return !this.isBlocking() ? 2.0F : super.getDamageModifier(damageSrc);
   }

   public IFusable getFusionTarget() {
      return this.fusionTarget;
   }

   public void setFusionTarget(IFusable fusionTarget) {
      this.fusionTarget = fusionTarget;
   }

   public Class getFusionClass(IFusable fusable) {
      if (fusable instanceof EntityCinder) {
         return CreatureManager.getInstance().getEntityClass("wisp");
      } else if (fusable instanceof EntityJengu) {
         return CreatureManager.getInstance().getEntityClass("nymph");
      } else if (fusable instanceof EntityGeonach) {
         return CreatureManager.getInstance().getEntityClass("vapula");
      } else if (fusable instanceof EntityDjinn) {
         return CreatureManager.getInstance().getEntityClass("sylph");
      } else {
         return fusable instanceof EntityArgus ? CreatureManager.getInstance().getEntityClass("spectre") : null;
      }
   }
}
