package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySalamander extends RideableCreatureEntity implements IMob {
   public EntitySalamander(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.isLavaCreature = true;
      this.hasAttackSound = true;
      this.hasJumpSound = true;
      this.canGrow = true;
      this.babySpawnChance = 0.01D;
      this.solidCollision = false;
      this.setupMob();
      this.stepHeight = 1.0F;
      this.setPathPriority(PathNodeType.LAVA, 0.0F);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
   }

   public void riderEffects(EntityLivingBase rider) {
      rider.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 105, 1));
      if (rider.isPotionActive(ObjectManager.getEffect("penetration"))) {
         rider.removePotionEffect(ObjectManager.getEffect("penetration"));
      }

      if (rider.isBurning()) {
         rider.extinguish();
      }

   }

   public float getAISpeedModifier() {
      return this.lavaContact() ? 2.0F : 1.0F;
   }

   public float getBlockPathWeight(int x, int y, int z) {
      int waterWeight = 10;
      BlockPos pos = new BlockPos(x, y, z);
      return this.getEntityWorld().getBlockState(pos).getBlock() == Blocks.LAVA ? (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1) : super.getBlockPathWeight(x, y, z);
   }

   public boolean isPushedByWater() {
      return false;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.85D;
   }

   public void specialAttack() {
      double distance = 5.0D;
      List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), new Predicate<EntityLivingBase>() {
         public boolean apply(EntityLivingBase possibleTarget) {
            return possibleTarget.isEntityAlive() && possibleTarget != EntitySalamander.this && !EntitySalamander.this.isRidingOrBeingRiddenBy(possibleTarget) && !EntitySalamander.this.isOnSameTeam(possibleTarget) && EntitySalamander.this.canAttackClass(possibleTarget.getClass()) && EntitySalamander.this.canAttackEntity(possibleTarget);
         }
      });
      if (!possibleTargets.isEmpty()) {
         for(EntityLivingBase possibleTarget : possibleTargets) {
            boolean doDamage = true;
            if (this.getRider() instanceof EntityPlayer && MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)this.getRider(), possibleTarget))) {
               doDamage = false;
            }

            if (doDamage) {
               possibleTarget.setFire(5);
            }
         }
      }

      this.playAttackSound();
      this.triggerAttackCooldown();
   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote) {
         if (!this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
               this.specialAttack();
               this.applyStaminaCost();
            }
         }
      }
   }

   public float getStaminaCost() {
      return 15.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 100;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
   }

   public void onDismounted(Entity entity) {
      super.onDismounted(entity);
      if (entity != null && entity instanceof EntityLivingBase) {
         ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 100, 1));
      }

   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean canBurn() {
      return false;
   }

   public boolean canBreatheUnderlava() {
      return true;
   }

   public boolean canBreatheAir() {
      return true;
   }

   public float getFallResistance() {
      return 100.0F;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return damageSrc.isFireDamage() ? 0.0F : super.getDamageModifier(damageSrc);
   }

   public void applyDropEffects(EntityItemCustom entityItem) {
      entityItem.setCanBurn(false);
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   public boolean petControlsEnabled() {
      return true;
   }
}
