package com.oblivioussp.spartanweaponry.entity.projectile;

import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import com.oblivioussp.spartanweaponry.item.ItemBolt;
import com.oblivioussp.spartanweaponry.util.EntityDamageSourceIndirectArmorPiercing;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityBolt extends EntityArrow implements IThrowableEntity {
   protected int knockbackStrength = 0;

   public EntityBolt(World worldIn) {
      super(worldIn);
   }

   public EntityBolt(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   public EntityBolt(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
   }

   public EntityBolt.BoltType getBoltType() {
      return EntityBolt.BoltType.STANDARD;
   }

   public void shoot(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy) {
      super.shoot(shooter, pitch, yaw, p_184547_4_, velocity * this.getRangeMultiplier(), inaccuracy);
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      super.shoot(x, y, z, velocity * this.getRangeMultiplier(), inaccuracy);
   }

   protected DamageSource causePiercingBoltDamage(EntityBolt bolt, Entity shooter) {
      return new EntityDamageSourceIndirectArmorPiercing("arrow", bolt, shooter, this.getArmorPiercingFactor());
   }

   protected void onHit(RayTraceResult raytraceResultIn) {
      Entity entity = raytraceResultIn.entityHit;
      if (entity != null) {
         float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
         int i = MathHelper.ceil((double)f * this.getDamage());
         DamageSource damagesource;
         if (this.shootingEntity == null) {
            damagesource = this.causePiercingBoltDamage(this, this);
         } else {
            damagesource = this.causePiercingBoltDamage(this, this.shootingEntity);
         }

         if (this.isBurning() && !(entity instanceof EntityEnderman)) {
            entity.setFire(5);
         }

         if (entity.attackEntityFrom(damagesource, (float)i)) {
            if (entity instanceof EntityLivingBase) {
               EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
               if (!this.world.isRemote) {
                  entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
               }

               if (this.knockbackStrength > 0) {
                  float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                  if (f1 > 0.0F) {
                     entitylivingbase.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1, 0.1D, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1);
                  }
               }

               if (this.shootingEntity instanceof EntityLivingBase) {
                  EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity);
                  EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.shootingEntity, entitylivingbase);
               }

               this.arrowHit(entitylivingbase);
               if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
               }
            }

            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            if (!(entity instanceof EntityEnderman)) {
               this.setDead();
            }
         } else {
            this.motionX *= -0.10000000149011612D;
            this.motionY *= -0.10000000149011612D;
            this.motionZ *= -0.10000000149011612D;
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            this.ticksInAir = 0;
            if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D) {
               if (this.pickupStatus == PickupStatus.ALLOWED) {
                  this.entityDropItem(this.getArrowStack(), 0.1F);
               }

               this.setDead();
            }
         }
      } else {
         super.onHit(raytraceResultIn);
      }

   }

   public void setKnockbackStrength(int knockback) {
      this.knockbackStrength = knockback;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemRegistrySW.bolt);
   }

   protected ItemBolt getBoltItem() {
      return ItemRegistrySW.bolt;
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }

   public float getRangeMultiplier() {
      return this.getBoltItem().getRangeMultiplier();
   }

   public float getArmorPiercingFactor() {
      return this.getBoltItem().getArmorPiercingFactor();
   }

   public static enum BoltType {
      STANDARD,
      DIAMOND,
      SPECTRAL;

      private BoltType() {
      }
   }
}
