package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.network.MessageEntityPerched;
import com.lycanitesmobs.core.network.MessageEntityPickedUp;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class ExtendedEntity {
   public static String[] FORCE_REMOVE_ENTITY_IDS;
   public static int FORCE_REMOVE_ENTITY_TICKS = 40;
   public EntityLivingBase entity;
   protected Map<String, Integer> projectileCooldownsPrimary = new HashMap();
   protected Map<String, Integer> projectileCooldownsSecondary = new HashMap();
   Vector3d lastSafePos;
   private boolean playerAllowFlyingSnapshot;
   private boolean playerIsFlyingSnapshot;
   public EntityLivingBase lastAttackedEntity;
   public int lastAttackedTime = 0;
   public Entity pickedUpByEntity;
   public Entity perchedByEntity;
   public boolean perchedEntityNoclip = false;
   public FearEntity fearEntity;
   boolean forceRemoveChecked = false;
   boolean forceRemove = false;
   int forceRemoveTicks;

   public static ExtendedEntity getForEntity(EntityLivingBase entity) {
      if (entity == null) {
         return null;
      } else {
         ExtendedEntity extendedEntity = (ExtendedEntity)entity.getCapability(LycanitesMobs.EXTENDED_ENTITY, (EnumFacing)null);
         if (extendedEntity.entity != entity) {
            extendedEntity.setEntity(entity);
         }

         return extendedEntity;
      }
   }

   public ExtendedEntity() {
      this.forceRemoveTicks = FORCE_REMOVE_ENTITY_TICKS;
   }

   public void setEntity(EntityLivingBase entity) {
      this.entity = entity;
   }

   public EntityLivingBase getEntity() {
      return this.entity;
   }

   public void setLastAttackedEntity(EntityLivingBase target) {
      this.lastAttackedEntity = target;
      this.lastAttackedTime = this.entity.ticksExisted;
   }

   public void onUpdate() {
      if (this.entity != null) {
         for(String cooldownName : this.projectileCooldownsPrimary.keySet()) {
            int cooldownValue = ((Integer)this.projectileCooldownsPrimary.get(cooldownName)).intValue();
            if (cooldownValue > 0) {
               this.projectileCooldownsPrimary.put(cooldownName, Integer.valueOf(cooldownValue - 1));
            }
         }

         for(String cooldownName : this.projectileCooldownsSecondary.keySet()) {
            int cooldownValue = ((Integer)this.projectileCooldownsSecondary.get(cooldownName)).intValue();
            if (cooldownValue > 0) {
               this.projectileCooldownsSecondary.put(cooldownName, Integer.valueOf(cooldownValue - 1));
            }
         }

         if (!this.entity.getEntityWorld().isRemote && FORCE_REMOVE_ENTITY_IDS != null && FORCE_REMOVE_ENTITY_IDS.length > 0 && !this.forceRemoveChecked) {
            LycanitesMobs.logDebug("ForceRemoveEntity", "Forced entity removal, checking: " + this.entity.getName());

            for(String forceRemoveID : FORCE_REMOVE_ENTITY_IDS) {
               if (forceRemoveID.equalsIgnoreCase(this.entity.getName())) {
                  this.forceRemove = true;
                  break;
               }
            }

            this.forceRemoveChecked = true;
         }

         if (this.forceRemove && this.forceRemoveTicks-- <= 0) {
            this.entity.setDead();
         }

         if (this.lastSafePos == null) {
            this.lastSafePos = new Vector3d(this.entity.posX, this.entity.posY, this.entity.posZ);
         }

         if (!this.entity.getEntityWorld().getBlockState(this.entity.getPosition()).getMaterial().isSolid()) {
            this.lastSafePos.set(Math.floor(this.entity.posX) + 0.5D, (double)this.entity.getPosition().getY(), Math.floor(this.entity.posZ) + 0.5D);
         }

         if (this.fearEntity != null && !this.fearEntity.isEntityAlive()) {
            this.fearEntity = null;
         }

         try {
            this.updatePickedUpByEntity();
         } catch (Exception var6) {
            ;
         }

         try {
            this.updatedPerchedByEntity();
         } catch (Exception var5) {
            ;
         }

      }
   }

   public void onDeath() {
      this.setPickedUpByEntity((Entity)null);
   }

   public void updatePickedUpByEntity() {
      if (this.pickedUpByEntity != null) {
         if (!this.entity.getEntityWorld().isRemote) {
            if (!this.pickedUpByEntity.isEntityAlive()) {
               this.setPickedUpByEntity((Entity)null);
               return;
            }

            if (this.pickedUpByEntity instanceof EntityLivingBase && ((EntityLivingBase)this.pickedUpByEntity).getHealth() <= 0.0F) {
               this.setPickedUpByEntity((Entity)null);
               return;
            }

            Potion weight = ObjectManager.getEffect("weight");
            if (weight != null && this.entity.isPotionActive(weight)) {
               this.setPickedUpByEntity((Entity)null);
               return;
            }

            if ((double)this.entity.getDistance(this.pickedUpByEntity) > 32.0D) {
               this.setPickedUpByEntity((Entity)null);
               return;
            }
         }

         if (this.pickedUpByEntity != null) {
            double[] pickupOffset = this.getPickedUpOffset();
            this.entity.setPosition(this.pickedUpByEntity.posX + pickupOffset[0], this.pickedUpByEntity.posY + pickupOffset[1], this.pickedUpByEntity.posZ + pickupOffset[2]);
            this.entity.motionX = this.pickedUpByEntity.motionX;
            this.entity.motionY = this.pickedUpByEntity.motionY;
            this.entity.motionZ = this.pickedUpByEntity.motionZ;
            this.entity.fallDistance = 0.0F;
            if (!this.entity.getEntityWorld().isRemote && this.entity instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)this.entity;
               player.capabilities.isFlying = true;
               this.entity.noClip = true;
            }
         }

      }
   }

   public void setPickedUpByEntity(Entity pickedUpByEntity) {
      if (this.pickedUpByEntity != pickedUpByEntity && this.entity != null) {
         if (this.entity.getRidingEntity() != null) {
            this.entity.dismountRidingEntity();
         }

         this.pickedUpByEntity = pickedUpByEntity;
         if (!this.entity.getEntityWorld().isRemote) {
            if (this.entity instanceof EntityPlayer) {
               if (pickedUpByEntity != null) {
                  this.playerAllowFlyingSnapshot = ((EntityPlayer)this.entity).capabilities.allowFlying;
                  this.playerIsFlyingSnapshot = ((EntityPlayer)this.entity).capabilities.isFlying;
               } else {
                  ((EntityPlayer)this.entity).capabilities.isFlying = this.playerIsFlyingSnapshot;
                  this.entity.noClip = false;
               }
            }

            if (this.pickedUpByEntity != null && !(this.entity instanceof EntityPlayer)) {
               double[] pickupOffset = this.getPickedUpOffset();
               this.entity.attemptTeleport(this.pickedUpByEntity.posX + pickupOffset[0], this.pickedUpByEntity.posY + pickupOffset[1], this.pickedUpByEntity.posZ + pickupOffset[2]);
            }

            MessageEntityPickedUp message = new MessageEntityPickedUp(this.entity, pickedUpByEntity);
            LycanitesMobs.packetHandler.sendToDimension(message, this.entity.dimension);
         }

         if (pickedUpByEntity == null) {
            if (this.lastSafePos != null) {
               this.entity.setPosition(this.lastSafePos.getX(), this.lastSafePos.getY(), this.lastSafePos.getZ());
            }

            this.entity.motionX = 0.0D;
            this.entity.motionY = 0.0D;
            this.entity.motionZ = 0.0D;
            this.entity.fallDistance = 0.0F;
         }

      }
   }

   public double[] getPickedUpOffset() {
      double[] pickupOffset = new double[]{0.0D, 0.0D, 0.0D};
      if (this.pickedUpByEntity instanceof BaseCreatureEntity) {
         pickupOffset = ((BaseCreatureEntity)this.pickedUpByEntity).getPickupOffset(this.entity);
      }

      return CreatureManager.getInstance().config.disablePickupOffsets && this.entity instanceof EntityPlayer ? new double[]{0.0D, 0.0D, 0.0D} : pickupOffset;
   }

   public boolean isPickedUp() {
      return this.pickedUpByEntity != null;
   }

   public boolean isFeared() {
      return this.pickedUpByEntity instanceof FearEntity;
   }

   public void setPerchedByEntity(Entity perchedByEntity) {
      if (this.perchedByEntity != null) {
         this.perchedByEntity.noClip = this.perchedEntityNoclip;
         if (this.perchedByEntity instanceof BaseCreatureEntity) {
            ((BaseCreatureEntity)this.perchedByEntity).setPerchTarget((EntityLivingBase)null);
         }
      }

      this.perchedByEntity = perchedByEntity;
      if (perchedByEntity != null) {
         this.perchedEntityNoclip = perchedByEntity.noClip;
         perchedByEntity.noClip = true;
         if (perchedByEntity instanceof BaseCreatureEntity) {
            ((BaseCreatureEntity)perchedByEntity).setPerchTarget(this.entity);
         }
      }

      if (!this.entity.getEntityWorld().isRemote) {
         MessageEntityPerched message = new MessageEntityPerched(this.entity, this.perchedByEntity);
         LycanitesMobs.packetHandler.sendToDimension(message, this.entity.dimension);
      }

   }

   public Entity getPerchedByEntity() {
      return this.perchedByEntity;
   }

   public Vec3d getPerchPosition() {
      double angle = Math.toRadians((double)this.entity.rotationYaw) + 90.0D;
      double xPerchPos = this.entity.posX;
      double zPerchPos = this.entity.posZ;
      double distance = (double)this.entity.width * 0.7D;
      if (distance != 0.0D) {
         xPerchPos += distance * -Math.sin(angle);
         zPerchPos += distance * Math.cos(angle);
      }

      return new Vec3d(xPerchPos, this.entity.posY + (double)this.entity.height * 0.78D, zPerchPos);
   }

   public void updatedPerchedByEntity() {
      Entity perchedByEntity = this.getPerchedByEntity();
      if (perchedByEntity != null) {
         Vec3d perchPosition = this.getPerchPosition();
         perchedByEntity.setPosition(perchPosition.x, perchPosition.y, perchPosition.z);
         perchedByEntity.motionX = this.entity.motionX;
         perchedByEntity.motionY = this.entity.motionY;
         perchedByEntity.motionZ = this.entity.motionZ;
         perchedByEntity.rotationYaw = this.entity.rotationYaw;
         perchedByEntity.noClip = true;
      }

   }

   public int getProjectileCooldown(int type, String projectileName) {
      if (type == 1) {
         return !this.projectileCooldownsPrimary.containsKey(projectileName) ? 0 : ((Integer)this.projectileCooldownsPrimary.get(projectileName)).intValue();
      } else {
         return !this.projectileCooldownsSecondary.containsKey(projectileName) ? 0 : ((Integer)this.projectileCooldownsSecondary.get(projectileName)).intValue();
      }
   }

   public void setProjectileCooldown(int type, String projectileName, int cooldown) {
      if (type == 1) {
         this.projectileCooldownsPrimary.put(projectileName, Integer.valueOf(cooldown));
      }

      this.projectileCooldownsSecondary.put(projectileName, Integer.valueOf(cooldown));
   }

   public void readNBT(NBTTagCompound nbtTagCompound) {
   }

   public void writeNBT(NBTTagCompound nbtTagCompound) {
   }
}
