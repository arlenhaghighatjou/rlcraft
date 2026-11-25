package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityProjectileLenciaBall extends EntitySRPProjectile {
   public EntityProjectileLenciaBall(World worldIn) {
      super(worldIn);
      this.setSize(0.3F, 0.3F);
   }

   public EntityProjectileLenciaBall(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
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

            result.entityHit.attackEntityFrom(damagesource, (float)SRPAttributes.LENCIA_ATTACK_DAMAGE);
            this.attackEntityAsMobMinimum(result.entityHit, (EntityParasiteBase)this.shootingEntity);
         }

         boolean flag = ForgeEventFactory.getMobGriefingEvent(this.world, this) && SRPConfigMobs.lenciaGriefing;
         ParasiteEventEntity.createExplosion(this.world, this.shootingEntity, this.posX, this.posY, this.posZ, 10.0F, flag);
         this.setDead();
      }

   }
}
