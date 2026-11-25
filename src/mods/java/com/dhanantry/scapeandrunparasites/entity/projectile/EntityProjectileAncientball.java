package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileAncientball extends EntitySRPProjectile {
   public EntityProjectileAncientball(World worldIn) {
      super(worldIn);
      this.setSize(0.3F, 0.3F);
   }

   public EntityProjectileAncientball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3F, 0.3F);
   }

   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.SLIME;
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         if (result.entityHit != null && result.entityHit instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase)result.entityHit;
            if (target instanceof EntityParasiteBase && !(target instanceof EntityNak)) {
               this.setDead();
               return;
            }

            float damageR = SRPAttributes.ORONCO_ATTACK_DAMAGE;
            DamageSource damagesource;
            if (this.shootingEntity == null) {
               damagesource = DamageSource.causeThrownDamage(this, this);
            } else {
               damagesource = DamageSource.causeThrownDamage(this, this.shootingEntity);
            }

            target.attackEntityFrom(damagesource, damageR);
            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 60, 0));
            this.attackEntityAsMobMinimum(target, (EntityParasiteBase)this.shootingEntity);
         }

         EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
         entityareaeffectcloud.setRadius(this.width * 4.0F);
         entityareaeffectcloud.setRadiusOnUse(-0.5F);
         entityareaeffectcloud.setWaitTime(5);
         entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration());
         entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.WITHER, 300, 0));
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
         this.world.spawnEntity(entityareaeffectcloud);
         this.setDead();
      }

   }
}
