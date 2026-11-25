package com.dhanantry.scapeandrunparasites.entity;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.PotionTypes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityToxicCloud extends Entity {
   private static final DataParameter<Float> RADIUS = EntityDataManager.createKey(EntityToxicCloud.class, DataSerializers.FLOAT);
   private static final DataParameter<Float> HEIG = EntityDataManager.createKey(EntityToxicCloud.class, DataSerializers.FLOAT);
   private static final DataParameter<Byte> PART = EntityDataManager.createKey(EntityToxicCloud.class, DataSerializers.BYTE);
   private static final DataParameter<Boolean> IGNORE_RADIUS = EntityDataManager.createKey(EntityToxicCloud.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> PARTICLE = EntityDataManager.createKey(EntityToxicCloud.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> PARTICLE_PARAM_1 = EntityDataManager.createKey(EntityToxicCloud.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> PARTICLE_PARAM_2 = EntityDataManager.createKey(EntityToxicCloud.class, DataSerializers.VARINT);
   private PotionType potion;
   private final List<PotionEffect> effects;
   private final Map<Entity, Integer> reapplicationDelayMap;
   private int duration;
   private int waitTime;
   private int reapplicationDelay;
   private float radiusOnUse;
   private float radiusPerTick;
   private EntityLivingBase owner;
   private EaglercraftUUID ownerUniqueId;

   public EntityToxicCloud(World worldIn) {
      super(worldIn);
      this.potion = PotionTypes.EMPTY;
      this.effects = Lists.newArrayList();
      this.reapplicationDelayMap = Maps.newHashMap();
      this.duration = 600;
      this.waitTime = 20;
      this.reapplicationDelay = 20;
      this.noClip = true;
      this.isImmuneToFire = true;
      this.setRadius(3.0F, 0.5F);
   }

   public EntityToxicCloud(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.setPosition(x, y, z);
   }

   protected void entityInit() {
      this.getDataManager().register(PART, Byte.valueOf((byte)0));
      this.getDataManager().register(RADIUS, Float.valueOf(0.5F));
      this.getDataManager().register(HEIG, Float.valueOf(0.5F));
      this.getDataManager().register(IGNORE_RADIUS, Boolean.valueOf(false));
      this.getDataManager().register(PARTICLE, Integer.valueOf(EnumParticleTypes.SPELL_MOB.getParticleID()));
      this.getDataManager().register(PARTICLE_PARAM_1, Integer.valueOf(0));
      this.getDataManager().register(PARTICLE_PARAM_2, Integer.valueOf(0));
   }

   public void setRadius(float radiusIn, float heightIn) {
      double d0 = this.posX;
      double d1 = this.posY;
      double d2 = this.posZ;
      this.setSize(radiusIn * 2.0F, heightIn);
      this.setPosition(d0, d1, d2);
      if (!this.world.isRemote) {
         this.getDataManager().set(RADIUS, Float.valueOf(radiusIn));
         this.getDataManager().set(HEIG, Float.valueOf(heightIn));
      }

   }

   public float getRadius() {
      return ((Float)this.getDataManager().get(RADIUS)).floatValue();
   }

   public float getHeight() {
      return ((Float)this.getDataManager().get(HEIG)).floatValue();
   }

   public void setPotion(PotionType potionIn) {
      this.potion = potionIn;
   }

   public void addEffect(PotionEffect effect) {
      this.effects.add(effect);
   }

   public byte getColor() {
      return ((Byte)this.getDataManager().get(PART)).byteValue();
   }

   public void setColor(int colorIn) {
      this.getDataManager().set(PART, Byte.valueOf((byte)colorIn));
   }

   public EnumParticleTypes getParticle() {
      return EnumParticleTypes.getParticleFromId(((Integer)this.getDataManager().get(PARTICLE)).intValue());
   }

   public void setParticle(EnumParticleTypes particleIn) {
      this.getDataManager().set(PARTICLE, Integer.valueOf(particleIn.getParticleID()));
   }

   public int getParticleParam1() {
      return ((Integer)this.getDataManager().get(PARTICLE_PARAM_1)).intValue();
   }

   public void setParticleParam1(int particleParam) {
      this.getDataManager().set(PARTICLE_PARAM_1, Integer.valueOf(particleParam));
   }

   public int getParticleParam2() {
      return ((Integer)this.getDataManager().get(PARTICLE_PARAM_2)).intValue();
   }

   public void setParticleParam2(int particleParam) {
      this.getDataManager().set(PARTICLE_PARAM_2, Integer.valueOf(particleParam));
   }

   protected void setIgnoreRadius(boolean ignoreRadius) {
      this.getDataManager().set(IGNORE_RADIUS, Boolean.valueOf(ignoreRadius));
   }

   public boolean shouldIgnoreRadius() {
      return ((Boolean)this.getDataManager().get(IGNORE_RADIUS)).booleanValue();
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int durationIn) {
      this.duration = durationIn;
   }

   public void onUpdate() {
      super.onUpdate();
      boolean flag = this.shouldIgnoreRadius();
      float f = this.getRadius();
      if (this.world.isRemote) {
         EnumParticleTypes enumparticletypes = this.getParticle();
         int[] aint = new int[enumparticletypes.getArgumentCount()];
         if (aint.length > 0) {
            aint[0] = this.getParticleParam1();
         }

         if (aint.length > 1) {
            aint[1] = this.getParticleParam2();
         }

         if (flag) {
            if (this.rand.nextInt(3) != 0) {
               for(int i = 0; i < 2; ++i) {
                  float f1 = this.rand.nextFloat() * 6.2831855F;
                  float f2 = MathHelper.sqrt(this.rand.nextFloat()) * 0.2F;
                  float f3 = MathHelper.cos(f1) * f2;
                  float f4 = MathHelper.sin(f1) * f2;
                  if (enumparticletypes == EnumParticleTypes.SPELL_MOB) {
                     int j = this.rand.nextBoolean() ? 16777215 : this.getColor();
                     int k = j >> 16 & 255;
                     int l = j >> 8 & 255;
                     int i1 = j & 255;
                     this.world.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), this.posX + (double)f3, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)f4, (double)((float)k / 255.0F), (double)((float)l / 255.0F), (double)((float)i1 / 255.0F), new int[0]);
                  } else {
                     this.world.spawnAlwaysVisibleParticle(enumparticletypes.getParticleID(), this.posX + (double)f3, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)f4, 0.0D, 0.0D, 0.0D, aint);
                  }
               }
            }
         } else {
            float f5 = 3.1415927F * f * f;

            for(int k1 = 0; (float)k1 < f5; ++k1) {
               if (this.rand.nextInt(3) == 0) {
                  float f6 = this.rand.nextFloat() * 6.2831855F;
                  float f7 = MathHelper.sqrt(this.rand.nextFloat()) * f;
                  float f8 = MathHelper.cos(f6) * f7;
                  float f9 = MathHelper.sin(f6) * f7;
                  if (enumparticletypes == EnumParticleTypes.SPELL_MOB) {
                     int l1 = this.getColor();
                     int i2 = l1 >> 16 & 255;
                     int j2 = l1 >> 8 & 255;
                     int j1 = l1 & 255;
                     this.world.spawnAlwaysVisibleParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), this.posX + (double)f8 * 2.0D, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)f9 * 2.0D, (double)((float)i2 / 255.0F), (double)((float)j2 / 255.0F), (double)((float)j1 / 255.0F), new int[0]);
                  } else {
                     this.world.spawnAlwaysVisibleParticle(enumparticletypes.getParticleID(), this.posX + (double)f8, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)f9, (0.5D - this.rand.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.rand.nextDouble()) * 0.15D, aint);
                  }
               }
            }
         }
      } else {
         if (this.ticksExisted >= this.waitTime + this.duration) {
            this.setDead();
            return;
         }

         boolean flag1 = this.ticksExisted < this.waitTime;
         if (flag != flag1) {
            this.setIgnoreRadius(flag1);
         }

         if (flag1) {
            return;
         }

         if (this.radiusPerTick != 0.0F) {
            f += this.radiusPerTick;
            if (f < 0.5F) {
               this.setDead();
               return;
            }

            this.setRadius(f, this.getHeight());
         }

         if (this.ticksExisted % 5 == 0) {
            Iterator<Entry<Entity, Integer>> iterator = this.reapplicationDelayMap.entrySet().iterator();

            while(iterator.hasNext()) {
               Entry<Entity, Integer> entry = (Entry)iterator.next();
               if (this.ticksExisted >= ((Integer)entry.getValue()).intValue()) {
                  iterator.remove();
               }
            }

            List<PotionEffect> potions = Lists.newArrayList();

            for(PotionEffect potioneffect1 : this.potion.getEffects()) {
               potions.add(new PotionEffect(potioneffect1.getPotion(), potioneffect1.getDuration() / 4, potioneffect1.getAmplifier(), potioneffect1.getIsAmbient(), potioneffect1.doesShowParticles()));
            }

            potions.addAll(this.effects);
            if (potions.isEmpty()) {
               this.reapplicationDelayMap.clear();
            } else {
               List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox());
               if (!list.isEmpty()) {
                  for(EntityLivingBase entitylivingbase : list) {
                     if (!(entitylivingbase instanceof EntityParasiteBase) && !this.reapplicationDelayMap.containsKey(entitylivingbase) && entitylivingbase.canBeHitWithPotion()) {
                        double d0 = entitylivingbase.posX - this.posX;
                        double d1 = entitylivingbase.posZ - this.posZ;
                        double d2 = d0 * d0 + d1 * d1;
                        if (d2 <= (double)(f * f)) {
                           this.reapplicationDelayMap.put(entitylivingbase, Integer.valueOf(this.ticksExisted + this.reapplicationDelay));

                           for(PotionEffect potioneffect : potions) {
                              if (potioneffect.getPotion().isInstant()) {
                                 potioneffect.getPotion().affectEntity(this, this.getOwner(), entitylivingbase, potioneffect.getAmplifier(), 0.5D);
                              } else {
                                 SRPPotions.applyStackPotion(potioneffect.getPotion(), entitylivingbase, potioneffect.getDuration(), potioneffect.getAmplifier());
                              }
                           }

                           if (this.radiusOnUse != 0.0F) {
                              f += this.radiusOnUse;
                              if (f < 0.5F) {
                                 this.setDead();
                                 return;
                              }

                              this.setRadius(f, this.getHeight());
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void setRadiusOnUse(float radiusOnUseIn) {
      this.radiusOnUse = radiusOnUseIn;
   }

   public void setRadiusPerTick(float radiusPerTickIn) {
      this.radiusPerTick = radiusPerTickIn;
   }

   public void setWaitTime(int waitTimeIn) {
      this.waitTime = waitTimeIn;
   }

   public void setOwner(@Nullable EntityLivingBase ownerIn) {
      this.owner = ownerIn;
      this.ownerUniqueId = ownerIn == null ? null : ownerIn.getUniqueID();
   }

   @Nullable
   public EntityLivingBase getOwner() {
      if (this.owner == null && this.ownerUniqueId != null && this.world instanceof WorldServer) {
         Entity entity = ((WorldServer)this.world).getEntityFromUuid(this.ownerUniqueId);
         if (entity instanceof EntityLivingBase) {
            this.owner = (EntityLivingBase)entity;
         }
      }

      return this.owner;
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      this.ticksExisted = compound.getInteger("Age");
      this.duration = compound.getInteger("Duration");
      this.waitTime = compound.getInteger("WaitTime");
      this.reapplicationDelay = compound.getInteger("ReapplicationDelay");
      this.radiusOnUse = compound.getFloat("RadiusOnUse");
      this.radiusPerTick = compound.getFloat("RadiusPerTick");
      this.setRadius(compound.getFloat("Radius"), compound.getFloat("heig"));
      this.ownerUniqueId = compound.getUniqueId("OwnerUUID");
      if (compound.hasKey("Particle", 8)) {
         EnumParticleTypes enumparticletypes = EnumParticleTypes.getByName(compound.getString("Particle"));
         if (enumparticletypes != null) {
            this.setParticle(enumparticletypes);
            this.setParticleParam1(compound.getInteger("ParticleParam1"));
            this.setParticleParam2(compound.getInteger("ParticleParam2"));
         }
      }

      if (compound.hasKey("Color", 99)) {
         this.setColor(compound.getInteger("Color"));
      }

      if (compound.hasKey("Potion", 8)) {
         this.setPotion(PotionUtils.getPotionTypeFromNBT(compound));
      }

      if (compound.hasKey("Effects", 9)) {
         NBTTagList nbttaglist = compound.getTagList("Effects", 10);
         this.effects.clear();

         for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttaglist.getCompoundTagAt(i));
            if (potioneffect != null) {
               this.addEffect(potioneffect);
            }
         }
      }

   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      compound.setInteger("Age", this.ticksExisted);
      compound.setInteger("Duration", this.duration);
      compound.setInteger("WaitTime", this.waitTime);
      compound.setInteger("ReapplicationDelay", this.reapplicationDelay);
      compound.setFloat("RadiusOnUse", this.radiusOnUse);
      compound.setFloat("RadiusPerTick", this.radiusPerTick);
      compound.setFloat("Radius", this.getRadius());
      compound.setFloat("heig", this.getHeight());
      compound.setString("Particle", this.getParticle().getParticleName());
      compound.setInteger("ParticleParam1", this.getParticleParam1());
      compound.setInteger("ParticleParam2", this.getParticleParam2());
      if (this.ownerUniqueId != null) {
         compound.setUniqueId("OwnerUUID", this.ownerUniqueId);
      }

      if (this.potion != PotionTypes.EMPTY && this.potion != null) {
         compound.setString("Potion", ((ResourceLocation)PotionType.REGISTRY.getNameForObject(this.potion)).toString());
      }

      if (!this.effects.isEmpty()) {
         NBTTagList nbttaglist = new NBTTagList();

         for(PotionEffect potioneffect : this.effects) {
            nbttaglist.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
         }

         compound.setTag("Effects", nbttaglist);
      }

   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (RADIUS.equals(key)) {
         this.setRadius(this.getRadius(), this.getHeight());
      }

      super.notifyDataManagerChange(key);
   }

   public EnumPushReaction getMobilityFlag() {
      return EnumPushReaction.IGNORE;
   }
}
