package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySkylus extends TameableCreatureEntity implements IMob {
   public EntitySkylus(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = false;
      this.spawnsInWater = true;
      this.hasAttackSound = true;
      this.babySpawnChance = 0.01D;
      this.canGrow = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false).setRange(1.0D));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.getControllingPassenger() == null && this.hasPickupEntity()) {
         ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
         if (extendedEntity != null) {
            extendedEntity.setPickedUpByEntity(this);
         }

         if (this.ticksExisted % 100 == 0 && this.getRNG().nextBoolean()) {
            this.dropPickupEntity();
         }
      }

   }

   public float getAISpeedModifier() {
      return this.getHealth() > this.getMaxHealth() / 2.0F ? 0.5F : 1.0F;
   }

   public float getBlockPathWeight(int x, int y, int z) {
      int waterWeight = 10;
      Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
      if (block == Blocks.WATER) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z))) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getAttackTarget() != null) {
         return super.getBlockPathWeight(x, y, z);
      } else {
         return this.waterContact() ? -999999.0F : super.getBlockPathWeight(x, y, z);
      }
   }

   public boolean isStrongSwimmer() {
      return true;
   }

   public boolean canWalk() {
      return false;
   }

   public void pickupEntity(EntityLivingBase entity) {
      super.pickupEntity(entity);
      this.leap(-1.0D, -0.5D);
   }

   public double[] getPickupOffset(Entity entity) {
      return new double[]{0.0D, 0.0D, 2.0D};
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if (target instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)target;
            if (this.canPickupEntity(entityLivingBase)) {
               this.pickupEntity(entityLivingBase);
            }
         }

         return true;
      }
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean canBreatheAir() {
      return false;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return this.getHealth() > this.getMaxHealth() / 2.0F ? 0.25F : 1.0F;
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
