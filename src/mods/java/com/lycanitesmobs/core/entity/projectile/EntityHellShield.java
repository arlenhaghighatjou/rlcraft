package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHellShield extends BaseProjectileEntity {
   public Entity shootingEntity;

   public EntityHellShield(World world) {
      super(world);
   }

   public EntityHellShield(World world, EntityLivingBase entityLivingBase) {
      super(world, entityLivingBase);
   }

   public EntityHellShield(World world, double x, double y, double z) {
      super(world, x, y, z);
   }

   public void setup() {
      this.entityName = "hellshield";
      this.modInfo = LycanitesMobs.modInfo;
      this.setDamage(0);
      this.setProjectileScale(1.0F);
      this.knockbackChance = 0.0D;
      this.noClip = true;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.posY > (double)(this.getEntityWorld().getHeight() + 20)) {
         this.setDead();
      }

   }

   protected float getGravityVelocity() {
      return 0.0F;
   }

   public void onImpactVisuals() {
      for(int i = 0; i < 8; ++i) {
         this.getEntityWorld().spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   public boolean canDamage(EntityLivingBase entity) {
      return false;
   }

   public SoundEvent getLaunchSound() {
      return null;
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}
