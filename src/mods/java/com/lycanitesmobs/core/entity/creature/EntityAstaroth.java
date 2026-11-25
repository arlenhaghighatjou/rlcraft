package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.projectile.EntityHellShield;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAstaroth extends TameableCreatureEntity implements IMob {
   public EntityAstaroth(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.solidCollision = false;
      this.setupMob();
      this.hitAreaWidthScale = 1.5F;
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false).setRange(1.0D).setMaxChaseDistance(8.0F));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(1.0D).setRange(40.0F).setMinChaseDistance(16.0F).setChaseTime(-1));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.updateTick % 20L == 0L && this.getMasterTarget() != null && this.getMasterTarget() instanceof EntityAsmodeus && ((BaseCreatureEntity)this.getMasterTarget()).getBattlePhase() > 0) {
         EntityHellShield projectile = new EntityHellShield(this.getEntityWorld(), this);
         projectile.setProjectileScale(3.0F);
         projectile.posY -= (double)this.height * 0.35D;
         double dX = this.getMasterTarget().posX - this.posX;
         double dY = this.getMasterTarget().posY + (double)this.getMasterTarget().height * 0.75D - projectile.posY;
         double dZ = this.getMasterTarget().posZ - this.posZ;
         double distance = (double)(MathHelper.sqrt(dX * dX + dZ * dZ) * 0.1F);
         float velocity = 0.8F;
         projectile.shoot(dX, dY + distance, dZ, velocity, 0.0F);
         this.getEntityWorld().spawnEntity(projectile);
      }

   }

   public boolean canAttackEntity(EntityLivingBase target) {
      return !(target instanceof EntityTrite) && !(target instanceof EntityCacodemon) && !(target instanceof EntityAsmodeus) ? super.canAttackEntity(target) : false;
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("devilstar", target, range, 0.0F, new Vec3d(0.0D, -2.799999952316284D, 0.0D), 1.2F, 1.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public void onDeath(DamageSource damageSource) {
      if (!this.getEntityWorld().isRemote && CreatureManager.getInstance().getCreature("trite").enabled) {
         int j = 2 + this.rand.nextInt(5) + this.getEntityWorld().getDifficulty().getDifficultyId() - 1;
         if (this.isTamed()) {
            j = 3;
         }

         for(int k = 0; k < j; ++k) {
            EntityTrite trite = (EntityTrite)CreatureManager.getInstance().getCreature("trite").createEntity(this.getEntityWorld());
            this.summonMinion(trite, (double)(this.rand.nextFloat() * 360.0F), 0.5D);
            if (this.isTamed()) {
               trite.setTemporary(100);
            }
         }
      }

      super.onDeath(damageSource);
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }
}
