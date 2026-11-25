package com.lycanitesmobs.core.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.spawner.SpawnerMobRegistry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class CreatureSpawn {
   public boolean enabled = true;
   public boolean disableVariants = false;
   public List<String> spawners = new ArrayList();
   public List<EnumCreatureType> vanillaSpawnerTypes = new ArrayList();
   public int[] dimensionIds;
   public String dimensionListType = "whitelist";
   public List<String> biomeTags = new ArrayList();
   public List<Biome> biomesFromTags = null;
   public List<String> biomeIds = new ArrayList();
   public List<Biome> biomes = null;
   public boolean ignoreBiome = false;
   public int spawnWeight = 8;
   public int dungeonWeight = 200;
   public int spawnAreaLimit = 5;
   public int spawnGroupMin = 1;
   public int spawnGroupMax = 3;
   public boolean spawnsInLight = false;
   public boolean spawnsInDark = true;
   public double worldDayMin = -1.0D;
   public boolean despawnNatural = true;
   public boolean despawnForced = false;

   public CreatureSpawn() {
   }

   public void loadFromJSON(CreatureInfo creatureInfo, JsonObject json) {
      if (json.has("enabled")) {
         this.enabled = json.get("enabled").getAsBoolean();
      }

      if (json.has("disableSubspecies")) {
         this.disableVariants = json.get("disableSubspecies").getAsBoolean();
      }

      this.spawners.clear();
      this.vanillaSpawnerTypes.clear();
      if (json.has("spawners")) {
         this.spawners = JSONHelper.getJsonStrings(json.get("spawners").getAsJsonArray());

         for(String spawner : this.spawners) {
            LycanitesMobs.logDebug("Creature", "Adding " + creatureInfo.getName() + " to " + spawner + " global spawn list.");
            SpawnerMobRegistry.createSpawn(creatureInfo, spawner);
            if ("monster".equalsIgnoreCase(spawner)) {
               this.vanillaSpawnerTypes.add(EnumCreatureType.MONSTER);
            } else if ("creature".equalsIgnoreCase(spawner)) {
               this.vanillaSpawnerTypes.add(EnumCreatureType.CREATURE);
            } else if ("watercreature".equalsIgnoreCase(spawner)) {
               this.vanillaSpawnerTypes.add(EnumCreatureType.WATER_CREATURE);
            } else if ("ambient".equalsIgnoreCase(spawner)) {
               this.vanillaSpawnerTypes.add(EnumCreatureType.AMBIENT);
            }
         }
      }

      if (json.has("dimensionIds")) {
         JsonArray jsonArray = json.get("dimensionIds").getAsJsonArray();
         this.dimensionIds = new int[jsonArray.size()];
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         for(int i = 0; jsonIterator.hasNext(); ++i) {
            this.dimensionIds[i] = ((JsonElement)jsonIterator.next()).getAsInt();
         }
      }

      if (json.has("dimensionListType")) {
         this.dimensionListType = json.get("dimensionListType").getAsString();
      }

      if (json.has("ignoreBiome")) {
         this.ignoreBiome = json.get("ignoreBiome").getAsBoolean();
      }

      if (json.has("biomeTags")) {
         this.biomeTags.clear();
         this.biomesFromTags = null;
         this.biomeTags = JSONHelper.getJsonStrings(json.get("biomeTags").getAsJsonArray());
      } else if (json.has("biomes")) {
         this.biomeTags.clear();
         this.biomesFromTags = null;
         this.biomeTags = JSONHelper.getJsonStrings(json.get("biomes").getAsJsonArray());
      }

      if (json.has("biomeIds")) {
         this.biomeIds.clear();
         this.biomes = null;
         this.biomeIds = JSONHelper.getJsonStrings(json.get("biomeIds").getAsJsonArray());
      }

      if (json.has("spawnWeight")) {
         this.spawnWeight = json.get("spawnWeight").getAsInt();
      }

      if (json.has("dungeonWeight")) {
         this.dungeonWeight = json.get("dungeonWeight").getAsInt();
      }

      if (json.has("spawnAreaLimit")) {
         this.spawnAreaLimit = json.get("spawnAreaLimit").getAsInt();
      }

      if (json.has("spawnGroupMin")) {
         this.spawnGroupMin = json.get("spawnGroupMin").getAsInt();
      }

      if (json.has("spawnGroupMax")) {
         this.spawnGroupMax = json.get("spawnGroupMax").getAsInt();
      }

      if (json.has("spawnsInLight")) {
         this.spawnsInLight = json.get("spawnsInLight").getAsBoolean();
      }

      if (json.has("spawnsInDark")) {
         this.spawnsInDark = json.get("spawnsInDark").getAsBoolean();
      }

      if (json.has("worldDayMin")) {
         this.worldDayMin = (double)json.get("worldDayMin").getAsInt();
      }

      if (json.has("despawnNatural")) {
         this.despawnNatural = json.get("despawnNatural").getAsBoolean();
      }

      if (json.has("despawnForced")) {
         this.despawnForced = json.get("despawnForced").getAsBoolean();
      }

   }

   public void registerVanillaSpawns(CreatureInfo creatureInfo) {
      LycanitesMobs.logDebug("MobSetup", "========== Post Init Setup: " + creatureInfo.name + " ==========");
      List<Biome> vanillaBiomes = new ArrayList();
      if (this.biomesFromTags == null) {
         this.biomesFromTags = JSONHelper.getBiomesFromTags(this.biomeTags);
      }

      vanillaBiomes.addAll(this.biomesFromTags);
      if (this.biomes == null) {
         this.biomes = JSONHelper.getBiomes(this.biomeIds);
      }

      vanillaBiomes.addAll(this.biomes);
      if (!CreatureManager.getInstance().spawnConfig.disableAllSpawning && creatureInfo.enabled && this.enabled && this.spawnWeight > 0 && this.spawnGroupMax > 0 && CreatureManager.getInstance().spawnConfig.spawnWeightScale > 0.0D) {
         int vanillaWeight = (int)Math.ceil(CreatureManager.getInstance().spawnConfig.spawnWeightScale * (double)this.spawnWeight);

         for(EnumCreatureType creatureType : this.vanillaSpawnerTypes) {
            EntityRegistry.addSpawn(creatureInfo.entityClass, vanillaWeight, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMin, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMax, creatureType, (Biome[])this.biomesFromTags.toArray(new Biome[this.biomesFromTags.size()]));

            for(Biome biome : vanillaBiomes) {
               if (biome == Biomes.HELL) {
                  EntityRegistry.addSpawn(creatureInfo.entityClass, vanillaWeight * 10, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMin, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMax, creatureType, new Biome[]{biome});
                  break;
               }

               LycanitesMobs.logDebug("MobSetup", "Vanilla Spawn Biome Added: " + biome);
            }
         }

         LycanitesMobs.logDebug("MobSetup", "Vanilla Spawns Added - Weight: " + vanillaWeight);
      }

      if (!CreatureManager.getInstance().spawnConfig.disableDungeonSpawners && this.dungeonWeight > 0) {
         DungeonHooks.addDungeonMob(creatureInfo.getResourceLocation(), this.dungeonWeight);
         LycanitesMobs.logDebug("MobSetup", "Dungeon Spawn Added - Weight: " + this.dungeonWeight);
      }

   }

   public boolean isAllowedDimension(World world) {
      if (world != null && world.provider != null) {
         if (!CreatureManager.getInstance().spawnConfig.isAllowedGlobal(world)) {
            return false;
         } else if (this.dimensionIds.length == 0) {
            return true;
         } else {
            for(int dimensionId : this.dimensionIds) {
               if (world.provider.getDimension() == dimensionId) {
                  LycanitesMobs.logDebug("MobSpawns", "Dimension is in " + this.dimensionListType + ".");
                  return this.dimensionListType.equalsIgnoreCase("whitelist");
               }
            }

            LycanitesMobs.logDebug("MobSpawns", "Dimension was not in " + this.dimensionListType + ".");
            return this.dimensionListType.equalsIgnoreCase("blacklist");
         }
      } else {
         LycanitesMobs.logDebug("MobSpawns", "No world or dimension spawn settings were found, defaulting to valid.");
         return true;
      }
   }

   public boolean isValidBiome(Biome biome) {
      if (this.ignoreBiome) {
         return true;
      } else {
         if (!this.biomeTags.isEmpty()) {
            if (this.biomesFromTags == null) {
               this.biomesFromTags = JSONHelper.getBiomesFromTags(this.biomeTags);
            }

            if (this.biomesFromTags.contains(biome)) {
               return true;
            }
         }

         if (!this.biomeIds.isEmpty()) {
            if (this.biomes == null) {
               this.biomes = JSONHelper.getBiomes(this.biomeIds);
            }

            if (this.biomes.contains(biome)) {
               return true;
            }
         }

         return false;
      }
   }
}
