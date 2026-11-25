package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileAngedball extends EntitySRPProjectile {
   public EntityProjectileAngedball(World worldIn) {
      super(worldIn);
      this.setSize(0.3F, 0.3F);
   }

   public EntityProjectileAngedball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3F, 0.3F);
   }

   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.SLIME;
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         if (result.entityHit != null && result.entityHit instanceof EntityLivingBase) {
            if (result.entityHit instanceof EntityParasiteBase && !(result.entityHit instanceof EntityNak)) {
               this.setDead();
               return;
            }

            DamageSource damagesource;
            if (this.shootingEntity == null) {
               damagesource = DamageSource.causeThrownDamage(this, this);
            } else {
               damagesource = DamageSource.causeThrownDamage(this, this.shootingEntity);
            }

            result.entityHit.attackEntityFrom(damagesource, (float)SRPAttributes.ANGED_RANGED_ATTACK_DAMAGE);
            this.attackEntityAsMobMinimum(result.entityHit, (EntityParasiteBase)this.shootingEntity);
         }

         EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.world, this.posX, this.posY, this.posZ);
         entityareaeffectcloud.setRadius(2.0F, 2.0F);
         entityareaeffectcloud.setRadiusOnUse(-0.5F);
         entityareaeffectcloud.setWaitTime(10);
         entityareaeffectcloud.setDuration(100);
         entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.POISON, 300, 0, false, false));
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.CORRO_E, 100, 0, false, false));
         this.world.spawnEntity(entityareaeffectcloud);
         this.setDead();
      }

   }
}
