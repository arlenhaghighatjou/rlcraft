package xzeroair.trinkets.entity;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xzeroair.trinkets.attributes.JumpAttribute;
import xzeroair.trinkets.attributes.UpdatingAttribute;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.RayTraceHelper;

public class AlphaWolf extends EntityWolf {
   private NBTTagCompound storedWolf;

   public AlphaWolf(World world) {
      super(world);
      this.setScaleForAge(false);
      this.setSize(0.6F, 1.2F);
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      this.tasks.taskEntries.clear();
   }

   public void onUpdate() {
      super.onUpdate();
      boolean canRide = TrinketsConfig.SERVER.races.goblin.rider;
      if (!canRide) {
         this.setDead();
      }

      if (this.isEntityAlive() && !this.world.isRemote) {
         if (!this.getPassengers().isEmpty()) {
            Entity rider = this.getControllingPassenger();
            if (rider instanceof EntityLivingBase) {
               EntityLivingBase driver = (EntityLivingBase)rider;
               if (!driver.isPotionActive(MobEffects.STRENGTH)) {
                  driver.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, 0, false, false));
               }

               if (!driver.isPotionActive(MobEffects.REGENERATION)) {
                  driver.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0, false, false));
               }

               if (!this.isPotionActive(MobEffects.REGENERATION)) {
                  this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 1, false, false));
               }

               if (driver.isPotionActive(MobEffects.WATER_BREATHING)) {
                  this.addPotionEffect(driver.getActivePotionEffect(MobEffects.WATER_BREATHING));
               }

               if (driver.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
                  this.addPotionEffect(driver.getActivePotionEffect(MobEffects.FIRE_RESISTANCE));
               }

               if (driver.isPotionActive(MobEffects.INVISIBILITY)) {
                  this.addPotionEffect(driver.getActivePotionEffect(MobEffects.INVISIBILITY));
               }

               if (this.isInWater() && this.getAir() < driver.getAir()) {
                  this.setAir(driver.getAir());
               }
            }
         }

         boolean r = this.getPassengers().contains(this.getOwner());
         if (!r && this.ticksExisted > 1) {
            this.setDead();
         }

      }
   }

   public void setDead() {
      if (!this.world.isRemote) {
         try {
            NBTTagCompound old = this.getPreviousWolf();
            if (old != null) {
               Entity oldWolf = EntityList.createEntityFromNBT(old, this.world);
               if (oldWolf != null) {
                  oldWolf.setLocationAndAngles(this.posX, this.posY + 1.100000023841858D, this.posZ, this.rotationYaw, 0.0F);
                  if (this.world.spawnEntity(oldWolf)) {
                     this.storeOldWolf(new NBTTagCompound());
                  }
               }
            }
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

      super.setDead();
   }

   public boolean startRiding(Entity entityIn) {
      return super.startRiding(entityIn);
   }

   public boolean startRiding(Entity entityIn, boolean force) {
      return super.startRiding(entityIn, force);
   }

   protected boolean canBeRidden(Entity entityIn) {
      return super.canBeRidden(entityIn);
   }

   public void dismountEntity(Entity entityIn) {
      super.dismountEntity(entityIn);
   }

   public void dismountRidingEntity() {
      super.dismountRidingEntity();
   }

   protected void removePassenger(Entity passenger) {
      super.removePassenger(passenger);
   }

   public void removePassengers() {
      super.removePassengers();
   }

   public void setTamedBy(EntityLivingBase entity) {
      if (entity instanceof EntityPlayer) {
         super.setTamedBy((EntityPlayer)entity);
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(JumpAttribute.Jump).setBaseValue(0.7D);
      UpdatingAttribute atkDamage = (new UpdatingAttribute(EaglercraftUUID.fromString("76c436ad-d830-48ff-8b3c-fa3bcc1891c2"), SharedMonsterAttributes.ATTACK_DAMAGE)).setSavedInNBT(true);
      atkDamage.addModifier(this, 4.0D, 2);
   }

   public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner) {
      if (!(target instanceof EntityCreeper) && !(target instanceof EntityGhast)) {
         if (target instanceof EntityWolf) {
            EntityWolf entitywolf = (EntityWolf)target;
            if (entitywolf.isTamed() && entitywolf.getOwner() == owner) {
               return false;
            }
         }

         if (target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer)owner).canAttackPlayer((EntityPlayer)target)) {
            return false;
         } else {
            return !(target instanceof AbstractHorse) || !((AbstractHorse)target).isTame();
         }
      } else {
         return false;
      }
   }

   public void travel(float strafe, float vertical, float forward) {
      if (this.isBeingRidden() && this.canBeSteered()) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)this.getControllingPassenger();
         this.rotationYaw = entitylivingbase.rotationYaw;
         this.prevRotationYaw = this.rotationYaw;
         this.rotationPitch = entitylivingbase.rotationPitch * 0.5F;
         this.setRotation(this.rotationYaw, this.rotationPitch);
         this.renderYawOffset = this.rotationYaw;
         this.rotationYawHead = this.renderYawOffset;
         strafe = entitylivingbase.moveStrafing * 0.5F;
         forward = entitylivingbase.moveForward;
         if (forward <= 0.0F) {
            forward *= 0.25F;
         }

         this.setSprinting(entitylivingbase.isSprinting());
         if (this.isJumping() && this.onGround) {
            this.jump();
         }

         this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
         if (this.canPassengerSteer()) {
            this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
            super.travel(strafe, vertical, forward);
         } else if (entitylivingbase instanceof EntityPlayer) {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
         }

         if (this.onGround) {
            this.setJumping(false);
         }

         this.prevLimbSwingAmount = this.limbSwingAmount;
         double d1 = this.posX - this.prevPosX;
         double d0 = this.posZ - this.prevPosZ;
         float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
         if (f2 > 1.0F) {
            f2 = 1.0F;
         }

         this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
         this.limbSwing += this.limbSwingAmount;
      } else {
         this.jumpMovementFactor = 0.02F;
         super.travel(strafe, vertical, forward);
      }

   }

   public void MountedAttack(EntityPlayer player, double maxDist) {
      Vec3d pos1 = this.getPositionEyes(1.0F);
      Vec3d lookVec = player.getLookVec();
      Vec3d targetLoc = pos1.addVector(lookVec.x * maxDist, lookVec.y * maxDist, lookVec.z * maxDist);
      double d0 = targetLoc.x - this.posX;
      double d1 = targetLoc.z - this.posZ;
      float f = MathHelper.sqrt(d0 * d0 + d1 * d1);
      double m1 = 0.4D;
      double m2 = 0.6D;
      double multiplier = 4.0D;
      if ((double)f >= 1.0E-4D) {
         double f1 = d0 / (double)f * 4.0D * 0.4D + this.motionX * 0.6D;
         double f2 = d1 / (double)f * 4.0D * 0.4D + this.motionZ * 0.6D;
         this.motionX += f1;
         this.motionZ += f2;
      }

      this.motionY = 0.42D;
      this.swingArm(EnumHand.MAIN_HAND);
      RayTraceHelper.Beam beam = new RayTraceHelper.Beam(player.world, player, maxDist, 1.0D, true);
      RayTraceHelper.rayTraceEntity(beam, (target) -> {
         if (target instanceof EntityLivingBase && target != this) {
            this.attackEntityAsMob(target);
            return true;
         } else {
            return false;
         }
      });
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      Entity entity = source.getTrueSource();
      return this.isBeingRidden() && entity != null && this.isRidingOrBeingRiddenBy(entity) ? false : super.attackEntityFrom(source, amount);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      return super.attackEntityAsMob(entityIn);
   }

   public boolean isJumping() {
      return this.isJumping;
   }

   protected float getJumpUpwardsMotion() {
      float jumpmotion = (float)this.getEntityAttribute(JumpAttribute.Jump).getAttributeValue();
      return jumpmotion;
   }

   public boolean canBeSteered() {
      return this.getControllingPassenger() instanceof EntityLivingBase;
   }

   public Entity getControllingPassenger() {
      return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
   }

   public void updatePassenger(Entity passenger) {
      super.updatePassenger(passenger);
   }

   public boolean shouldDismountInWater(Entity rider) {
      return false;
   }

   public double getMountedYOffset() {
      return (double)this.height;
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected boolean canDropLoot() {
      return false;
   }

   public boolean canMateWith(EntityAnimal otherAnimal) {
      return false;
   }

   public boolean isChild() {
      return false;
   }

   public void storeOldWolf(EntityWolf wolf) {
      NBTTagCompound tag = new NBTTagCompound();
      wolf.writeToNBT(tag);
      this.storedWolf = tag;
   }

   public void storeOldWolf(NBTTagCompound tag) {
      this.storedWolf = tag;
   }

   public NBTTagCompound getPreviousWolf() {
      if (this.storedWolf == null) {
         this.storedWolf = new NBTTagCompound();
      }

      return this.storedWolf;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setTag("xat.wolf.stored", this.storedWolf);
      compound.setBoolean("xat:summoned", true);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("xat.wolf.stored")) {
         this.storedWolf = (NBTTagCompound)compound.getTag("xat.wolf.stored");
      }

   }
}
