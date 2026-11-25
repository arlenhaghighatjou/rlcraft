package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.AvoidGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityQuillbeast extends TameableCreatureEntity implements IMob {
   AvoidGoal aiAvoid;

   public EntityQuillbeast(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(true).setEnabled(false));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(10.0F).setChaseTime(-1));
   }

   public void attackRanged(Entity target, float range) {
      for(int i = -2; i < 12; ++i) {
         this.fireProjectile("quill", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 0.75F, 1.0F, (float)i * 2.0F * (this.getRNG().nextFloat() - 0.5F));
      }

      super.attackRanged(target, range);
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
}
