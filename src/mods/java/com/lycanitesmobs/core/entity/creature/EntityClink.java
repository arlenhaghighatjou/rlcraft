package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityClink extends TameableCreatureEntity implements IMob {
   public EntityClink(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.setupMob();
      this.attackPhaseMax = 3;
      this.setAttackCooldownMax(10);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(4.0F));
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("throwingscythe", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 2.0F, 1.0F);
      this.nextAttackPhase();
      super.attackRanged(target, range);
   }

   public int getMeleeCooldown() {
      return this.getAttackPhase() == 2 ? super.getMeleeCooldown() : Math.round((float)super.getMeleeCooldown() / 6.0F);
   }

   public int getRangedCooldown() {
      return this.getAttackPhase() == 2 ? super.getRangedCooldown() : Math.round((float)super.getRangedCooldown() / 6.0F);
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("cactus") ? false : super.isDamageTypeApplicable(type, source, damage);
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
}
