package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityArix extends TameableCreatureEntity implements IMob {
   protected boolean wantsToLand;
   protected boolean isLanded;

   public EntityArix(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.hasAttackSound = false;
      this.flySoundSpeed = 20;
      this.stepHeight = 1.0F;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(5.0F).setCheckSight(false));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (this.isLanded) {
            this.wantsToLand = false;
            if (!this.isSitting() && this.updateTick % 100L == 0L && this.getRNG().nextBoolean()) {
               this.leap(1.0D, 1.0D);
               this.isLanded = false;
            }
         } else if (this.wantsToLand) {
            if (this.isSafeToLand()) {
               this.isLanded = true;
            }
         } else if (this.updateTick % 100L == 0L && this.getRNG().nextBoolean()) {
            this.wantsToLand = true;
         }
      }

      if (this.getEntityWorld().isRemote && !this.hasPerchTarget()) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SNOWBALL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public BlockPos getWanderPosition(BlockPos wanderPosition) {
      if (this.wantsToLand || !this.isLanded) {
         BlockPos groundPos;
         for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && this.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.AIR; groundPos = groundPos.down()) {
            ;
         }

         if (this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid()) {
            return groundPos.up();
         }
      }

      return super.getWanderPosition(wanderPosition);
   }

   public double getFlightOffset() {
      if (!this.wantsToLand) {
         super.getFlightOffset();
      }

      return 0.0D;
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("icefireball", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 0.8F, 2.0F, 6.0F);
      super.attackRanged(target, range);
   }

   public boolean isFlying() {
      return !this.isLanded;
   }

   public boolean isStrongSwimmer() {
      return true;
   }

   public boolean canBreatheUnderwater() {
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
      return type.equals("ooze") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public float getFallResistance() {
      return 100.0F;
   }
}
