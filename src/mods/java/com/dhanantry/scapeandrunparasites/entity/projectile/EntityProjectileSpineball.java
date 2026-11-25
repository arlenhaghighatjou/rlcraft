package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityProjectileSpineball extends EntitySRPProjectile {
   private float damage;
   private int duration;
   private int amp;
   private double item;

   public EntityProjectileSpineball(World worldIn) {
      super(worldIn);
      this.setSize(0.3F, 0.3F);
   }

   public EntityProjectileSpineball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, float projDamage) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.setSize(0.3F, 0.3F);
      this.damage = projDamage;
   }

   protected EnumParticleTypes getParticleType() {
      return EnumParticleTypes.SLIME;
   }

   public void setDurationAmplifier(int duration, int amplifier) {
      this.duration = duration * 20;
      this.amp = amplifier - 1;
   }

   public void setGearDamage(double in) {
      this.item = in;
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         if (result.entityHit != null && result.entityHit instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase)result.entityHit;
            if (target instanceof EntityParasiteBase && !(target instanceof EntityNak)) {
               this.setDead();
               return;
            }

            boolean primitive = false;
            EntityParasiteBase shooter = (EntityParasiteBase)this.shootingEntity;
            if (shooter instanceof EntityEmana) {
               primitive = true;
            }

            DamageSource damagesource;
            if (this.shootingEntity == null) {
               damagesource = DamageSource.causeThrownDamage(this, this);
            } else {
               damagesource = DamageSource.causeThrownDamage(this, this.shootingEntity);
            }

            target.attackEntityFrom(damagesource, this.damage);
            target.addPotionEffect(new PotionEffect(MobEffects.POISON, this.duration, this.amp));
            this.attackEntityAsMobMinimum(target, (EntityParasiteBase)this.shootingEntity);
            this.damageArmor(target, this.item);
            if (target.getHealth() <= 0.0F && primitive && shooter.isEntityAlive()) {
               double k = shooter.getKillC();
               ++k;
               shooter.setKillC(k);
               shooter.particleStatus((byte)5);
               if (k > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
                  ParasiteEventEntity.spawnNext(shooter, new EntityEmanaAdapted(this.world), true, true);
               }
            }
         }

         this.setDead();
      }

   }

   private void damageArmor(EntityLivingBase target, double percen) {
      ArrayList<ItemStack> off = new ArrayList();
      Iterable<ItemStack> gear = target.getArmorInventoryList();
      if (gear != null && !gear.equals(Collections.emptyList())) {
         for(ItemStack part : gear) {
            if (!part.isEmpty() && part.isItemStackDamageable()) {
               off.add(part);
            }
         }

         if (!off.isEmpty()) {
            for(ItemStack part : off) {
               part.damageItem((int)((double)part.getMaxDamage() * percen), target);
            }
         }
      }

   }
}
