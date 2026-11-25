package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityScaryOrb extends Entity {
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
   private boolean followF;
   private double poosX;
   private double poosY;
   private double poosZ;
   private static final DataParameter<Integer> SELFE = EntityDataManager.createKey(EntityScaryOrb.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityScaryOrb.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> WAITSTART = EntityDataManager.createKey(EntityScaryOrb.class, DataSerializers.VARINT);

   public EntityScaryOrb(World worldIn) {
      super(worldIn);
      this.setSize(0.5F, 0.5F);
      this.ignoreFrustumCheck = true;
      this.isImmuneToFire = true;
      this.lastActiveTime = 0;
      this.timeSinceIgnited = 0;
      this.followF = true;
      this.setFuseState(7);
      this.setStartState(40);
   }

   public EntityScaryOrb(World worldIn, EntityPMalleable in, int fuse, int waitStart) {
      this(worldIn);
      this.father = in;
      this.prevRenderYawOffset = in.prevRenderYawOffset;
      this.renderYawOffset = in.renderYawOffset;
      this.prevRotationYawHead = in.prevRotationYawHead;
      this.rotationYawHead = in.rotationYawHead;
      this.prevLimbSwingAmount = in.prevLimbSwingAmount;
      this.limbSwingAmount = in.limbSwingAmount;
      this.limbSwing = in.limbSwing;
      this.setFuseState(fuse);
      this.setStartState(waitStart);
   }

   public EntityScaryOrb(World worldIn, EntityPMalleable in, int fuse, int waitStart, boolean stayPY) {
      this(worldIn, in, fuse, waitStart);
      this.followF = stayPY;
   }

   protected void entityInit() {
      this.dataManager.register(SELFE, Integer.valueOf(-1));
      this.dataManager.register(FUSE, Integer.valueOf(-1));
      this.dataManager.register(WAITSTART, Integer.valueOf(-1));
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.ticksExisted > this.getStartState()) {
         this.setSelfeState(1);
         this.dyingBurst(true, 1);
         if (this.world.isRemote) {
            if (this.father != null) {
               this.prevRenderYawOffset = this.father.prevRenderYawOffset;
               this.renderYawOffset = this.father.renderYawOffset;
               this.prevRotationYawHead = this.father.prevRotationYawHead;
               this.rotationYawHead = this.father.rotationYawHead;
            }

            this.spawnOrbEffects(4);
            return;
         }

         if (this.father != null) {
            if (this.father.isEntityAlive() && this.followF) {
               this.posX = this.father.posX;
               this.posY = this.father.posY - this.rand.nextDouble() * 0.1D;
               this.posZ = this.father.posZ;
            } else {
               this.posX = this.poosX;
               this.posY = this.poosY - this.rand.nextDouble() * 0.1D;
               this.posZ = this.poosZ;
               if (this.followF) {
                  this.setDead();
               }
            }
         } else {
            this.posX = this.poosX;
            this.posY = this.poosY - this.rand.nextDouble() * 0.1D;
            this.posZ = this.poosZ;
         }
      } else {
         if (this.world.isRemote) {
            this.spawnOrbEffects(4);
            return;
         }

         if (this.father != null) {
            if (this.father.isEntityAlive() && this.followF) {
               this.poosX = this.father.posX;
               this.poosY = this.father.posY;
               this.poosZ = this.father.posZ;
               this.posX = this.father.posX;
               this.posY = this.father.posY - this.rand.nextDouble() * 0.1D;
               this.posZ = this.father.posZ;
            } else {
               if (this.followF) {
                  this.setDead();
               }

               this.poosX = this.posX;
               this.poosY = this.posY;
               this.poosZ = this.posZ;
            }
         } else {
            this.poosX = this.posX;
            this.poosY = this.posY;
            this.poosZ = this.posZ;
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
      } else {
         this.setSize(this.width + 0.8F, this.height + 0.32F);
      }

   }

   protected void selfExplode() {
      this.setSelfeState(2);
      if (this.getSelfeState() == 2) {
         ++this.timerDDD;
         if (this.timerDDD > 15) {
            if (!this.world.isRemote) {
               if (this.father == null) {
                  float f = this.width / 2.0F;
                  float f1 = this.height;
                  AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.posX - (double)f, this.posY, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f);

                  for(EntityPlayer player : this.world.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb)) {
                     if (!player.capabilities.isCreativeMode) {
                        int ccc = 100;
                        CooldownTracker track = player.getCooldownTracker();

                        for(int i = 0; i < player.inventory.mainInventory.size(); ++i) {
                           if (track.getCooldown(((ItemStack)player.inventory.mainInventory.get(i)).getItem(), 1.0F) == 0.0F) {
                              track.setCooldown(((ItemStack)player.inventory.mainInventory.get(i)).getItem(), ccc);
                           }
                        }

                        if (track.getCooldown(player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem(), 1.0F) == 0.0F) {
                           track.setCooldown(player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem(), ccc);
                        }
                     }
                  }

                  this.setDead();
                  return;
               }

               float f = this.width / 2.0F;
               float f1 = this.height;
               AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.posX - (double)f, this.posY, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)f1, this.posZ + (double)f);
               List<EntityLivingBase> moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

               for(EntityLivingBase mob : moblist) {
                  this.father.scaryOrbEffect(mob, moblist.size());
               }
            } else {
               int par = this.getFuseState();
               par = par + par / 2;

               for(int i = 0; i <= par; ++i) {
                  this.spawnParticles(EnumParticleTypes.EXPLOSION_LARGE);
               }

               for(int i = 0; i <= par; ++i) {
                  ParticleSpawner.spawnParticle(SRPEnumParticle.EEN, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D, this.posY + this.rand.nextDouble() * 2.0D * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2.0D, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian(), 0, 0, 0);
               }
            }

            this.playSound(SRPSounds.ORB_E, 1.0F, 1.0F);
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
