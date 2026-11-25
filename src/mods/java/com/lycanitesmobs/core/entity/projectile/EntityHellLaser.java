package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityHellLaser extends EntityProjectileLaser {
   public EntityHellLaser(World world) {
      super(world);
   }

   public EntityHellLaser(World world, double par2, double par4, double par6, int setTime, int setDelay) {
      super(world, par2, par4, par6, setTime, setDelay);
   }

   public EntityHellLaser(World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
      super(world, par2, par4, par6, setTime, setDelay, followEntity);
   }

   public EntityHellLaser(World world, EntityLivingBase entityShooter, int setTime, int setDelay) {
      super(world, entityShooter, setTime, setDelay);
   }

   public EntityHellLaser(World world, EntityLivingBase entityShooter, int setTime, int setDelay, Entity followEntity) {
      super(world, entityShooter, setTime, setDelay, followEntity);
   }

   public void setup() {
      this.entityName = "helllaser";
      this.modInfo = LycanitesMobs.modInfo;
      this.setDamage(1);
   }

   public void setStats() {
      super.setStats();
      this.setRange(16.0F);
      this.setLaserWidth(4.0F);
   }

   public Class getLaserEndClass() {
      return EntityHellLaserEnd.class;
   }

   public boolean updateDamage(Entity target) {
      boolean damageDealt = super.updateDamage(target);
      if (this.getThrower() != null && damageDealt && target instanceof EntityLivingBase) {
         ((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.WITHER, this.getEffectDuration(5), 0));
      }

      return damageDealt;
   }

   public ResourceLocation getBeamTexture() {
      if (AssetManager.getTexture(this.entityName + "Beam") == null) {
         AssetManager.addTexture(this.entityName + "Beam", this.modInfo, "textures/items/" + this.entityName.toLowerCase() + "_beam.png");
      }

      return AssetManager.getTexture(this.entityName + "Beam");
   }

   public SoundEvent getLaunchSound() {
      return AssetManager.getSound(this.entityName);
   }

   public SoundEvent getBeamSound() {
      return AssetManager.getSound(this.entityName);
   }
}
