package com.dhanantry.scapeandrunparasites.entity.monster;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBiomass extends EntityParasiteBase {
   private int fuse;
   private EntityParasiteBase entityin;
   private EntityLivingBase target;
   private String parasite;
   private int point;
   private static final DataParameter<Float> STAGE = EntityDataManager.createKey(EntityBiomass.class, DataSerializers.FLOAT);
   private boolean payfather;
   private float currentWidth;
   private float currentHeight;
   private float capWidth;
   private float capHeight;
   private float growWidth;
   private float growHeight;

   public EntityBiomass(World worldIn) {
      super(worldIn);
      this.setSize(0.98F, 0.98F);
      this.tasks.removeTask(this.folow);
      this.tasks.removeTask(this.aiWander);
      this.fuse = 777;
      this.preventEntitySpawning = true;
      this.type = 100;
      this.currentWidth = 0.98F;
      this.currentHeight = 0.98F;
      this.capWidth = 0.98F;
      this.capHeight = 0.98F;
   }

   public EntityBiomass(World worldIn, double x, double y, double z) {
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
   }

   public EntityBiomass(World worldIn, EntityParasiteBase fatherEntity, float stage, EntityLivingBase target, boolean payfather) {
      this(worldIn);
      this.entityin = fatherEntity;
      this.setStage(stage);
      this.target = target;
      this.payfather = payfather;
   }

   public EntityBiomass(World worldIn, EntityParasiteBase entityin, EntityLivingBase target) {
      this(worldIn);
      this.entityin = entityin;
      this.target = target;
      this.setStage(1.0F);
      this.payfather = true;
   }

   public int getParasiteIDRegister() {
      return 205;
   }

   public void fall(float distance, float damageMultiplier) {
      if (distance >= 18.0F) {
         super.fall(distance, damageMultiplier);
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(0.0D);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(STAGE, Float.valueOf(0.0F));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote) {
         if (this.isDead || this.getHealth() <= 0.0F) {
            return;
         }

         if (this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 0.1F);
         }
      }

      if (this.world.isRemote) {
         switch((int)this.getStage()) {
         case 1:
            if (this.ticksExisted % 10 == 0) {
               for(int i = 0; i <= 1; ++i) {
                  this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
               }
            }
            break;
         case 2:
            if (this.ticksExisted % 5 == 0) {
               for(int i = 0; i <= 1; ++i) {
                  this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
               }
            }
            break;
         case 3:
            if (this.ticksExisted % 3 == 0) {
               for(int i = 0; i <= 2; ++i) {
                  this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
               }
            }
            break;
         case 4:
            if (this.ticksExisted % 10 == 0) {
               for(int i = 0; i <= 1; ++i) {
                  this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
               }
            }
            break;
         case 5:
            if (this.ticksExisted % 10 == 0) {
               for(int i = 0; i <= 1; ++i) {
                  this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
               }
            }
            break;
         case 6:
            if (this.ticksExisted % 5 == 0) {
               for(int i = 0; i <= 1; ++i) {
                  this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0);
               }
            }
         }
      } else if (this.ticksExisted % 10 == 0) {
         this.applyCOTH(2);
      }

      if (this.onGround) {
         --this.fuse;
      }

      if (this.ticksExisted >= 200) {
         --this.fuse;
      }

      if (this.fuse <= 0) {
         this.particleStatus((byte)7);
         this.setDead();
         if (!this.world.isRemote) {
            this.explode();
            this.world.setEntityState(this, (byte)18);
         }
      } else if (this.onGround) {
         switch((int)this.getStage()) {
         case 1:
            this.growWidth += 0.005F;
            this.growHeight += 0.006F;
            this.world.setEntityState(this, (byte)11);
            break;
         case 2:
            this.growWidth += 0.007F;
            this.growHeight += 0.009F;
            this.world.setEntityState(this, (byte)12);
            break;
         case 3:
            this.growWidth += 0.015F;
            this.growHeight += 0.02F;
            this.world.setEntityState(this, (byte)13);
            break;
         case 4:
            this.growWidth += 0.007F;
            this.growHeight += 0.009F;
            this.world.setEntityState(this, (byte)15);
            break;
         case 5:
            this.growWidth += 0.005F;
            this.growHeight += 0.006F;
            this.world.setEntityState(this, (byte)16);
            break;
         case 6:
            this.growWidth += 0.007F;
            this.growHeight += 0.009F;
            this.world.setEntityState(this, (byte)17);
         }
      }

   }

   public void applyCOTH(int r) {
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow((double)r);

      for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
         if (mob != this && !(mob instanceof EntityParasiteBase)) {
            mob.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 200, 1, false, false));
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public float getGrowW() {
      return this.growWidth;
   }

   @SideOnly(Side.CLIENT)
   public float getGrowHeight() {
      return this.growHeight;
   }

   private boolean explode() {
      this.applyCOTH(2);
      this.playSound(SRPSounds.FLESH_PRIMITIVE, 1.0F, 1.0F);
      if (this.fuse == 777) {
         return false;
      } else if (this.entityin == null) {
         return this.explode2();
      } else {
         EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName(new ResourceLocation(this.parasite), this.world);
         if (entityout == null) {
            return false;
         } else {
            entityout.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue((double)(16.0F + (this.getStage() - 1.0F) * 8.0F));
            entityout.setLocationAndAngles(this.posX, this.posY, this.posZ, this.entityin.rotationYaw, this.entityin.rotationPitch);
            entityout.onInitialSpawn(this.entityin.world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
            if (SRPConfigSystems.rageEnable) {
               entityout.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 1200, 1, false, false));
            }

            entityout.addPotionEffect(new PotionEffect(SRPPotions.DEBAR_E, 120000, 1, false, false));
            if (this.isBurning()) {
               entityout.setHealth(entityout.getMaxHealth() * 0.5F);
               entityout.setFire(8);
            }

            this.entityin.world.spawnEntity(entityout);
            if (this.target != null) {
               entityout.setAttackTarget(this.target);
            }

            if (this.payfather && this.entityin instanceof EntityCanSummon) {
               EntityCanSummon father = (EntityCanSummon)this.entityin;
               father.setActualParasites(this.point);
               father.addID(entityout.getEntityId(), this.point);
            }

            return true;
         }
      }
   }

   private boolean explode2() {
      if (this.fuse != 777 && this.parasite != null) {
         EntityLiving entityout = (EntityLiving)EntityList.createEntityByIDFromName(new ResourceLocation(this.parasite), this.world);
         if (entityout == null) {
            return false;
         } else {
            entityout.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue((double)(16.0F + (this.getStage() - 1.0F) * 8.0F));
            entityout.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            entityout.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
            if (SRPConfigSystems.rageEnable) {
               entityout.addPotionEffect(new PotionEffect(SRPPotions.RAGE_E, 1200, 1, false, false));
            }

            entityout.addPotionEffect(new PotionEffect(SRPPotions.DEBAR_E, 120000, 1, false, false));
            if (this.isBurning()) {
               entityout.setHealth(entityout.getMaxHealth() * 0.5F);
               entityout.setFire(8);
            }

            this.world.spawnEntity(entityout);
            if (this.target != null) {
               entityout.setAttackTarget(this.target);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @SideOnly(Side.CLIENT)
   private void explotionParticles() {
      double b = 0.0D;
      switch((int)this.getStage()) {
      case 2:
         b = 0.3D;
         break;
      case 3:
         b = 0.6D;
         break;
      case 4:
         b = 0.3D;
      case 5:
      default:
         break;
      case 6:
         b = 0.3D;
      }

      for(int i = 0; i <= 10; ++i) {
         double d0 = (double)((float)this.posX + this.rand.nextFloat());
         double d1 = (double)((float)this.posY + this.rand.nextFloat()) + 1.0D;
         double d2 = (double)((float)this.posZ + this.rand.nextFloat());
         double d3 = d0 - this.posX;
         double d4 = d1 - this.posY;
         double d5 = d2 - this.posZ;
         double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
         d3 = d3 / d6;
         d4 = d4 / d6;
         d5 = d5 / d6;
         double d7 = 0.5D / (d6 / 1.0D + 0.1D);
         d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.7F);
         d3 = d3 * d7 * b;
         d4 = d4 * d7 * b * 0.5D;
         d5 = d5 * d7 * b;
         this.spawnParticles(SRPEnumParticle.BIOMASS, 0, 0, 0, d0, d1, d2, d3, d4, d5);
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

   public void setSizeGrow(float width, float height, float capWidht, float capHeight) {
      this.currentWidth = width;
      this.currentHeight = height;
      this.capWidth = capWidht;
      this.capHeight = capHeight;
   }

   public void setFuse(int fuseIn) {
      this.fuse = fuseIn;
   }

   public void setParasite(String in, int points) {
      this.parasite = in;
      this.point = points;
   }

   public int getFuse() {
      return this.fuse;
   }

   public int getSkin() {
      return super.getSkin();
   }

   public void setSkin(int texture) {
      super.setSkin(texture);
   }

   public float getStage() {
      return ((Float)this.dataManager.get(STAGE)).floatValue();
   }

   public void setStage(float in) {
      this.dataManager.set(STAGE, Float.valueOf(in));
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setShort("Fuse", (short)this.getFuse());
      if (this.entityin != null) {
         compound.setInteger("parasitefather", this.entityin.getEntityId());
      }

      if (this.target != null) {
         compound.setInteger("parasitetarget", this.target.getEntityId());
      }

      if (this.parasite != null) {
         compound.setString("parasiteparasite", this.parasite);
      }

      compound.setInteger("parasitepoint", this.point);
      compound.setBoolean("parasitepayfather", this.payfather);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setFuse(compound.getShort("Fuse"));
      if (compound.hasKey("parasitefather", 99) && this.world.getEntityByID(compound.getInteger("parasitefather")) instanceof EntityParasiteBase) {
         this.entityin = (EntityParasiteBase)this.world.getEntityByID(compound.getInteger("parasitefather"));
      }

      if (compound.hasKey("parasitetarget", 99)) {
         this.target = (EntityLivingBase)this.world.getEntityByID(compound.getInteger("parasitetarget"));
      }

      if (compound.hasKey("parasiteparasite", 8)) {
         this.parasite = compound.getString("parasiteparasite").toString();
      }

      if (compound.hasKey("parasitepoint", 99)) {
         this.point = compound.getInteger("parasitepoint");
      }

      if (compound.hasKey("parasitepayfather", 99)) {
         this.payfather = compound.getBoolean("parasitepayfather");
      }

   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 11) {
         this.growWidth += 0.005F;
         this.growHeight += 0.006F;
      } else if (id == 12) {
         this.growWidth += 0.01F;
         this.growHeight += 0.012F;
      } else if (id == 13) {
         this.growWidth += 0.001F;
         this.growHeight += 0.001F;
      } else if (id == 15) {
         this.growWidth += 0.01F;
         this.growHeight += 0.012F;
      } else if (id == 16) {
         this.growWidth += 0.005F;
         this.growHeight += 0.006F;
      } else if (id == 17) {
         this.growWidth += 0.01F;
         this.growHeight += 0.012F;
      } else if (id == 18) {
         this.explotionParticles();
      } else {
         super.handleStatusUpdate(id);
      }

   }
}
