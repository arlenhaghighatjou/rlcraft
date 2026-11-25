package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityEpion extends RideableCreatureEntity implements IMob {
   public boolean griefing = true;

   public EntityEpion(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = false;
      this.flySoundSpeed = 20;
      this.trueSight = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(6.0F));
   }

   public void loadCreatureFlags() {
      this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
   }

   public float getStafeSpeed() {
      return 1.0F;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && !this.isTamed() && !this.isMinion() && !this.isRareVariant() && !this.isFlying() && (this.onGround || this.isInWater()) && this.isEntityAlive()) {
         int explosionRadius = 2;
         if (this.subspecies != null) {
            explosionRadius = 3;
         }

         explosionRadius = Math.max(2, Math.round((float)explosionRadius * (float)this.sizeScale));
         if (this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.griefing) {
            this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, (float)explosionRadius, true);
         } else {
            this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, (float)explosionRadius, false);
         }

         this.setDead();
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public boolean canEntityBeSeen(Entity target) {
      return this.isRareVariant() ? true : super.canEntityBeSeen(target);
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("bloodleech", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 1.2F, 2.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public boolean isFlying() {
      if (this.getEntityWorld().isRemote) {
         return true;
      } else {
         if (this.daylightBurns() && this.getEntityWorld().isDaytime() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.griefing) {
            float brightness = this.getBrightness();
            if (brightness > 0.5F && this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
               return false;
            }
         }

         return true;
      }
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

   public boolean daylightBurns() {
      return !this.isMinion() && !this.hasMaster() && !this.isTamed() && !this.isRareVariant();
   }

   public float getFallResistance() {
      return 100.0F;
   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote && this.updateTick % 2L != 0L) {
         if (this.getStamina() >= this.getStaminaCost()) {
            if (rider instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)rider;
               ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("bloodleech");
               if (projectileInfo != null) {
                  BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), player);
                  this.getEntityWorld().spawnEntity(projectile);
                  this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                  this.triggerAttackCooldown();
               }
            }

            this.applyStaminaCost();
         }
      }
   }

   public float getStaminaCost() {
      return 5.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 0;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
   }

   public ResourceLocation getTexture() {
      if (!"Vampire Bat".equals(this.getCustomNameTag())) {
         return super.getTexture();
      } else {
         String textureName = this.getTextureName() + "_vampirebat";
         if (AssetManager.getTexture(textureName) == null) {
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
         }

         return AssetManager.getTexture(textureName);
      }
   }

   @SideOnly(Side.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getEntityBoundingBox().grow(10.0D, 10.0D, 10.0D).offset(0.0D, -5.0D, 0.0D);
   }
}
