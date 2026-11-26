package com.lycanitesmobs.core.spawner;

import net.minecraft.entity.EntityList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ItemDrop;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MobSpawn {
   public CreatureInfo creatureInfo;
   public Class entityClass;
   protected boolean ignoreForgeCanSpawnEvent = false;
   protected boolean ignoreMobInstanceConditions = false;
   protected boolean ignoreDimension = false;
   protected String biomeCheck = "default";
   protected boolean ignoreLightLevel = false;
   protected boolean ignoreGroupLimit = false;
   protected int weight = -1;
   protected double chance = -1.0D;
   protected int blockCost = -1;
   protected String mobNameTag = "";
   protected String naturalDespawn = "default";
   protected int temporary = -1;
   protected double mobSizeScale = -1.0D;
   protected int subspecies = -1;
   protected int variant = -1;
   protected boolean fixate = false;
   protected double home = -1.0D;
   protected int mobLevel = 0;
   public boolean spawnAsBoss = false;
   protected List<ItemDrop> mobDrops = new ArrayList();
   public int dungeonLevelMin = -1;
   public int dungeonLevelMax = -1;
   public int blockProtection = 0;
   public boolean dungeonBoss = false;

   public static MobSpawn createFromJSON(JsonObject json) {
      MobSpawn mobSpawn = null;
      if (json.has("mobId")) {
         String mobId = json.get("mobId").getAsString();
         CreatureInfo creatureInfo = CreatureManager.getInstance().getCreatureFromId(mobId);
         if (creatureInfo != null) {
            mobSpawn = new MobSpawn(creatureInfo);
            mobSpawn.loadFromJSON(json);
         } else {
            Class entityClass = null;
            EntityEntry entry = (EntityEntry)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(mobId));
            if (entry != null) {
               entityClass = entry.getEntityClass();
            }

            mobSpawn = new MobSpawn(entityClass);
         }

         if (mobSpawn != null) {
            mobSpawn.loadFromJSON(json);
         } else {
            LycanitesMobs.logWarning("", "[JSONSpawner] Unable to find a Lycanites Mob from the mob id: " + mobId + " Mob Spawn entry ignored.");
         }
      }

      return mobSpawn;
   }

   public MobSpawn(CreatureInfo creatureInfo) {
      this.creatureInfo = creatureInfo;
      this.entityClass = creatureInfo.entityClass;
   }

   public MobSpawn(Class entityClass) {
      this.entityClass = entityClass;
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("ignoreForgeCanSpawnEvent")) {
         this.ignoreForgeCanSpawnEvent = json.get("ignoreForgeCanSpawnEvent").getAsBoolean();
      }

      if (json.has("ignoreDimension")) {
         this.ignoreDimension = json.get("ignoreDimension").getAsBoolean();
      }

      if (json.has("biomeCheck")) {
         this.biomeCheck = json.get("biomeCheck").getAsString();
      }

      if (json.has("ignoreLightLevel")) {
         this.ignoreLightLevel = json.get("ignoreLightLevel").getAsBoolean();
      }

      if (json.has("ignoreGroupLimit")) {
         this.ignoreGroupLimit = json.get("ignoreGroupLimit").getAsBoolean();
      }

      if (json.has("ignoreMobInstanceConditions")) {
         this.ignoreMobInstanceConditions = json.get("ignoreMobInstanceConditions").getAsBoolean();
      }

      if (json.has("weight")) {
         this.weight = json.get("weight").getAsInt();
      }

      if (json.has("chance")) {
         this.chance = json.get("chance").getAsDouble();
      }

      if (json.has("blockCost")) {
         this.blockCost = json.get("blockCost").getAsInt();
      }

      if (json.has("mobNameTag")) {
         this.mobNameTag = json.get("mobNameTag").getAsString();
      }

      if (json.has("naturalDespawn")) {
         this.naturalDespawn = json.get("naturalDespawn").getAsString();
      }

      if (json.has("temporary")) {
         this.temporary = json.get("temporary").getAsInt();
      }

      if (json.has("mobSizeScale")) {
         this.mobSizeScale = json.get("mobSizeScale").getAsDouble();
      }

      if (json.has("subspecies")) {
         this.subspecies = json.get("subspecies").getAsInt();
      }

      if (json.has("variant")) {
         this.variant = json.get("variant").getAsInt();
      }

      if (json.has("fixate")) {
         this.fixate = json.get("fixate").getAsBoolean();
      }

      if (json.has("home")) {
         this.home = json.get("home").getAsDouble();
      }

      if (json.has("mobLevel")) {
         this.mobLevel = json.get("mobLevel").getAsInt();
      }

      if (json.has("spawnAsBoss")) {
         this.spawnAsBoss = json.get("spawnAsBoss").getAsBoolean();
      }

      if (json.has("mobDrops")) {
         this.mobDrops.clear();

         for(JsonElement mobDropJson : json.getAsJsonArray("mobDrops")) {
            ItemDrop itemDrop = ItemDrop.createFromJSON(mobDropJson.getAsJsonObject());
            if (itemDrop != null) {
               this.mobDrops.add(itemDrop);
            }
         }
      }

      if (json.has("dungeonLevelMin")) {
         this.dungeonLevelMin = json.get("dungeonLevelMin").getAsInt();
      }

      if (json.has("dungeonLevelMax")) {
         this.dungeonLevelMax = json.get("dungeonLevelMax").getAsInt();
      }

      if (json.has("blockProtection")) {
         this.blockProtection = json.get("blockProtection").getAsInt();
      }

      if (json.has("dungeonBoss")) {
         this.dungeonBoss = json.get("dungeonBoss").getAsBoolean();
      }

   }

   public boolean canSpawn(World world, int blockCount, Biome biome, boolean forceIgnoreDimension) {
      if (!CreatureManager.getInstance().spawnConfig.isAllowedGlobal(world)) {
         return false;
      } else {
         if (this.creatureInfo != null) {
            if (!this.creatureInfo.enabled || !this.creatureInfo.creatureSpawn.enabled) {
               return false;
            }

            if (world.getDifficulty() == EnumDifficulty.PEACEFUL && !this.creatureInfo.peaceful) {
               return false;
            }
         }

         if (this.getWeight() <= 0) {
            return false;
         } else if (blockCount < this.getBlockCost()) {
            return false;
         } else {
            if (this.creatureInfo != null) {
               if (this.creatureInfo.creatureSpawn.worldDayMin > 0.0D) {
                  ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
                  if (worldExt != null) {
                     int day = (int)Math.floor((double)(worldExt.useTotalWorldTime ? world.getTotalWorldTime() : world.getWorldTime()) / 24000.0D);
                     if ((double)day < this.creatureInfo.creatureSpawn.worldDayMin) {
                        return false;
                     }
                  }
               }

               if (!forceIgnoreDimension && !this.ignoreDimension && !this.creatureInfo.creatureSpawn.isAllowedDimension(world)) {
                  return false;
               }

               if (biome != null && this.shouldCheckBiome() && !this.creatureInfo.creatureSpawn.isValidBiome(biome)) {
                  return false;
               }
            }

            if (this.getChance() < 1.0D && world.rand.nextDouble() > this.getChance()) {
               return false;
            } else {
               return true;
            }
         }
      }
   }

   public int getBlockCost() {
      return this.blockCost > -1 ? this.blockCost : 0;
   }

   public double getChance() {
      return this.chance > -1.0D ? this.chance : 1.0D;
   }

   public int getWeight() {
      if (this.weight > -1) {
         return this.weight;
      } else {
         return this.creatureInfo != null ? this.creatureInfo.creatureSpawn.spawnWeight : 8;
      }
   }

   public boolean getNaturalDespawn() {
      if ("true".equalsIgnoreCase(this.naturalDespawn)) {
         return true;
      } else if ("false".equalsIgnoreCase(this.naturalDespawn)) {
         return false;
      } else {
         return this.creatureInfo != null ? this.creatureInfo.creatureSpawn.despawnNatural : true;
      }
   }

   public boolean shouldCheckBiome() {
      if ("ignore".equalsIgnoreCase(this.biomeCheck)) {
         return false;
      } else if ("check".equalsIgnoreCase(this.biomeCheck)) {
         return true;
      } else if (this.creatureInfo != null) {
         return !this.creatureInfo.creatureSpawn.ignoreBiome;
      } else {
         return false;
      }
   }

   public EntityLiving createEntity(World world) {
      try {
         if (this.creatureInfo != null) {
            return this.creatureInfo.createEntity(world);
         } else {
            return this.entityClass == null ? null : (EntityLiving)EntityList.newEntity(this.entityClass, world);
         }
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public void onSpawned(EntityLiving entityLiving, EntityPlayer player) {
      if (!"".equals(this.mobNameTag)) {
         entityLiving.setCustomNameTag(this.mobNameTag);
      }

      if (!this.getNaturalDespawn()) {
         entityLiving.enablePersistence();
      }

      if (entityLiving instanceof BaseCreatureEntity) {
         BaseCreatureEntity entityCreature = (BaseCreatureEntity)entityLiving;
         boolean firstSpawn = true;
         if (this.mobSizeScale > -1.0D) {
            entityCreature.setSizeScale(this.mobSizeScale);
            firstSpawn = false;
         }

         if (this.subspecies > -1) {
            entityCreature.setSubspecies(this.subspecies);
            firstSpawn = false;
         }

         if (this.variant > -1) {
            entityCreature.applyVariant(this.variant);
            firstSpawn = false;
         }

         if (this.fixate && player != null) {
            entityCreature.setFixateTarget(player);
         }

         if (this.home >= 0.0D) {
            entityCreature.setHome((int)entityCreature.posX, (int)entityCreature.posY, (int)entityCreature.posZ, (float)this.home);
         }

         if (this.mobLevel > 0) {
            entityCreature.addLevel(this.mobLevel);
         }

         if (this.temporary > -1) {
            entityCreature.setTemporary(this.temporary);
         }

         for(ItemDrop itemDrop : this.mobDrops) {
            entityCreature.addSavedItemDrop(itemDrop);
         }

         entityCreature.spawnedAsBoss = this.spawnAsBoss;
         if (this.blockProtection > 0) {
            entityCreature.spawnedWithBlockProtection = this.blockProtection;
            ExtendedWorld.getForWorld(entityCreature.getEntityWorld()).bossUpdate(entityCreature);
         }

         entityCreature.firstSpawn = firstSpawn;
      }

   }

   public String toString() {
      if (this.creatureInfo != null) {
         return this.creatureInfo.getName();
      } else {
         return this.entityClass != null ? this.entityClass.toString() : "Invalid Creature ID or Class";
      }
   }
}
