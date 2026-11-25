package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.damagesources.ElementDamageSource;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ElementManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityZephyr extends TameableCreatureEntity implements IMob {
   protected short aoeAttackTick = 0;

   public EntityZephyr(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.hasAttackTarget() && ++this.aoeAttackTick == (this.isPetType("familiar") ? 100 : 40)) {
         this.aoeAttackTick = 0;

         for(Object entityObj : this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, 4.0D)) {
            EntityLivingBase target = (EntityLivingBase)entityObj;
            if (target != this && this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
               target.attackEntityFrom(ElementDamageSource.causeElementDamage(this, ElementManager.getInstance().getElement("lightning")), this.getAttackDamage(1.0D));
            }
         }
      }

      if (this.getEntityWorld().isRemote && this.hasAttackTarget()) {
         for(Object entityObj : this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, 4.0D)) {
            EntityLivingBase target = (EntityLivingBase)entityObj;
            if (this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
               this.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT, target.posX + (this.rand.nextDouble() - 0.5D) * (double)target.width, target.posY + this.rand.nextDouble() * (double)target.height, target.posZ + (this.rand.nextDouble() - 0.5D) * (double)target.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }
         }
      }

   }

   public boolean isFlying() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return "lightningBolt".equalsIgnoreCase(type) ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }
}
