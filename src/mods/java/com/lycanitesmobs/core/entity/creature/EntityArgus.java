package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityArgus extends TameableCreatureEntity implements IMob, IFusable {
   private int teleportTime = 60;
   protected IFusable fusionTarget;

   public EntityArgus(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.spawnsInWater = true;
      this.setupMob();
      this.stepHeight = 1.0F;
      this.setAttackCooldownMax(40);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextPriorityGoalIndex++, (new StealthGoal(this)).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true).setMaxChaseDistanceSq(5.0F));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(16.0F).setMinChaseDistance(14.0F));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("chaosorb", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 0.6F, 1.0F, 1.0F);
      super.attackRanged(target, range);
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

   public IFusable getFusionTarget() {
      return this.fusionTarget;
   }

   public void setFusionTarget(IFusable fusionTarget) {
      this.fusionTarget = fusionTarget;
   }

   public Class getFusionClass(IFusable fusable) {
      if (fusable instanceof EntityCinder) {
         return CreatureManager.getInstance().getEntityClass("grue");
      } else if (fusable instanceof EntityJengu) {
         return CreatureManager.getInstance().getEntityClass("eechetik");
      } else if (fusable instanceof EntityGeonach) {
         return CreatureManager.getInstance().getEntityClass("tremor");
      } else if (fusable instanceof EntityDjinn) {
         return CreatureManager.getInstance().getEntityClass("wraith");
      } else {
         return fusable instanceof EntityAegis ? CreatureManager.getInstance().getEntityClass("spectre") : null;
      }
   }
}
