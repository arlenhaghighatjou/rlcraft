package com.lycanitesmobs.core.pets;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureStats;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.info.Variant;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PetEntry {
   public PetManager petManager;
   public UUID petEntryID;
   private String type;
   public boolean active = true;
   public int respawnTime = 0;
   public int respawnTimeMax;
   public boolean isRespawning = false;
   public int spawnCount = 0;
   public boolean temporary = false;
   public int temporaryDuration = 100;
   public boolean spawningActive = true;
   public EntityLivingBase host;
   public SummonSet summonSet;
   public Entity entity;
   public NBTTagCompound entityNBT;
   public int entityTick = 0;
   public float entityHealth = 1.0F;
   public float entityMaxHealth = 1.0F;
   public int entityLevel = 1;
   public int entityExperience = 0;
   public int entityMaxExperience = CreatureStats.BASE_LEVELUP_EXPERIENCE;
   public String entityName = "";
   public int subspeciesIndex = 0;
   public int variantIndex = 0;
   public double entitySize = 1.0D;
   public String color = "000000";
   public boolean teleportEntity = false;
   public boolean releaseEntity = false;

   public static PetEntry createFromEntity(EntityPlayer player, BaseCreatureEntity entity, String petType) {
      CreatureInfo creatureInfo = entity.creatureInfo;
      PetEntry petEntry = new PetEntry(UUID.randomUUID(), petType, player, creatureInfo.getName());
      if (entity.hasCustomName()) {
         petEntry.setEntityName(entity.getCustomNameTag());
      }

      petEntry.setEntitySubspecies(entity.getSubspeciesIndex());
      petEntry.setEntityVariant(entity.getVariantIndex());
      petEntry.setEntitySize(entity.sizeScale);
      petEntry.setColor("000000");
      return petEntry;
   }

   public PetEntry(UUID petEntryID, String type, EntityLivingBase host, String summonType) {
      this.petEntryID = petEntryID;
      this.type = type;
      this.host = host;
      ExtendedPlayer playerExt = null;
      if (host instanceof EntityPlayer) {
         playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)host);
      }

      this.summonSet = new SummonSet(playerExt);
      this.summonSet.summonableOnly = false;
      this.summonSet.setSummonType(summonType);
      this.respawnTimeMax = CreatureManager.getInstance().config.petRespawnTime;
      if ("minion".equalsIgnoreCase(this.type)) {
         this.temporary = true;
      }

   }

   public PetEntry setEntityName(String name) {
      this.entityName = name;
      return this;
   }

   public PetEntry setEntitySubspecies(int index) {
      this.subspeciesIndex = index;
      return this;
   }

   public PetEntry setEntityVariant(int index) {
      this.variantIndex = index;
      return this;
   }

   public PetEntry setEntitySize(double size) {
      this.entitySize = size;
      return this;
   }

   public PetEntry setColor(String color) {
      this.color = color;
      return this;
   }

   public PetEntry setOwner(EntityLivingBase owner) {
      this.host = owner;
      if (this.host != null && this.host instanceof EntityPlayer) {
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer((EntityPlayer)this.host);
         this.summonSet.playerExt = playerExt;
      }

      return this;
   }

   public PetEntry setSpawningActive(boolean spawningActive) {
      if (this.spawningActive == spawningActive) {
         return this;
      } else {
         if (!this.host.getEntityWorld().isRemote) {
            if (!CreatureManager.getInstance().config.isSoulboundAllowed(this.host.getEntityWorld())) {
               spawningActive = false;
            }

            if (!spawningActive) {
               this.despawnEntity();
            } else if (this.usesSpirit() && this.summonSet.playerExt != null) {
               if (this.summonSet.playerExt.spirit < this.getSpiritCost()) {
                  this.spawningActive = false;
                  return this;
               }

               this.summonSet.playerExt.spirit -= this.getSpiritCost();
               this.summonSet.playerExt.spiritReserved += this.getSpiritCost();
            }
         }

         this.spawningActive = spawningActive;
         return this;
      }
   }

   public void setLevel(int level) {
      if (this.entity != null && this.entity instanceof BaseCreatureEntity) {
         ((BaseCreatureEntity)this.entity).setLevel(level);
      }

      this.entityLevel = level;
   }

   public int getLevel() {
      if (this.entity != null && this.entity instanceof BaseCreatureEntity) {
         this.entityLevel = ((BaseCreatureEntity)this.entity).getLevel();
      }

      return this.entityLevel;
   }

   public int getExperience() {
      if (this.entity != null && this.entity instanceof BaseCreatureEntity) {
         this.entityExperience = ((BaseCreatureEntity)this.entity).getExperience();
      }

      return this.entityExperience;
   }

   public void setExperience(int experience) {
      if (this.entity != null && this.entity instanceof BaseCreatureEntity) {
         ((BaseCreatureEntity)this.entity).setExperience(experience);
      }

      this.entityExperience = experience;
   }

   public int getMaxExperience() {
      return CreatureStats.BASE_LEVELUP_EXPERIENCE + Math.round((float)(CreatureStats.BASE_LEVELUP_EXPERIENCE * this.getLevel()) * 0.25F);
   }

   public CreatureInfo getCreatureInfo() {
      return this.summonSet != null && !"".equals(this.summonSet.summonType) ? CreatureManager.getInstance().getCreature(this.summonSet.summonType) : null;
   }

   public float getHealth() {
      if (this.entity != null && this.entity instanceof EntityLivingBase) {
         this.entityHealth = ((EntityLivingBase)this.entity).getHealth();
      }

      return this.entityHealth;
   }

   public float getMaxHealth() {
      if (this.entity != null && this.entity instanceof EntityLivingBase) {
         this.entityMaxHealth = ((EntityLivingBase)this.entity).getMaxHealth();
      }

      return this.entityMaxHealth;
   }

   public void copy(PetEntry copyEntry) {
      this.setEntityName(copyEntry.entityName);
      this.setEntitySubspecies(copyEntry.subspeciesIndex);
      this.setEntityVariant(copyEntry.variantIndex);
      this.setEntitySize(copyEntry.entitySize);
      this.setColor(copyEntry.color);
      if (copyEntry.summonSet != null) {
         this.summonSet.setSummonType(copyEntry.summonSet.summonType);
      }

   }

   public String getDisplayName() {
      String displayName = this.summonSet.getCreatureInfo().getTitle();
      if (this.entityName != null && !"".equals(this.entityName)) {
         displayName = this.entityName + " (" + displayName + ")";
      }

      return displayName;
   }

   public void onAdd(PetManager petManager) {
      this.petManager = petManager;
   }

   public void remove() {
      this.setSpawningActive(false);
      this.active = false;
   }

   public void onUpdate(World world) {
      if (world.isRemote) {
         if (this.isRespawning && this.respawnTime > 0) {
            --this.respawnTime;
         }

      } else if (this.active) {
         if (!this.isActive()) {
            this.remove();
         } else {
            if (!CreatureManager.getInstance().config.isSoulboundAllowed(this.host.getEntityWorld())) {
               this.setSpawningActive(false);
            }

            if (this.spawningActive) {
               if (this.entity != null && !this.entity.isEntityAlive()) {
                  this.saveEntityNBT();
                  this.entity = null;
                  this.isRespawning = true;
                  this.respawnTime = this.respawnTimeMax;
                  if (this.summonSet.playerExt != null) {
                     this.summonSet.playerExt.sendPetEntryToPlayer(this);
                  }
               }

               if (this.entity == null) {
                  if (!this.isRespawning) {
                     this.respawnTime = 0;
                  }

                  if (this.respawnTime > this.respawnTimeMax) {
                     this.respawnTime = this.respawnTimeMax;
                  }

                  if (this.respawnTime-- <= 0) {
                     this.spawnEntity();
                     this.isRespawning = false;
                  }
               }

               if (this.entity != null) {
                  ++this.entityTick;

                  try {
                     if (this.teleportEntity) {
                        if (this.entity.getEntityWorld() != this.host.getEntityWorld()) {
                           this.entity.changeDimension(this.host.getEntityWorld().provider.getDimension());
                        }

                        this.entity.setPosition(this.host.posX, this.host.posY, this.host.posZ);
                     }
                  } catch (Exception var3) {
                     LycanitesMobs.logDebug("Pet", "Unable to teleport a pet.");
                  }

                  if (this.entity instanceof EntityLivingBase) {
                     EntityLivingBase entityLiving = (EntityLivingBase)this.entity;
                     if (this.entityTick % 60 == 0 && entityLiving.getHealth() < entityLiving.getMaxHealth()) {
                        entityLiving.setHealth(Math.min(entityLiving.getHealth() + 1.0F, entityLiving.getMaxHealth()));
                     }

                     this.entityHealth = entityLiving.getHealth();
                     this.entityMaxHealth = entityLiving.getMaxHealth();
                  }

                  if (this.entity.hasCustomName()) {
                     this.entityName = this.entity.getCustomNameTag();
                  }
               }
            } else {
               if (this.entity != null) {
                  this.saveEntityNBT();
                  this.entity.setDead();
                  this.entity = null;
               }

               if (this.respawnTime > 0) {
                  --this.respawnTime;
               }
            }

            this.teleportEntity = false;
         }
      }
   }

   public void onBehaviourUpdate() {
      if (this.entity != null && this.entity instanceof TameableCreatureEntity) {
         this.summonSet.applyBehaviour((TameableCreatureEntity)this.entity);
      }

   }

   public boolean isActive() {
      return this.entity != null || !this.temporary || this.spawnCount <= 0;
   }

   public void spawnEntity() {
      if (this.entity == null && this.host != null) {
         try {
            this.entity = (Entity)this.summonSet.getCreatureClass().getConstructor(World.class).newInstance(this.host.getEntityWorld());
         } catch (Exception var5) {
            LycanitesMobs.logWarning("Pets", "[Pet Entry] Unable to find an entity class for pet entry.  Type: " + this.summonSet.summonType + " Class: " + this.summonSet.getCreatureClass() + " ID: " + this.petEntryID);
         }

         if (this.entity != null) {
            this.loadEntityNBT();
            this.entity.setLocationAndAngles(this.host.posX, this.host.posY, this.host.posZ, this.host.rotationYaw, 0.0F);
            if (this.entity instanceof BaseCreatureEntity) {
               BaseCreatureEntity entityCreature = (BaseCreatureEntity)this.entity;
               entityCreature.applyLevel(this.entityLevel);
               entityCreature.setExperience(this.entityExperience);
               entityCreature.setMinion(true);
               entityCreature.setPetEntry(this);
               if (entityCreature instanceof TameableCreatureEntity) {
                  TameableCreatureEntity entityTameable = (TameableCreatureEntity)entityCreature;
                  this.summonSet.applyBehaviour(entityTameable);
               }

               float randomAngle = 45.0F + 45.0F * this.host.getRNG().nextFloat();
               if (this.host.getRNG().nextBoolean()) {
                  randomAngle = -randomAngle;
               }

               BlockPos spawnPos = entityCreature.getFacingPosition(this.host, -1.0D, (double)randomAngle);
               if (!this.entity.getEntityWorld().isSideSolid(spawnPos, EnumFacing.UP)) {
                  this.entity.setLocationAndAngles((double)spawnPos.getX(), (double)spawnPos.getY(), (double)spawnPos.getZ(), this.host.rotationYaw, 0.0F);
               } else {
                  spawnPos = entityCreature.getFacingPosition(this.host, -1.0D, (double)(-randomAngle));
                  if (this.entity.getEntityWorld().isSideSolid(spawnPos, EnumFacing.UP)) {
                     this.entity.setLocationAndAngles((double)spawnPos.getX(), (double)spawnPos.getY(), (double)spawnPos.getZ(), this.host.rotationYaw, 0.0F);
                  }
               }

               if (this.temporary) {
                  entityCreature.setTemporary(this.temporaryDuration);
               }

               if (this.entityName != null && !"".equals(this.entityName)) {
                  entityCreature.setCustomNameTag(this.entityName);
               }

               entityCreature.setSizeScale(this.entitySize);
               entityCreature.setSubspecies(this.subspeciesIndex);
               entityCreature.applyVariant(this.variantIndex);
               if (entityCreature instanceof TameableCreatureEntity && this.host instanceof EntityPlayer) {
                  TameableCreatureEntity entityTameable = (TameableCreatureEntity)entityCreature;
                  entityTameable.setPlayerOwner((EntityPlayer)this.host);
                  this.summonSet.applyBehaviour(entityTameable);
               }
            }

            ++this.spawnCount;
            if (this.entity instanceof EntityLivingBase && this.isRespawning) {
               EntityLivingBase entityLiving = (EntityLivingBase)this.entity;
               entityLiving.setHealth(entityLiving.getMaxHealth() / 2.0F);
            }

            this.onSpawnEntity(this.entity);
            this.host.getEntityWorld().spawnEntity(this.entity);
            if (this.summonSet.playerExt != null) {
               this.summonSet.playerExt.sendPetEntryToPlayer(this);
            }

         }
      }
   }

   public void onSpawnEntity(Entity entity) {
   }

   public void despawnEntity() {
      if (this.entity != null) {
         this.onDespawnEntity(this.entity);
         this.saveEntityNBT();
         this.entity.setDead();
         this.entity = null;
      }
   }

   public void onDespawnEntity(Entity entity) {
   }

   public void assignEntity(Entity entity) {
      if (this.entity != null) {
         this.despawnEntity();
      }

      this.setSpawningActive(true);
      this.entity = entity;
      if (this.entity instanceof BaseCreatureEntity) {
         BaseCreatureEntity entityCreature = (BaseCreatureEntity)this.entity;
         entityCreature.setMinion(true);
         entityCreature.setPetEntry(this);
         if (entityCreature instanceof TameableCreatureEntity) {
            TameableCreatureEntity entityTameable = (TameableCreatureEntity)entityCreature;
            this.summonSet.updateBehaviour(entityTameable);
         }

         if (this.temporary) {
            entityCreature.setTemporary(this.temporaryDuration);
         }

         if (this.entityName != null && !"".equals(this.entityName)) {
            entityCreature.setCustomNameTag(this.entityName);
         }

         entityCreature.setSizeScale(this.entitySize);
         entityCreature.setSubspecies(this.subspeciesIndex);
         entityCreature.applyVariant(this.variantIndex);
      }

      ++this.spawnCount;
      this.saveEntityNBT();
      this.onSpawnEntity(this.entity);
   }

   public String getType() {
      return this.type;
   }

   public boolean usesSpirit() {
      return "pet".equals(this.getType()) || "mount".equals(this.getType());
   }

   public int getSpiritCost() {
      return this.summonSet.playerExt == null ? 0 : this.summonSet.playerExt.spiritCharge * this.getCreatureInfo().summonCost;
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound.hasUniqueId("UUID")) {
         this.petEntryID = nbtTagCompound.getUniqueId("UUID");
      }

      if (nbtTagCompound.hasKey("Type")) {
         this.type = nbtTagCompound.getString("Type");
      }

      if (nbtTagCompound.hasKey("Active")) {
         this.active = nbtTagCompound.getBoolean("Active");
      }

      if (nbtTagCompound.hasKey("RespawnTime")) {
         this.respawnTime = nbtTagCompound.getInteger("RespawnTime");
      }

      if (nbtTagCompound.hasKey("Respawning")) {
         this.isRespawning = nbtTagCompound.getBoolean("Respawning");
      }

      if (nbtTagCompound.hasKey("SpawningActive")) {
         this.spawningActive = nbtTagCompound.getBoolean("SpawningActive");
      }

      this.summonSet.readFromNBT(nbtTagCompound);
      if (nbtTagCompound.hasKey("EntityName")) {
         this.setEntityName(nbtTagCompound.getString("EntityName"));
      }

      if (!"familiar".equals(this.getType())) {
         if (nbtTagCompound.hasKey("SubspeciesID")) {
            this.setEntitySubspecies(Subspecies.getIndexFromOld(nbtTagCompound.getInteger("SubspeciesID")));
            this.setEntityVariant(Variant.getIndexFromOld(nbtTagCompound.getInteger("SubspeciesID")));
         }

         if (nbtTagCompound.hasKey("Subspecies")) {
            this.setEntitySubspecies(nbtTagCompound.getInteger("Subspecies"));
         }

         if (nbtTagCompound.hasKey("Variant")) {
            this.setEntityVariant(nbtTagCompound.getInteger("Variant"));
         }

         if (nbtTagCompound.hasKey("EntitySize")) {
            this.setEntitySize(nbtTagCompound.getDouble("EntitySize"));
         }

         if (nbtTagCompound.hasKey("Color")) {
            this.setColor(nbtTagCompound.getString("Color"));
         }
      }

      if (nbtTagCompound.hasKey("EntityNBT")) {
         this.entityNBT = nbtTagCompound.getCompoundTag("EntityNBT");
      }

      this.loadEntityNBT();
   }

   public void writeToNBT(NBTTagCompound nbtTagCompound) {
      nbtTagCompound.setUniqueId("UUID", this.petEntryID);
      nbtTagCompound.setString("Type", this.getType());
      nbtTagCompound.setBoolean("Active", this.active);
      nbtTagCompound.setInteger("RespawnTime", this.respawnTime);
      nbtTagCompound.setBoolean("Respawning", this.isRespawning);
      nbtTagCompound.setBoolean("SpawningActive", this.spawningActive);
      this.summonSet.writeToNBT(nbtTagCompound);
      if (this.usesSpirit()) {
         nbtTagCompound.setString("EntityName", this.entityName);
         nbtTagCompound.setInteger("Subspecies", this.subspeciesIndex);
         nbtTagCompound.setInteger("Variant", this.variantIndex);
         nbtTagCompound.setDouble("EntitySize", this.entitySize);
         nbtTagCompound.setString("Color", this.color);
      }

      this.saveEntityNBT();
      nbtTagCompound.setTag("EntityNBT", this.entityNBT);
   }

   public void saveEntityNBT() {
      if (this.entityNBT == null) {
         this.entityNBT = new NBTTagCompound();
      }

      this.entityNBT.setInteger("MobLevel", this.getLevel());
      this.entityNBT.setInteger("Experience", this.getExperience());
      if (this.entity instanceof BaseCreatureEntity) {
         BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)this.entity;
         baseCreatureEntity.inventory.writeToNBT(this.entityNBT);
         NBTTagCompound extTagCompound = new NBTTagCompound();
         baseCreatureEntity.extraMobBehaviour.writeToNBT(extTagCompound);
         this.entityNBT.setTag("ExtraBehaviour", extTagCompound);
         if (this.entity instanceof AgeableCreatureEntity) {
            AgeableCreatureEntity ageableCreatureEntity = (AgeableCreatureEntity)this.entity;
            this.entityNBT.setInteger("Age", ageableCreatureEntity.getGrowingAge());
         }

         if (this.entity.hasCustomName()) {
            this.entityName = this.entity.getCustomNameTag();
         }

         this.entity.writeToNBT(this.entityNBT);
      }

   }

   public void loadEntityNBT() {
      if (this.entityNBT != null) {
         if (this.entityNBT.hasKey("MobLevel")) {
            this.setLevel(this.entityNBT.getInteger("MobLevel"));
         }

         if (this.entityNBT.hasKey("Experience")) {
            this.setExperience(this.entityNBT.getInteger("Experience"));
         }

         if (this.entity instanceof BaseCreatureEntity) {
            BaseCreatureEntity baseCreatureEntity = (BaseCreatureEntity)this.entity;
            baseCreatureEntity.inventory.readFromNBT(this.entityNBT);
            if (this.entity instanceof AgeableCreatureEntity) {
               AgeableCreatureEntity ageableCreatureEntity = (AgeableCreatureEntity)this.entity;
               if (this.entityNBT.hasKey("Age")) {
                  ageableCreatureEntity.setGrowingAge(this.entityNBT.getInteger("Age"));
               } else {
                  ageableCreatureEntity.setGrowingAge(0);
               }
            }
         }

      }
   }
}
