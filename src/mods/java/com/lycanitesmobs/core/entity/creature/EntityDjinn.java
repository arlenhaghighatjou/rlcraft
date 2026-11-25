package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityDjinn extends TameableCreatureEntity implements IMob, IFusable {
   public float fireDamageAbsorbed = 0.0F;
   protected IFusable fusionTarget;

   public EntityDjinn(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(16.0F).setMinChaseDistance(8.0F));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && !this.isTamed() && this.fireDamageAbsorbed >= 10.0F) {
         this.transform(CreatureManager.getInstance().getEntityClass("zephyr"), (Entity)null, false);
      }

   }

   public void attackRanged(Entity target, float range) {
      int projectileCount = 10;

      for(int i = 0; i < projectileCount; ++i) {
         this.fireProjectile("whirlwind", target, range, (float)(360 / projectileCount * i), new Vec3d(0.0D, 0.0D, 0.0D), 0.6F, 2.0F, 1.0F);
      }

      super.attackRanged(target, range);
   }

   public boolean isFlying() {
      return true;
   }

   public boolean isStrongSwimmer() {
      return false;
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
      if (!type.equals("cactus") && !type.equals("inWall")) {
         if (source.isFireDamage()) {
            this.fireDamageAbsorbed += damage;
            return false;
         } else if (type.equals("lightningBolt") && !this.isTamed()) {
            this.transform(CreatureManager.getInstance().getEntityClass("zephyr"), (Entity)null, false);
            return false;
         } else {
            return super.isDamageTypeApplicable(type, source, damage);
         }
      } else {
         return false;
      }
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean canBurn() {
      return false;
   }

   public IFusable getFusionTarget() {
      return this.fusionTarget;
   }

   public void setFusionTarget(IFusable fusionTarget) {
      this.fusionTarget = fusionTarget;
   }

   public Class getFusionClass(IFusable fusable) {
      if (fusable instanceof EntityCinder) {
         return CreatureManager.getInstance().getEntityClass("zephyr");
      } else if (fusable instanceof EntityJengu) {
         return CreatureManager.getInstance().getEntityClass("reiver");
      } else if (fusable instanceof EntityGeonach) {
         return CreatureManager.getInstance().getEntityClass("banshee");
      } else if (fusable instanceof EntityAegis) {
         return CreatureManager.getInstance().getEntityClass("sylph");
      } else {
         return fusable instanceof EntityArgus ? CreatureManager.getInstance().getEntityClass("wraith") : null;
      }
   }
}
