package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.inventory.InventoryCreature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FearEntity extends BaseCreatureEntity {
   public Entity fearedEntity;

   public FearEntity(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasStepSound = false;
      this.hasAttackSound = false;
      this.spreadFire = false;
      this.setSize(0.8F, 1.8F);
      this.setupMob();
   }

   public FearEntity(World world, Entity feared) {
      this(world);
      this.setFearedEntity(feared);
   }

   public void setupMob() {
      this.experienceValue = 0;
      this.inventory = new InventoryCreature(this.getName(), this);
      this.isImmuneToFire = true;
   }

   public void loadItemDrops() {
   }

   protected boolean canDespawn() {
      return false;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (this.fearedEntity != null && this.fearedEntity.isEntityAlive() && this.fearedEntity instanceof EntityLivingBase) {
            EntityLivingBase fearedEntityLiving = (EntityLivingBase)this.fearedEntity;
            if (this.canPickupEntity(fearedEntityLiving)) {
               this.pickupEntity(fearedEntityLiving);
            }

            if (this.hasPickupEntity() && !(this.getPickupEntity() instanceof EntityPlayer)) {
               this.getPickupEntity().rotationYaw = this.rotationYaw;
               this.getPickupEntity().rotationPitch = this.rotationPitch;
            }

            if (this.getPickupEntity() == null) {
               this.setPosition(this.fearedEntity.posX, this.fearedEntity.posY, this.fearedEntity.posZ);
               this.motionX = this.fearedEntity.motionX;
               this.motionY = this.fearedEntity.motionY;
               this.motionZ = this.fearedEntity.motionZ;
               this.fallDistance = 0.0F;
            }

            if (ObjectManager.getEffect("fear") != null && fearedEntityLiving.isPotionActive(ObjectManager.getEffect("fear"))) {
               if (this.fearedEntity instanceof EntityLivingBase) {
                  if (fearedEntityLiving.isPotionActive(MobEffects.LEVITATION)) {
                     PotionEffect activeDebuff = fearedEntityLiving.getActivePotionEffect(MobEffects.LEVITATION);
                     this.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, activeDebuff.getDuration(), activeDebuff.getAmplifier()));
                  }

                  Potion instability = ObjectManager.getEffect("instability");
                  if (instability != null && fearedEntityLiving.isPotionActive(instability)) {
                     PotionEffect activeDebuff = fearedEntityLiving.getActivePotionEffect(instability);
                     this.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, activeDebuff.getDuration(), activeDebuff.getAmplifier()));
                  }
               }

            } else {
               this.setDead();
            }
         } else {
            this.setDead();
         }
      }
   }

   public boolean rollWanderChance() {
      return true;
   }

   public void setFearedEntity(Entity feared) {
      this.fearedEntity = feared;
      this.setSize(feared.width, feared.height);
      this.noClip = feared.noClip;
      this.stepHeight = feared.stepHeight;
      this.setLocationAndAngles(feared.posX, feared.posY, feared.posZ, feared.rotationYaw, feared.rotationPitch);
      if (feared instanceof EntityLivingBase && !(feared instanceof EntityPlayer)) {
         EntityLivingBase fearedEntityLiving = (EntityLivingBase)feared;
         this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(fearedEntityLiving.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue());
      }

   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return false;
   }

   public boolean isPotionApplicable(PotionEffect par1PotionEffect) {
      return false;
   }

   public boolean isFlying() {
      if (this.pickupEntity != null) {
         if (this.pickupEntity instanceof BaseCreatureEntity) {
            return ((BaseCreatureEntity)this.pickupEntity).isFlying();
         }

         if (this.pickupEntity instanceof EntityFlying) {
            return true;
         }

         if (this.pickupEntity instanceof EntityPlayer) {
            return ((EntityPlayer)this.pickupEntity).capabilities.isCreativeMode;
         }
      }

      return false;
   }

   public boolean isInvisible() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public boolean isInvisibleToPlayer(EntityPlayer player) {
      return true;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return AssetManager.getSound("effect_fear");
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return AssetManager.getSound("effect_fear");
   }

   protected SoundEvent getDeathSound() {
      return AssetManager.getSound("effect_fear");
   }

   public void playFlySound() {
   }
}
