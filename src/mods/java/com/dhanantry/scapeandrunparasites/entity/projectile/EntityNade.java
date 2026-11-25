package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityNade extends Entity {
   public float prevRenderYawOffset;
   public float renderYawOffset;
   public float prevRotationYawHead;
   public float rotationYawHead;
   public float prevLimbSwingAmount;
   public float limbSwingAmount;
   public float limbSwing;
   public int hurtTime;
   public int deathTime;
   protected int lastActiveTime;
   protected int timeSinceIgnited;
   protected int timerDDD;
   EntityPMalleable father;
   private double poosX;
   private double poosY;
   private double poosZ;
   private static final DataParameter<Integer> SELFE = EntityDataManager.createKey(EntityNade.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityNade.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> WAITSTART = EntityDataManager.createKey(EntityNade.class, DataSerializers.VARINT);

   public EntityNade(World worldIn) {
      super(worldIn);
      this.setSize(0.5F, 0.5F);
      this.ignoreFrustumCheck = true;
      this.isImmuneToFire = true;
      this.lastActiveTime = 0;
      this.timeSinceIgnited = 0;
      this.setFuseState(3);
      this.setStartState(10);
   }

   public EntityNade(World worldIn, int fuse, int waitStart) {
      this(worldIn);
      this.setFuseState(fuse);
      this.setStartState(waitStart);
   }

   protected void entityInit() {
      this.dataManager.register(SELFE, Integer.valueOf(-1));
      this.dataManager.register(FUSE, Integer.valueOf(-1));
      this.dataManager.register(WAITSTART, Integer.valueOf(-1));
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.ticksExisted > 3) {
         this.setSelfeState(1);
         this.dyingBurst(true, 1);
         if (this.world.isRemote) {
            for(int i = 0; i < 20; ++i) {
               this.spawnParticles(EnumParticleTypes.SMOKE_NORMAL);
            }

            for(int i = 0; i < 10; ++i) {
               this.spawnParticles(EnumParticleTypes.SMOKE_LARGE);
            }

            return;
         }

         this.posX = this.poosX;
         if (this.onGround) {
            this.posY += this.rand.nextDouble() * 0.01D;
         }

         this.posZ = this.poosZ;
      } else {
         if (this.world.isRemote) {
            for(int i = 0; i < 10; ++i) {
               this.spawnParticles(EnumParticleTypes.SMOKE_NORMAL);
            }

            for(int i = 0; i < 5; ++i) {
               this.spawnParticles(EnumParticleTypes.SMOKE_LARGE);
            }

            return;
         }

         this.poosX = this.posX;
         this.poosZ = this.posZ;
         if (this.ticksExisted == 2) {
            this.playSound(SRPSounds.NADE_S, 1.0F, 1.0F);
         }
      }

   }

   public int getStartState() {
      return ((Integer)this.dataManager.get(WAITSTART)).intValue();
   }

   public void setStartState(int state) {
      this.dataManager.set(WAITSTART, Integer.valueOf(state));
   }

   public int getFuseState() {
      return ((Integer)this.dataManager.get(FUSE)).intValue();
   }

   public void setFuseState(int state) {
      this.dataManager.set(FUSE, Integer.valueOf(state));
   }

   public int getSelfeState() {
      return ((Integer)this.dataManager.get(SELFE)).intValue();
   }

   public void setSelfeState(int state) {
      this.dataManager.set(SELFE, Integer.valueOf(state));
   }

   protected void dyingBurst(boolean fromDeath, int value) {
      int i = this.getSelfeState();
      if (i > 0 && this.timeSinceIgnited == 0) {
         ;
      }

      this.timeSinceIgnited += i * value;
      if (this.timeSinceIgnited < 0) {
         this.timeSinceIgnited = 0;
      }

      if (this.timeSinceIgnited >= this.getFuseState()) {
         this.timeSinceIgnited = this.getFuseState();
         this.selfExplode();
         this.prevPosX = this.posX;
         if (this.onGround) {
            this.prevPosY = this.posY;
         }

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
      } else {
         this.setSize(this.width + 0.8F, this.height + 0.32F);
      }

   }

   protected void selfExplode() {
      this.setSelfeState(2);
      if (this.getSelfeState() == 2) {
         ++this.timerDDD;
         if (!this.world.isRemote) {
            if (this.father != null && this.father.isEntityAlive()) {
               float f = this.width / 2.0F;
               float f1 = this.height;
               AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.posX - (double)f, this.posY, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f);

               for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
                  if (!(mob instanceof EntityParasiteBase)) {
                     mob.attackEntityFrom(DamageSource.GENERIC, (float)this.father.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
                     this.father.attackEntityAsMobMinimum(mob);
                  }
               }
            }
         } else {
            int par = this.getFuseState();
            par = par + par / 2;
         }

         if (this.timerDDD > this.getStartState()) {
            this.setDead();
         }
      }

   }

   public void applyEntityCollision(Entity entityIn) {
      if (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).capabilities.isCreativeMode) {
         ;
      }
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   }

   public void setFatherS(EntityPMalleable in) {
      this.father = in;
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
   }

   @SideOnly(Side.CLIENT)
   public void spawnParticles(EnumParticleTypes particleType) {
      double d0 = this.rand.nextGaussian() * 0.02D;
      double d1 = this.rand.nextGaussian() * 0.02D;
      double d2 = this.rand.nextGaussian() * 0.02D;
      this.world.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
   }

   @SideOnly(Side.CLIENT)
   public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
      double d0 = this.rand.nextGaussian() * 0.02D;
      double d1 = this.rand.nextGaussian() * 0.02D;
      double d2 = this.rand.nextGaussian() * 0.02D;
      ParticleSpawner.spawnParticle(particleType, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, r, g, b);
   }

   @SideOnly(Side.CLIENT)
   public void spawnOrbEffects(int cap1) {
      for(int i = -cap1; i <= cap1; ++i) {
         for(int j = -cap1; j <= cap1; ++j) {
            if (i > -2 && i < 2 && j == -1) {
               j = 2;
            }

            if (this.rand.nextInt(16) == 0) {
               for(int k = 0; k <= 5; ++k) {
                  ParticleSpawner.spawnParticle(SRPEnumParticle.EEN, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D, this.posY + this.rand.nextDouble() * 2.0D * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D, (double)((float)i + this.rand.nextFloat()) - 0.5D, (double)((float)k - this.rand.nextFloat() - 1.0F), (double)((float)j + this.rand.nextFloat()) - 0.5D, 0, 0, 0);
               }
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public float getSelfeFlashIntensity(float p_70831_1_) {
      return ((float)this.lastActiveTime + (float)(this.timeSinceIgnited - this.lastActiveTime) * p_70831_1_ * 5.0F) / (float)(this.getFuseState() - 2);
   }
}
