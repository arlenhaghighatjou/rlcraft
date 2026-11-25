package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileElviaBall extends EntitySRPProjectile {
   public EntityProjectileElviaBall(World worldIn) {
      super(worldIn);
      this.setSize(0.3F, 0.3F);
   }

   public EntityProjectileElviaBall(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3F, 0.3F);
   }

   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.EXPLOSION_NORMAL;
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

            result.entityHit.attackEntityFrom(damagesource, (float)SRPAttributes.ELVIA_ATTACK_DAMAGE);
            this.attackEntityAsMobMinimum(result.entityHit, (EntityParasiteBase)this.shootingEntity);
         }

         this.setDead();
      }

   }
}
