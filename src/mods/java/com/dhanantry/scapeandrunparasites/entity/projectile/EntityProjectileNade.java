package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileNade extends EntitySRPProjectile {
   private int duration;
   private int fuse;

   public EntityProjectileNade(World worldIn) {
      super(worldIn);
      this.setSize(0.3F, 0.3F);
   }

   public EntityProjectileNade(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, int fuse, int duration) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3F, 0.3F);
      this.fuse = fuse;
      this.duration = duration;
   }

   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.SLIME;
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         if (this.shootingEntity == null) {
            return;
         }

         if (!this.shootingEntity.isEntityAlive()) {
            return;
         }

         EntityNade nade = new EntityNade(this.world, this.fuse, this.duration);
         nade.setFatherS((EntityPMalleable)this.shootingEntity);
         nade.copyLocationAndAnglesFrom(this);
         this.world.spawnEntity(nade);
         this.setDead();
      }

   }
}
