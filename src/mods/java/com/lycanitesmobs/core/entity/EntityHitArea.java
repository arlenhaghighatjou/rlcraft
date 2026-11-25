package com.lycanitesmobs.core.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityHitArea extends Entity {
   public Entity owner;
   protected static final DataParameter<Float> WIDTH = EntityDataManager.createKey(EntityHitArea.class, DataSerializers.FLOAT);
   protected static final DataParameter<Float> HEIGHT = EntityDataManager.createKey(EntityHitArea.class, DataSerializers.FLOAT);

   public EntityHitArea(Entity ownerEntity, float width, float height) {
      super(ownerEntity.getEntityWorld());
      this.owner = ownerEntity;
      this.setSize(width, height);
      this.noClip = true;
      this.isImmuneToFire = true;
   }

   public EntityHitArea(World world) {
      super(world);
   }

   protected void entityInit() {
      this.dataManager.register(WIDTH, Float.valueOf(this.width));
      this.dataManager.register(HEIGHT, Float.valueOf(this.height));
   }

   public void onUpdate() {
      if ((this.owner == null || this.owner.isDead) && !this.getEntityWorld().isRemote) {
         this.setDead();
      }

      super.onUpdate();
      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(WIDTH, Float.valueOf(this.width));
         this.dataManager.set(HEIGHT, Float.valueOf(this.height));
      } else {
         float newWidth = ((Float)this.dataManager.get(WIDTH)).floatValue();
         float newHeight = ((Float)this.dataManager.get(HEIGHT)).floatValue();
         if (this.width != newWidth || this.height != newHeight) {
            this.setSize(newWidth, newHeight);
         }
      }

   }

   public boolean isBurning() {
      return false;
   }

   protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
   }

   protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource damageSource, float damageAmount) {
      if (this.getEntityWorld().isRemote) {
         return true;
      } else if (this.isEntityInvulnerable(damageSource)) {
         return false;
      } else if (this.owner == null) {
         return true;
      } else {
         return this.owner instanceof BaseCreatureEntity ? ((BaseCreatureEntity)this.owner).attackEntityFromArea(this, damageSource, damageAmount) : this.owner.attackEntityFrom(damageSource, damageAmount);
      }
   }

   public boolean isEntityEqual(Entity entity) {
      return this == entity || this.owner == entity;
   }

   public String getName() {
      return this.owner != null ? this.owner.getName() : "Hit Area";
   }
}
