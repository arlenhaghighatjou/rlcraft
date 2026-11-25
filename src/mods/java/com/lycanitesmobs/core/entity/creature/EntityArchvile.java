package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.ChaseGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.EffectAuraGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.SummonMinionsGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityArchvile extends TameableCreatureEntity implements IMob {
   public EntityArchvile(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = false;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextIdleGoalIndex, new FaceTargetGoal(this));
      this.tasks.addTask(this.nextCombatGoalIndex, (new ChaseGoal(this)).setMinDistance(16.0F).setMaxDistance(64.0F).setSpeed(1.0D));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(1.0D).setRange(32.0F).setMinChaseDistance(16.0F).setChaseTime(-1));
      this.tasks.addTask(this.nextCombatGoalIndex, (new EffectAuraGoal(this)).setEffect(MobEffects.STRENGTH).setAmplifier(2).setEffectSeconds(2).setRange(32.0F).setCheckSight(false).setTargetTypes(BaseCreatureEntity.TARGET_TYPES.ALLY.id).setTargetCreatureType("demon"));
      this.tasks.addTask(this.nextCombatGoalIndex, (new EffectAuraGoal(this)).setEffect(MobEffects.SPEED).setAmplifier(2).setEffectSeconds(2).setRange(32.0F).setCheckSight(false).setTargetTypes(BaseCreatureEntity.TARGET_TYPES.ALLY.id).setTargetCreatureType("demon"));
      this.tasks.addTask(this.nextCombatGoalIndex, (new EffectAuraGoal(this)).setEffect(MobEffects.RESISTANCE).setAmplifier(2).setEffectSeconds(2).setRange(32.0F).setCheckSight(false).setTargetTypes(BaseCreatureEntity.TARGET_TYPES.ALLY.id).setTargetCreatureType("demon"));
      this.tasks.addTask(this.nextCombatGoalIndex, (new SummonMinionsGoal(this)).setMinionInfo("belph").setSummonCap(2).setConditions((new GoalConditions()).setRareVariantOnly(true)));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public void attackRanged(Entity target, float range) {
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("doomfireball");
      BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), this);
      if (projectile != null) {
         projectile.setProjectileScale(2.0F);
         projectile.shoot(0.0D, -1.0D, 0.0D, 0.5F, 4.0F);
         projectile.setPosition((double)target.getPosition().getX(), (double)(target.getPosition().getY() + 4) + this.rand.nextDouble() * 3.0D, (double)target.getPosition().getZ());
         this.getEntityWorld().spawnEntity(projectile);
      }

      super.attackRanged(target, range);
   }

   public boolean canBurn() {
      return false;
   }

   public boolean petControlsEnabled() {
      return true;
   }
}
