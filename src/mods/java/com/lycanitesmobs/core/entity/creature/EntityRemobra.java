package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityRemobra extends TameableCreatureEntity implements IMob {
   public EntityRemobra(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.flySoundSpeed = 20;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(5.0F));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("venomshot", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 2.0F, 1.0F);
      super.attackRanged(target, range);
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
}
