package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntitySRPProjectile extends EntityFireball {
   public EntitySRPProjectile(World worldIn) {
      super(worldIn);
      this.setSize(0.3F, 0.3F);
   }

   public EntitySRPProjectile(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3F, 0.3F);
   }

   public boolean isBurning() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }

   protected boolean attackEntityAsMobMinimum(Entity entityIn, EntityParasiteBase attacker) {
      if (entityIn != null && attacker != null) {
         if (!attacker.isEntityAlive()) {
            return false;
         } else {
            return entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase) ? attacker.attackEntityAsMobMinimum((EntityLivingBase)entityIn) : false;
         }
      } else {
         return false;
      }
   }
}
