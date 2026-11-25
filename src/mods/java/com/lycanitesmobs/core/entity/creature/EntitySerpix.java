package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntitySerpix extends TameableCreatureEntity implements IGroupHeavy {
   public EntitySerpix(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.spawnsOnLand = true;
      this.spawnsInWater = true;
      this.hasAttackSound = false;
      this.trueSight = true;
      this.babySpawnChance = 0.25D;
      this.growthTime = -120000;
      this.setupMob();
      this.hitAreaWidthScale = 1.5F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextPriorityGoalIndex++, (new StealthGoal(this)).setStealthTime(60));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.5D).setStaminaTime(100).setRange(12.0F).setMinChaseDistance(8.0F));
   }

   public float getAISpeedModifier() {
      return this.isInWater() ? 2.0F : 1.0F;
   }

   public boolean isPushedByWater() {
      return false;
   }

   public void attackRanged(Entity target, float range) {
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("blizzard");
      if (projectileInfo != null) {
         List<RapidFireProjectileEntity> projectiles = new ArrayList();
         RapidFireProjectileEntity projectileEntry = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         projectiles.add(projectileEntry);
         RapidFireProjectileEntity projectileEntry2 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         ++projectileEntry2.offsetX;
         projectiles.add(projectileEntry2);
         RapidFireProjectileEntity projectileEntry3 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         --projectileEntry3.offsetX;
         projectiles.add(projectileEntry3);
         RapidFireProjectileEntity projectileEntry4 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         ++projectileEntry4.offsetZ;
         projectiles.add(projectileEntry4);
         RapidFireProjectileEntity projectileEntry5 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         --projectileEntry5.offsetZ;
         projectiles.add(projectileEntry5);
         RapidFireProjectileEntity projectileEntry6 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         ++projectileEntry6.offsetY;
         projectiles.add(projectileEntry6);
         RapidFireProjectileEntity projectileEntry7 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
         projectileEntry7.offsetY -= 10.0D;
         projectiles.add(projectileEntry7);
         BlockPos launchPos = this.getFacingPosition(4.0D);

         for(RapidFireProjectileEntity projectile : projectiles) {
            projectile.setProjectileScale(1.0F);
            projectile.posY -= (double)(this.height / 4.0F);
            float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);
            double d0 = target.posX - (double)launchPos.getX() + (double)accuracy;
            double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + (double)accuracy;
            double d2 = target.posZ - (double)launchPos.getZ() + (double)accuracy;
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
            float velocity = 1.2F;
            projectile.shoot(d0, d1 + (double)f1, d2, velocity, 6.0F);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            projectile.setPosition((double)launchPos.getX(), (double)launchPos.getY(), (double)launchPos.getZ());
            this.getEntityWorld().spawnEntity(projectile);
         }

         super.attackRanged(target, range);
      }
   }

   public boolean canStealth() {
      if (this.isTamed() && this.isSitting()) {
         return false;
      } else {
         IBlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0));
         if (blockState.getBlock() != Blocks.AIR) {
            if (blockState.getMaterial() == Material.GROUND) {
               return true;
            }

            if (blockState.getMaterial() == Material.GRASS) {
               return true;
            }

            if (blockState.getMaterial() == Material.LEAVES) {
               return true;
            }

            if (blockState.getMaterial() == Material.SAND) {
               return true;
            }

            if (blockState.getMaterial() == Material.CLAY) {
               return true;
            }

            if (blockState.getMaterial() == Material.SNOW) {
               return true;
            }

            if (blockState.getMaterial() == Material.CRAFTED_SNOW) {
               return true;
            }
         }

         return blockState.getBlock() == Blocks.NETHERRACK;
      }
   }

   public boolean canBeTempted() {
      return this.isChild();
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
      if (type.equals("ooze")) {
         return false;
      } else if (type.equals("cactus")) {
         return false;
      } else {
         return type.equals("inWall") ? false : super.isDamageTypeApplicable(type, source, damage);
      }
   }
}
