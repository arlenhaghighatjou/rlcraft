package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityIgnibus extends RideableCreatureEntity implements IGroupHeavy {
   protected boolean wantsToLand;
   protected boolean isLanded;

   public EntityIgnibus(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.isLavaCreature = true;
      this.flySoundSpeed = 20;
      this.hasAttackSound = false;
      this.setAttackCooldownMax(20);
      this.setupMob();
      this.stepHeight = 1.0F;
      this.hitAreaWidthScale = 1.5F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setStaminaTime(100).setRange(24.0F).setMinChaseDistance(10.0F));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (!this.isLanded) {
            if (this.wantsToLand) {
               if (!this.isLanded && this.isSafeToLand()) {
                  this.isLanded = true;
               }
            } else if (!this.hasPickupEntity() && !this.hasAttackTarget() && this.updateTick % 100L == 0L && this.getRNG().nextBoolean()) {
               this.wantsToLand = true;
            }
         } else {
            this.wantsToLand = false;
            if (this.hasPickupEntity() || this.getControllingPassenger() != null || this.getLeashed() || this.isInWater() || !this.isTamed() && this.updateTick % 100L == 0L && this.getRNG().nextBoolean()) {
               this.leap(1.0D, 1.0D);
               this.isLanded = false;
            }

            if (this.isTamed() && !this.isSitting()) {
               this.isLanded = false;
            }
         }

         if (!this.hasPickupEntity() && this.getControllingPassenger() == null && !this.hasAttackTarget() && !this.isInWater()) {
            if (this.isTamed() && this.isSitting() && !this.getLeashed()) {
               this.wantsToLand = true;
            }
         } else {
            this.wantsToLand = false;
         }
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public void riderEffects(EntityLivingBase rider) {
      rider.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 105, 1));
      super.riderEffects(rider);
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

   public boolean isFlying() {
      return !this.isLanded;
   }

   public boolean isStrongSwimmer() {
      return false;
   }

   public void attackRanged(Entity target, float range) {
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("primeember");
      if (projectileInfo != null) {
         List<RapidFireProjectileEntity> projectiles = new ArrayList();
         RapidFireProjectileEntity projectileEntry = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         projectiles.add(projectileEntry);
         RapidFireProjectileEntity projectileEntry2 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         ++projectileEntry2.offsetX;
         projectileEntry2.setProjectileScale(0.125F);
         projectiles.add(projectileEntry2);
         RapidFireProjectileEntity projectileEntry3 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         --projectileEntry3.offsetX;
         projectileEntry3.setProjectileScale(0.125F);
         projectiles.add(projectileEntry3);
         RapidFireProjectileEntity projectileEntry4 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         ++projectileEntry4.offsetZ;
         projectileEntry4.setProjectileScale(0.125F);
         projectiles.add(projectileEntry4);
         RapidFireProjectileEntity projectileEntry5 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         --projectileEntry5.offsetZ;
         projectileEntry5.setProjectileScale(0.125F);
         projectiles.add(projectileEntry5);
         RapidFireProjectileEntity projectileEntry6 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         ++projectileEntry6.offsetY;
         projectileEntry6.setProjectileScale(0.125F);
         projectiles.add(projectileEntry6);
         RapidFireProjectileEntity projectileEntry7 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         --projectileEntry7.offsetY;
         projectileEntry7.setProjectileScale(0.125F);
         projectiles.add(projectileEntry7);

         for(RapidFireProjectileEntity projectile : projectiles) {
            projectile.setProjectileScale(0.125F);
            projectile.posY -= (double)(this.height * 0.2F);
            float accuracy = 4.0F * (this.getRNG().nextFloat() - 0.5F);
            double d0 = target.posX - this.posX + (double)accuracy;
            double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + (double)accuracy;
            double d2 = target.posZ - this.posZ + (double)accuracy;
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
            float velocity = 1.2F;
            projectile.shoot(d0, d1 + (double)f1, d2, velocity, 6.0F);
            projectile.setProjectileScale(2.0F);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().spawnEntity(projectile);
         }

         super.attackRanged(target, range);
      }
   }

   public boolean canBurn() {
      return false;
   }

   public boolean canBreatheUnderlava() {
      return true;
   }

   public float getFallResistance() {
      return 100.0F;
   }

   public float getDamageModifier(DamageSource damageSrc) {
      return damageSrc.isFireDamage() ? 0.0F : super.getDamageModifier(damageSrc);
   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote) {
         if (!this.abilityToggled) {
            if (this.hasPickupEntity()) {
               this.dropPickupEntity();
            } else if (this.getStamina() >= this.getStaminaCost()) {
               if (rider instanceof EntityPlayer) {
                  EntityPlayer player = (EntityPlayer)rider;
                  ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("primeember");
                  if (projectileInfo == null) {
                     return;
                  }

                  List<RapidFireProjectileEntity> projectiles = new ArrayList();
                  RapidFireProjectileEntity projectileEntry = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), player, 15, 3);
                  projectiles.add(projectileEntry);
                  RapidFireProjectileEntity projectileEntry2 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
                  ++projectileEntry2.offsetX;
                  projectileEntry2.setProjectileScale(0.25F);
                  projectiles.add(projectileEntry2);
                  RapidFireProjectileEntity projectileEntry3 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
                  --projectileEntry3.offsetX;
                  projectileEntry3.setProjectileScale(0.25F);
                  projectiles.add(projectileEntry3);
                  RapidFireProjectileEntity projectileEntry4 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
                  ++projectileEntry4.offsetZ;
                  projectileEntry4.setProjectileScale(0.25F);
                  projectiles.add(projectileEntry4);
                  RapidFireProjectileEntity projectileEntry5 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
                  --projectileEntry5.offsetZ;
                  projectileEntry5.setProjectileScale(0.25F);
                  projectiles.add(projectileEntry5);
                  RapidFireProjectileEntity projectileEntry6 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
                  ++projectileEntry6.offsetY;
                  projectileEntry6.setProjectileScale(0.25F);
                  projectiles.add(projectileEntry6);
                  RapidFireProjectileEntity projectileEntry7 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
                  projectileEntry7.offsetY -= 10.0D;
                  projectileEntry7.setProjectileScale(0.25F);
                  projectiles.add(projectileEntry7);

                  for(RapidFireProjectileEntity projectile : projectiles) {
                     projectile.setProjectileScale(1.0F);
                     projectile.posY -= (double)(this.height / 4.0F);
                     this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                     this.getEntityWorld().spawnEntity(projectile);
                  }

                  this.triggerAttackCooldown();
               }

               this.applyStaminaCost();
            }
         }
      }
   }

   public float getStaminaCost() {
      return 2.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 40;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
   }

   public float getBrightness() {
      return this.isAttackOnCooldown() ? 1.0F : super.getBrightness();
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return this.isAttackOnCooldown() ? 15728880 : super.getBrightnessForRender();
   }
}
