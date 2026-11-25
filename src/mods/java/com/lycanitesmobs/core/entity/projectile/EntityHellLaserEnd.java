package com.lycanitesmobs.core.entity.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.core.entity.LaserEndProjectileEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityHellLaserEnd extends LaserEndProjectileEntity {
   public EntityHellLaserEnd(World world) {
      super(world);
   }

   public EntityHellLaserEnd(World world, double par2, double par4, double par6, EntityProjectileLaser laser) {
      super(world, par2, par4, par6, laser);
   }

   public EntityHellLaserEnd(World world, EntityLivingBase shooter, EntityProjectileLaser laser) {
      super(world, shooter, laser);
   }

   public void setup() {
      this.entityName = "helllaser";
      this.modInfo = LycanitesMobs.modInfo;
   }

   public void setStats() {
      super.setStats();
      this.setSpeed(1.0D);
   }

   public ResourceLocation getTexture() {
      if (AssetManager.getTexture(this.entityName + "End") == null) {
         AssetManager.addTexture(this.entityName + "End", this.modInfo, "textures/items/" + this.entityName.toLowerCase() + "_end.png");
      }

      return AssetManager.getTexture(this.entityName + "End");
   }

   public SoundEvent getLaunchSound() {
      return AssetManager.getSound(this.entityName);
   }
}
