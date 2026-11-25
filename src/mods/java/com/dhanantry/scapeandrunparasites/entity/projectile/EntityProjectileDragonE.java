package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityProjectileDragonE extends EntitySRPProjectile {
   private int ticksInAir;

   public EntityProjectileDragonE(World worldIn) {
      super(worldIn);
      this.setSize(0.3F, 0.3F);
   }

   public EntityProjectileDragonE(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3F, 0.3F);
   }

   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.DRAGON_BREATH;
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(4.0D);

         for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
            if (!(mob instanceof EntityParasiteBase) || mob instanceof EntityNak) {
               SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 200, 1);
               DamageSource damagesource;
               if (this.shootingEntity == null) {
                  damagesource = DamageSource.causeThrownDamage(this, this);
               } else {
                  damagesource = DamageSource.causeThrownDamage(this, this.shootingEntity);
               }

               mob.attackEntityFrom(damagesource, (float)SRPAttributes.INFDRAGONE_RANGED_ATTACK_DAMAGE);
               this.attackEntityAsMobMinimum(mob, (EntityParasiteBase)this.shootingEntity);
            }
         }

         EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.world, this.posX, this.posY, this.posZ);
         entityareaeffectcloud.setRadius(3.0F, 3.0F);
         entityareaeffectcloud.setRadiusOnUse(-0.5F);
         entityareaeffectcloud.setWaitTime(10);
         entityareaeffectcloud.setDuration(100);
         entityareaeffectcloud.setParticle(EnumParticleTypes.DRAGON_BREATH);
         entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.POISON, 300, 0));
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 1));
         this.world.spawnEntity(entityareaeffectcloud);
         this.setDead();
      }

   }

   public void onUpdate() {
      if (this.world.isRemote || (this.shootingEntity == null || !this.shootingEntity.isDead) && this.world.isBlockLoaded(new BlockPos(this))) {
         super.onUpdate();
         if (this.isFireballFiery()) {
            this.setFire(1);
         }

         ++this.ticksInAir;
         RayTraceResult raytraceresult = ProjectileHelper.forwardsRaycast(this, true, this.ticksInAir >= 25, this.shootingEntity);
         if (raytraceresult != null && !ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onImpact(raytraceresult);
         }

         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
         ProjectileHelper.rotateTowardsMovement(this, 0.2F);
         float f = this.getMotionFactor();
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               float f1 = 0.25F;
               this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ, new int[0]);
            }

            f = 0.8F;
         }

         this.motionX += this.accelerationX;
         this.motionY += this.accelerationY;
         this.motionZ += this.accelerationZ;
         this.motionX *= (double)f;
         this.motionY *= (double)f;
         this.motionZ *= (double)f;

         for(int i = 0; i <= 4; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(this.getParticleType(), this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
         }

         this.setPosition(this.posX, this.posY, this.posZ);
      } else {
         this.setDead();
      }

   }
}
