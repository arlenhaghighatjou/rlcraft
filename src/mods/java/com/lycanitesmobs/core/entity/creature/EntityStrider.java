package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityStrider extends RideableCreatureEntity implements IGroupHeavy {
   protected int pickupCooldown = 100;
   private int pickupTime = 0;

   public EntityStrider(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.hasAttackSound = true;
      this.babySpawnChance = 0.0D;
      this.canGrow = true;
      this.setupMob();
      this.hitAreaWidthScale = 1.5F;
      this.hitAreaHeightScale = 1.0F;
      this.setPathPriority(PathNodeType.WATER, 0.0F);
      this.stepHeight = 4.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (this.isTamed() && this.hasPickupEntity() && this.getPickupEntity() == this.getOwner()) {
            this.dropPickupEntity();
         }

         if (this.hasPickupEntity()) {
            ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
            if (extendedEntity != null) {
               extendedEntity.setPickedUpByEntity(this);
            }

            if (this.isTamed() && !this.canAttackEntity(this.getPickupEntity())) {
               this.getPickupEntity().addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, this.getEffectDuration(5), 1));
            } else if (this.pickupTime++ % 40 == 0) {
               this.attackEntityAsMob(this.getPickupEntity(), 0.5D);
               if (this.getPickupEntity() instanceof EntityLivingBase && ObjectManager.getEffect("penetration") != null) {
                  this.getPickupEntity().addPotionEffect(new PotionEffect(ObjectManager.getEffect("penetration"), this.getEffectDuration(5), 1));
               }
            }
         } else {
            if (this.pickupCooldown > 0) {
               --this.pickupCooldown;
            }

            this.pickupTime = 0;
         }
      }

   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote) {
         if (!this.abilityToggled) {
            if (this.hasPickupEntity()) {
               this.dropPickupEntity();
            } else if (this.getStamina() >= this.getStaminaCost()) {
               EntityLivingBase nearestTarget = (EntityLivingBase)this.getNearestEntity(EntityLivingBase.class, (Class)null, 4.0D, false);
               if (this.canPickupEntity(nearestTarget)) {
                  this.pickupEntity(nearestTarget);
               }

               this.applyStaminaCost();
            }
         }
      }
   }

   public float getStaminaCost() {
      return 20.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 100;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
   }

   public float getAISpeedModifier() {
      if (this.isInWater()) {
         return 2.0F;
      } else {
         return this.waterContact() ? 1.5F : super.getAISpeedModifier();
      }
   }

   public boolean isPushedByWater() {
      return false;
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

   public BlockPos getWanderPosition(BlockPos wanderPosition) {
      BlockPos groundPos;
      for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && !this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid(); groundPos = groundPos.down()) {
         ;
      }

      return groundPos.up();
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if (target instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)target;
            if (this.canPickupEntity(entityLivingBase) && this.canEntityBeSeen(target)) {
               this.pickupEntity(entityLivingBase);
               this.pickupCooldown = 100;
            }
         }

         return true;
      }
   }

   public void onDamage(DamageSource damageSrc, float damage) {
      if (this.hasPickupEntity() && !this.isTamed() && this.getRNG().nextFloat() <= 0.25F) {
         this.dropPickupEntity();
      }

      super.onDamage(damageSrc, damage);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public float getFallResistance() {
      return 20.0F;
   }

   public double[] getPickupOffset(Entity entity) {
      return new double[]{0.0D, 5.5D, 0.0D};
   }

   public boolean canPickupEntity(EntityLivingBase entity) {
      return !this.isTamed() && this.pickupCooldown > 0 ? false : super.canPickupEntity(entity);
   }

   public void dropPickupEntity() {
      ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
      if (extendedEntity != null) {
         extendedEntity.setPickedUpByEntity((Entity)null);
      }

      this.pickupEntity = null;
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
