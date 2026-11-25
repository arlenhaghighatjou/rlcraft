package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityBomb extends Entity {
   private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityBomb.class, DataSerializers.VARINT);
   @Nullable
   private EntityParasiteBase tntPlacedBy;
   private int fuse;
   private float str;
   private float damage;
   private boolean grief;
   private int rangeRad;
   private static final DataParameter<Byte> SKIN = EntityDataManager.createKey(EntityBomb.class, DataSerializers.BYTE);

   public EntityBomb(World worldIn) {
      super(worldIn);
      this.fuse = 80;
      this.str = 4.0F;
      this.preventEntitySpawning = true;
      this.isImmuneToFire = true;
      this.setSize(0.68F, 0.68F);
   }

   public EntityBomb(World worldIn, EntityParasiteBase igniter, boolean canGrief) {
      this(worldIn);
      this.tntPlacedBy = igniter;
      this.setPosition(igniter.posX, igniter.posY + (double)igniter.getEyeHeight() - 0.10000000149011612D, igniter.posZ);
      this.grief = canGrief;
   }

   public EntityBomb(World worldIn, double x, double y, double z, EntityParasiteBase igniter, float stren) {
      this(worldIn);
      this.setPosition(x, y, z);
      float f = (float)(Math.random() * 6.283185307179586D);
      this.motionX = (double)(-((float)Math.sin((double)f)) * 0.02F);
      this.motionY = 0.20000000298023224D;
      this.motionZ = (double)(-((float)Math.cos((double)f)) * 0.02F);
      this.setFuse(80);
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
      this.tntPlacedBy = igniter;
      this.str = stren;
   }

   protected void entityInit() {
      this.dataManager.register(FUSE, Integer.valueOf(80));
      this.dataManager.register(SKIN, Byte.valueOf((byte)0));
   }

   public void setInWeb() {
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (!this.hasNoGravity()) {
         this.motionY -= 0.03999999910593033D;
      }

      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863D;
      this.motionY *= 0.9800000190734863D;
      this.motionZ *= 0.9800000190734863D;
      if (this.onGround) {
         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
         this.motionY *= -0.5D;
      }

      --this.fuse;
      if (this.fuse <= 0) {
         this.explode();
      } else {
         this.handleWaterMovement();
      }

      this.onLivingUpdate();
   }

   public void onLivingUpdate() {
      this.collideWithNearbyEntities();
   }

   protected void collideWithNearbyEntities() {
      List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(), EntitySelectors.getTeamCollisionPredicate(this));
      if (!list.isEmpty()) {
         int i = this.world.getGameRules().getInt("maxEntityCramming");
         if (i > 0 && list.size() > i - 1 && this.rand.nextInt(4) == 0) {
            int j = 0;

            for(int k = 0; k < list.size(); ++k) {
               if (!((Entity)list.get(k)).isRiding()) {
                  ++j;
               }
            }

            if (j > i - 1) {
               this.attackEntityFrom(DamageSource.CRAMMING, 6.0F);
            }
         }

         for(int l = 0; l < list.size(); ++l) {
            Entity entity = (Entity)list.get(l);
            this.collideWithEntity(entity);
         }
      }

   }

   protected void collideWithEntity(Entity entityIn) {
      entityIn.applyEntityCollision(this);
   }

   public void onEntityUpdate() {
      super.onEntityUpdate();
   }

   private void explode() {
      if (this.str > 0.0F) {
         boolean flag = ForgeEventFactory.getMobGriefingEvent(this.world, this) && this.grief;
         ParasiteEventEntity.createExplosion(this.world, this, this.posX, this.posY, this.posZ, this.str, flag);
      }

      if (!this.world.isRemote) {
         this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.5F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
         float f = 4.0F;
         if (this.tntPlacedBy != null) {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow((double)this.rangeRad);

            for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
               if (!(mob instanceof EntityParasiteBase) && mob.canEntityBeSeen(this)) {
                  mob.attackEntityFrom(DamageSource.causeThrownDamage(this, this.tntPlacedBy), this.damage);
                  SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 300, 0);
                  if (this.tntPlacedBy.isEntityAlive()) {
                     this.tntPlacedBy.attackEntityAsMobMinimum(mob);
                  }
               }
            }
         }

         if (this.getSkin() == 2) {
            ParasiteEventEntity.spawnFromList(this, SRPConfigMobs.jinjoMobs, (EntityLivingBase)null);
         }

         EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(this.world, this.posX, this.posY, this.posZ);
         entityareaeffectcloud.setRadius((float)this.rangeRad, 1.5F);
         entityareaeffectcloud.setWaitTime(5);
         entityareaeffectcloud.setDuration(60);
         entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.POISON, 300, 0));
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.VIRA_E, 3600, 0, false, false));
         this.world.spawnEntity(entityareaeffectcloud);
         this.setDead();
      }
   }

   public void setMotion(double xSpeedIn, double ySpeedIn, double zSpeedIn, double capX, double capY) {
      xSpeedIn = Math.min(xSpeedIn, capX);
      ySpeedIn = Math.min(ySpeedIn, capY);
      zSpeedIn = Math.min(zSpeedIn, capX);
      this.motionX = xSpeedIn * (Math.random() * 2.0D - 1.0D);
      this.motionY = ySpeedIn;
      this.motionZ = zSpeedIn * (Math.random() * 2.0D - 1.0D);
   }

   public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
      float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
      float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
      float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
      this.shootTwo((double)f, (double)f1, (double)f2, velocity, inaccuracy);
      this.motionX += entityThrower.motionX;
      this.motionZ += entityThrower.motionZ;
      if (!entityThrower.onGround) {
         this.motionY += entityThrower.motionY;
      }

   }

   public void shootTwo(double x, double y, double z, float velocity, float inaccuracy) {
      float f = MathHelper.sqrt(x * x + y * y + z * z);
      x = x / (double)f;
      y = y / (double)f;
      z = z / (double)f;
      x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      x = x * (double)velocity;
      y = y * (double)velocity;
      z = z * (double)velocity;
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      float f1 = MathHelper.sqrt(x * x + z * z);
      this.rotationYaw = (float)(MathHelper.atan2(x, z) * 57.29577951308232D);
      this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * 57.29577951308232D);
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      compound.setShort("Fuse", (short)this.getFuse());
      compound.setInteger("parasitetype", this.getSkin());
      compound.setFloat("stren", this.str);
      compound.setBoolean("cangrief", this.grief);
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      this.setFuse(compound.getShort("Fuse"));
      if (compound.hasKey("parasitetype", 99)) {
         this.setSkin(compound.getInteger("parasitetype"));
      }

      if (compound.hasKey("stren", 99)) {
         this.str = compound.getFloat("stren");
      }

      if (compound.hasKey("cangrief", 99)) {
         this.grief = compound.getBoolean("cangrief");
      }

   }

   @Nullable
   public EntityLivingBase getTntPlacedBy() {
      return this.tntPlacedBy;
   }

   public float getEyeHeight() {
      return 0.0F;
   }

   public void setFuse(int fuseIn) {
      this.dataManager.set(FUSE, Integer.valueOf(fuseIn));
      this.fuse = fuseIn;
   }

   public void setStren(float in) {
      this.str = in;
   }

   public void updateSTR() {
      this.world.setEntityState(this, (byte)((int)this.str));
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (FUSE.equals(key)) {
         this.fuse = this.getFuseDataManager();
      }

   }

   public int getFuseDataManager() {
      return ((Integer)this.dataManager.get(FUSE)).intValue();
   }

   public int getFuse() {
      return this.fuse;
   }

   public byte getSkin() {
      return ((Byte)this.dataManager.get(SKIN)).byteValue();
   }

   public void setSkin(int texture) {
      this.dataManager.set(SKIN, Byte.valueOf((byte)texture));
   }

   public void setDamage(float in, int radius) {
      this.damage = in;
      this.rangeRad = radius;
   }

   public void handleStatusUpdate(byte id) {
      this.str = (float)id;
      if (id >= 2) {
         this.grief = true;
      }

   }
}
