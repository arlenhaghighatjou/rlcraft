package com.lycanitesmobs.core.spawner;

import com.google.common.base.Predicate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.mobevent.MobEvent;
import com.lycanitesmobs.core.mobevent.MobEventPlayerServer;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import com.lycanitesmobs.core.spawner.location.SpawnLocation;
import com.lycanitesmobs.core.spawner.trigger.ChunkSpawnTrigger;
import com.lycanitesmobs.core.spawner.trigger.SpawnTrigger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class Spawner {
   public List<SpawnCondition> conditions = new ArrayList();
   public List<SpawnTrigger> triggers = new ArrayList();
   public List<SpawnLocation> locations = new ArrayList();
   public List<MobSpawn> mobSpawns = new ArrayList();
   public String name;
   public String sharedName;
   public boolean loadDefault = true;
   public boolean enabled = true;
   public int conditionsRequired = 0;
   public int triggersRequired = 1;
   public Map<Integer, String> triggerCountMessages = new HashMap();
   public String multipleLocations = "combine";
   public int mobCountMin = 1;
   public int mobCountMax = 1;
   public boolean mobCountPlayerScaling = true;
   public int mobCountPlayerRange = 64;
   public boolean ignoreDimensions = false;
   public boolean ignoreBiomes = false;
   public boolean ignoreCollision = false;
   public boolean ignoreLightLevel = false;
   public boolean ignoreGroupLimit = false;
   protected double groupLimitRange = 32.0D;
   protected boolean ignoreForgeCanSpawnEvent = false;
   public boolean forceNoDespawn = false;
   public boolean chainSpawning = true;
   public boolean enableWithoutMobs = false;
   public String eventName = "";
   public int blockBreakRadius = -1;
   protected boolean dropsRequirePlayerDamage = false;
   protected Map<EntityPlayer, Integer> triggerCounts = new HashMap();

   public Spawner() {
   }

   public void loadFromJSON(JsonObject json) {
      this.name = json.get("name").getAsString();
      this.sharedName = this.name;
      if (json.has("sharedName")) {
         this.sharedName = json.get("sharedName").getAsString();
      }

      if (json.has("loadDefault")) {
         this.loadDefault = json.get("loadDefault").getAsBoolean();
      }

      if (json.has("enabled")) {
         this.enabled = json.get("enabled").getAsBoolean();
      }

      if (json.has("conditionsRequired")) {
         this.conditionsRequired = json.get("conditionsRequired").getAsInt();
      }

      if (json.has("triggersRequired")) {
         this.triggersRequired = json.get("triggersRequired").getAsInt();
      }

      if (json.has("triggerCountMessages")) {
         JsonArray jsonArray = json.get("triggerCountMessages").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject tcmJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            this.triggerCountMessages.put(Integer.valueOf(tcmJson.get("count").getAsInt()), tcmJson.get("message").getAsString());
         }
      }

      if (json.has("multipleLocations")) {
         this.multipleLocations = json.get("multipleLocations").getAsString();
      }

      if (json.has("mobCountMin")) {
         this.mobCountMin = json.get("mobCountMin").getAsInt();
      }

      if (json.has("mobCountMax")) {
         this.mobCountMax = json.get("mobCountMax").getAsInt();
      }

      if (json.has("mobCountPlayerScaling")) {
         this.mobCountPlayerScaling = json.get("mobCountPlayerScaling").getAsBoolean();
      }

      if (json.has("mobCountPlayerRange")) {
         this.mobCountPlayerRange = json.get("mobCountPlayerRange").getAsInt();
      }

      if (json.has("ignoreDimensions")) {
         this.ignoreDimensions = json.get("ignoreDimensions").getAsBoolean();
      }

      if (json.has("ignoreBiomes")) {
         this.ignoreBiomes = json.get("ignoreBiomes").getAsBoolean();
      }

      if (json.has("ignoreCollision")) {
         this.ignoreCollision = json.get("ignoreCollision").getAsBoolean();
      }

      if (json.has("ignoreLightLevel")) {
         this.ignoreLightLevel = json.get("ignoreLightLevel").getAsBoolean();
      }

      if (json.has("ignoreGroupLimit")) {
         this.ignoreGroupLimit = json.get("ignoreGroupLimit").getAsBoolean();
      }

      if (json.has("groupLimitRange")) {
         this.groupLimitRange = json.get("groupLimitRange").getAsDouble();
      }

      if (json.has("ignoreForgeCanSpawnEvent")) {
         this.ignoreForgeCanSpawnEvent = json.get("ignoreForgeCanSpawnEvent").getAsBoolean();
      }

      if (json.has("forceNoDespawn")) {
         this.forceNoDespawn = json.get("forceNoDespawn").getAsBoolean();
      }

      if (json.has("chainSpawning")) {
         this.chainSpawning = json.get("chainSpawning").getAsBoolean();
      }

      if (json.has("enableWithoutMobs")) {
         this.enableWithoutMobs = json.get("enableWithoutMobs").getAsBoolean();
      }

      if (json.has("eventName")) {
         this.eventName = json.get("eventName").getAsString();
      }

      if (json.has("blockBreakRadius")) {
         this.blockBreakRadius = json.get("blockBreakRadius").getAsInt();
      }

      if (json.has("dropsRequirePlayerDamage")) {
         this.dropsRequirePlayerDamage = json.get("dropsRequirePlayerDamage").getAsBoolean();
      }

      if (json.has("conditions")) {
         JsonArray jsonArray = json.get("conditions").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject conditionJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            SpawnCondition spawnCondition = SpawnCondition.createFromJSON(conditionJson);
            this.conditions.add(spawnCondition);
         }
      }

      if (json.has("triggers")) {
         JsonArray jsonArray = json.get("triggers").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject triggerJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            SpawnTrigger spawnTrigger = SpawnTrigger.createFromJSON(triggerJson, this);
            this.triggers.add(spawnTrigger);
            if (this.enabled) {
               SpawnerEventListener.getInstance().addTrigger(spawnTrigger);
            }
         }
      }

      if (json.has("locations")) {
         JsonArray jsonArray = json.get("locations").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject locationJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            SpawnLocation spawnLocation = SpawnLocation.createFromJSON(locationJson);
            this.locations.add(spawnLocation);
         }
      }

      if (json.has("mobSpawns")) {
         JsonArray jsonArray = json.get("mobSpawns").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject mobSpawnJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            MobSpawn mobSpawn = MobSpawn.createFromJSON(mobSpawnJson);
            if (mobSpawn != null) {
               this.mobSpawns.add(mobSpawn);
            }
         }
      }

   }

   public void destroy() {
      for(SpawnTrigger spawnTrigger : this.triggers) {
         SpawnerEventListener.getInstance().removeTrigger(spawnTrigger);
      }

      SpawnerManager.getInstance().removeSpawner(this);
   }

   public boolean isEnabled(World world, EntityPlayer player) {
      if (this.enabled && !CreatureManager.getInstance().spawnConfig.disableAllSpawning && world.getGameRules().getBoolean("doMobSpawning")) {
         if (!"".equals(this.eventName)) {
            ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
            if (worldExt == null) {
               return false;
            }

            if (worldExt.getMobEventPlayerServer(this.eventName) == null) {
               return false;
            }
         }

         return this.enableWithoutMobs || !this.mobSpawns.isEmpty() || SpawnerMobRegistry.getMobSpawns(this.sharedName) != null;
      } else {
         return false;
      }
   }

   public boolean canSpawn(World world, EntityPlayer player, BlockPos triggerPos) {
      if (!SpawnerManager.getInstance().globalSpawnConditions.isEmpty()) {
         LycanitesMobs.logDebug("JSONSpawner", "Global Conditions Required: " + SpawnerManager.getInstance().globalSpawnConditions.size());

         for(SpawnCondition condition : SpawnerManager.getInstance().globalSpawnConditions) {
            if (!condition.isMet(world, player, triggerPos)) {
               LycanitesMobs.logDebug("JSONSpawner", "Global Condition: " + condition + "Failed");
               return false;
            }
         }
      }

      if (this.conditions.isEmpty()) {
         LycanitesMobs.logDebug("JSONSpawner", "No Conditions");
         return true;
      } else {
         LycanitesMobs.logDebug("JSONSpawner", "Conditions Required: " + (this.conditionsRequired > 0 ? this.conditionsRequired : "All"));
         int conditionsMet = 0;
         int conditionsRequired = this.conditionsRequired > 0 ? this.conditionsRequired : this.conditions.size();

         for(SpawnCondition condition : this.conditions) {
            boolean met = condition.isMet(world, player, triggerPos);
            LycanitesMobs.logDebug("JSONSpawner", "Condition: " + condition + " " + (met ? "Passed" : "Failed"));
            if (met) {
               ++conditionsMet;
               if (conditionsMet >= conditionsRequired) {
                  LycanitesMobs.logDebug("JSONSpawner", "Sufficient Conditions Met");
                  return true;
               }
            }
         }

         LycanitesMobs.logDebug("JSONSpawner", "Insufficient Conditions Met: " + conditionsMet + "/" + conditionsRequired);
         return false;
      }
   }

   public boolean trigger(World world, EntityPlayer player, SpawnTrigger spawnTrigger, BlockPos triggerPos, int level, int countAmount, int chain) {
      if (!this.isEnabled(world, player)) {
         return false;
      } else {
         LycanitesMobs.logDebug("JSONSpawner", "~O==================== Spawner Triggered: " + this.name + " ====================O~");
         if (!this.canSpawn(world, player, triggerPos)) {
            LycanitesMobs.logDebug("JSONSpawner", "This Spawner Cannot Spawn");
            return false;
         } else if (this.triggersRequired > 1 && player != null) {
            if (!this.triggerCounts.containsKey(player)) {
               this.triggerCounts.put(player, Integer.valueOf(Math.max(countAmount, 0)));
            }

            int currentCount = ((Integer)this.triggerCounts.get(player)).intValue();
            int lastCount = currentCount;
            if (countAmount == 0) {
               currentCount = 0;
            } else {
               currentCount = currentCount + countAmount;
            }

            if (currentCount != lastCount && this.triggerCountMessages.containsKey(Integer.valueOf(currentCount))) {
               String message = LanguageManager.translate((String)this.triggerCountMessages.get(Integer.valueOf(currentCount)));
               player.sendStatusMessage(new TextComponentString(message), true);
            }

            LycanitesMobs.logDebug("JSONSpawner", "Trigger " + currentCount + "/" + this.triggersRequired);
            if (currentCount >= this.triggersRequired) {
               this.triggerCounts.put(player, Integer.valueOf(0));
               return this.doSpawn(world, player, spawnTrigger, triggerPos, level, chain);
            } else {
               this.triggerCounts.put(player, Integer.valueOf(currentCount));
               return false;
            }
         } else {
            LycanitesMobs.logDebug("JSONSpawner", "Only one trigger required.");
            return this.doSpawn(world, player, spawnTrigger, triggerPos, level, chain);
         }
      }
   }

   public boolean doSpawn(World world, EntityPlayer player, SpawnTrigger spawnTrigger, BlockPos triggerPos, int level, int chain) {
      int mobCount = this.mobCountMin;
      if (this.mobCountMax > this.mobCountMin) {
         mobCount = world.rand.nextInt(this.mobCountMax - this.mobCountMin) + this.mobCountMin;
      }

      if (player != null && this.mobCountPlayerScaling) {
         List<EntityPlayer> nearbyPlayers = world.getPlayers(EntityPlayer.class, (targetPlayer) -> {
            if ((targetPlayer.isCreative() || targetPlayer.isSpectator()) && !SpawnerEventListener.testOnCreative) {
               return false;
            } else {
               return targetPlayer.getDistance(player) <= (float)this.mobCountPlayerRange;
            }
         });
         LycanitesMobs.logDebug("JSONSpawner", "Nearby Players: " + nearbyPlayers.size());
         if (!nearbyPlayers.isEmpty()) {
            mobCount = (int)Math.ceil((double)mobCount / (double)nearbyPlayers.size());
         }
      }

      ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
      LycanitesMobs.logDebug("JSONSpawner", "Spawning Wave: " + mobCount + " Mob(s)");
      LycanitesMobs.logDebug("JSONSpawner", "Trigger World: " + world);
      LycanitesMobs.logDebug("JSONSpawner", "Trigger Player: " + player);
      LycanitesMobs.logDebug("JSONSpawner", "Trigger Position: " + triggerPos);
      List<BlockPos> spawnPositions = this.getSpawnPos(world, player, triggerPos);
      if (spawnTrigger instanceof ChunkSpawnTrigger) {
         for(BlockPos spawnPos : (BlockPos[])spawnPositions.toArray(new BlockPos[spawnPositions.size()])) {
            int chunkOffset = 8;
            if (spawnPos.getX() >= triggerPos.getX() + chunkOffset && spawnPos.getX() <= triggerPos.getX() + chunkOffset) {
               if (spawnPos.getZ() < triggerPos.getZ() + chunkOffset || spawnPos.getZ() > triggerPos.getZ() + chunkOffset) {
                  LycanitesMobs.logDebug("JSONSpawner", "A spawn position exceeds chunk z boundaries and will not be used for worldgen triggered spawning.");
                  spawnPositions.remove(spawnPos);
               }
            } else {
               LycanitesMobs.logDebug("JSONSpawner", "A spawn position exceeds chunk x boundaries and will not be used for worldgen triggered spawning.");
               spawnPositions.remove(spawnPos);
            }
         }
      }

      if (spawnPositions.isEmpty()) {
         LycanitesMobs.logDebug("JSONSpawner", "No Spawn Positions Found From Spawn Location");
         return false;
      } else {
         Collections.shuffle(spawnPositions);
         LycanitesMobs.logDebug("JSONSpawner", "Positions Found: " + spawnPositions.size());
         List<Biome> biomes = null;
         if (!this.ignoreBiomes) {
            biomes = new ArrayList();

            for(BlockPos spawnPos : spawnPositions) {
               Biome biome = world.getBiome(spawnPos);
               if (!biomes.contains(biome)) {
                  biomes.add(biome);
               }
            }

            LycanitesMobs.logDebug("JSONSpawner", "Biomes Found: " + biomes.size());
         } else {
            LycanitesMobs.logDebug("JSONSpawner", "All biome checks are ignored by this Spawner.");
         }

         Map<Biome, List<MobSpawn>> mobSpawns = this.getMobSpawns(world, player, spawnPositions.size(), biomes);
         LycanitesMobs.logDebug("JSONSpawner", "Spawnable Mobs Found: " + mobSpawns.size());
         if (mobSpawns.size() == 0) {
            return false;
         } else {
            int mobsSpawned = 0;

            for(BlockPos spawnPos : spawnPositions) {
               if (mobsSpawned >= mobCount) {
                  break;
               }

               LycanitesMobs.logDebug("JSONSpawner", "---------- Spawn Iteration: " + mobsSpawned + " ----------");
               LycanitesMobs.logDebug("JSONSpawner", "Spawn Position: " + spawnPos);
               MobSpawn mobSpawn = null;
               Biome spawnBiome = world.getBiome(spawnPos);
               if (mobSpawns.containsKey((Object)null)) {
                  mobSpawn = this.chooseMobToSpawn(world, (List)mobSpawns.get((Object)null));
               } else if (mobSpawns.containsKey(spawnBiome)) {
                  mobSpawn = this.chooseMobToSpawn(world, (List)mobSpawns.get(spawnBiome));
               }

               if (mobSpawn == null) {
                  LycanitesMobs.logDebug("JSONSpawner", "No Mob Spawn Chosen");
               } else {
                  LycanitesMobs.logDebug("JSONSpawner", "Spawn Mob: " + mobSpawn);
                  EntityLiving entityLiving = mobSpawn.createEntity(world);
                  if (entityLiving == null) {
                     LycanitesMobs.logWarning("JSONSpawner", "Unable to instantiate an entity. Class: " + mobSpawn.entityClass);
                  } else {
                     BaseCreatureEntity entityCreature = null;
                     if (entityLiving instanceof BaseCreatureEntity) {
                        entityCreature = (BaseCreatureEntity)entityLiving;
                        LycanitesMobs.logDebug("JSONSpawner", "Mob is a Lycanites Mob");
                     } else {
                        LycanitesMobs.logDebug("JSONSpawner", "Mob is not a Lycanites Mob");
                     }

                     LycanitesMobs.logDebug("JSONSpawner", "Attempting to spawn " + entityLiving + "...");
                     entityLiving.setLocationAndAngles((double)spawnPos.getX() + 0.5D, (double)spawnPos.getY(), (double)spawnPos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);
                     Result canSpawn = ForgeEventFactory.canEntitySpawn(entityLiving, world, (float)spawnPos.getX() + 0.5F, (float)spawnPos.getY(), (float)spawnPos.getZ() + 0.5F, false);
                     if (canSpawn == Result.DENY && !this.ignoreForgeCanSpawnEvent && !mobSpawn.ignoreForgeCanSpawnEvent) {
                        LycanitesMobs.logDebug("JSONSpawner", "Spawn Check Failed! Spawning blocked by Forge Can Spawn Event, this is caused another mod.");
                     } else if (!this.mobInstanceSpawnCheck(entityLiving, mobSpawn, world, player, spawnPos, level, canSpawn == Result.ALLOW)) {
                        LycanitesMobs.logDebug("JSONSpawner", "Mob Instance Spawn Check Failed!");
                     } else {
                        this.spawnEntity(world, worldExt, entityLiving, level, mobSpawn, player, chain);
                        if (!ForgeEventFactory.doSpecialSpawn(entityLiving, world, (float)spawnPos.getX() + 0.5F, (float)spawnPos.getY(), (float)spawnPos.getZ() + 0.5F) && entityCreature != null) {
                           entityLiving.onInitialSpawn(world.getDifficultyForLocation(spawnPos), (IEntityLivingData)null);
                        }

                        LycanitesMobs.logDebug("JSONSpawner", "Spawn Checks Passed! Mob Spawned!");
                        ++mobsSpawned;
                     }
                  }
               }
            }

            return mobsSpawned > 0;
         }
      }
   }

   public List<BlockPos> getSpawnPos(World world, EntityPlayer player, BlockPos triggerPos) {
      LycanitesMobs.logDebug("JSONSpawner", "Searching for Spawn Positions...");
      List<BlockPos> spawnPositions = new ArrayList();
      if (this.locations.isEmpty()) {
         LycanitesMobs.logDebug("JSONSpawner", "No Spawn Locations, Using Trigger Position");
         spawnPositions.add(triggerPos);
         return spawnPositions;
      } else if (this.locations.size() == 1) {
         LycanitesMobs.logDebug("JSONSpawner", "Only One Spawn Location Type");
         return ((SpawnLocation)this.locations.get(0)).getSpawnPositions(world, player, triggerPos);
      } else {
         LycanitesMobs.logDebug("JSONSpawner", "Multiple Spawn Location Types, Mode: " + this.multipleLocations);
         if ("order".equals(this.multipleLocations)) {
            for(SpawnLocation location : this.locations) {
               spawnPositions = location.getSpawnPositions(world, player, triggerPos);
               if (!spawnPositions.isEmpty()) {
                  return spawnPositions;
               }
            }
         }

         if ("random".equals(this.multipleLocations)) {
            List<List<BlockPos>> possibleSpawnPosLists = new ArrayList();

            for(SpawnLocation location : this.locations) {
               spawnPositions = location.getSpawnPositions(world, player, triggerPos);
               if (!spawnPositions.isEmpty()) {
                  possibleSpawnPosLists.add(spawnPositions);
               }
            }

            if (possibleSpawnPosLists.isEmpty()) {
               return spawnPositions;
            } else if (possibleSpawnPosLists.size() == 1) {
               return (List)possibleSpawnPosLists.get(0);
            } else {
               int randomIndex = world.rand.nextInt(possibleSpawnPosLists.size());
               return (List)possibleSpawnPosLists.get(randomIndex);
            }
         } else if (!"combine".equals(this.multipleLocations)) {
            return spawnPositions;
         } else {
            for(SpawnLocation location : this.locations) {
               spawnPositions.addAll(location.getSpawnPositions(world, player, triggerPos));
            }

            return spawnPositions;
         }
      }
   }

   public Map<Biome, List<MobSpawn>> getMobSpawns(World world, EntityPlayer player, int blockCount, List<Biome> biomes) {
      Map<Biome, List<MobSpawn>> mobSpawns = new HashMap();
      if (biomes == null) {
         mobSpawns.put((Biome)null, this.getBiomeSpawns(world, player, blockCount, (Biome)null));
         return mobSpawns;
      } else {
         for(Biome biome : biomes) {
            mobSpawns.put(biome, this.getBiomeSpawns(world, player, blockCount, biome));
         }

         return mobSpawns;
      }
   }

   public List<MobSpawn> getBiomeSpawns(World world, EntityPlayer player, int blockCount, Biome biome) {
      List<MobSpawn> biomeSpawns = new ArrayList();
      Collection<MobSpawn> globalSpawns = SpawnerMobRegistry.getMobSpawns(this.sharedName);
      if (globalSpawns != null) {
         biomeSpawns.addAll(globalSpawns);
      }

      biomeSpawns.addAll(this.mobSpawns);
      List<MobSpawn> viableMobSpawns = new ArrayList();

      for(MobSpawn possibleMobSpawn : biomeSpawns) {
         if (possibleMobSpawn.canSpawn(world, blockCount, biome, this.ignoreDimensions)) {
            viableMobSpawns.add(possibleMobSpawn);
         }
      }

      return viableMobSpawns;
   }

   public MobSpawn chooseMobToSpawn(World world, List<MobSpawn> mobSpawns) {
      int totalWeights = 0;

      for(MobSpawn mobSpawn : mobSpawns) {
         totalWeights += mobSpawn.getWeight();
      }

      if (totalWeights <= 0) {
         return null;
      } else {
         int randomWeight = 1;
         if (totalWeights > 1) {
            randomWeight = world.rand.nextInt(totalWeights);
         }

         int searchWeight = 0;
         MobSpawn chosenMobSpawn = null;

         for(MobSpawn mobSpawn : mobSpawns) {
            chosenMobSpawn = mobSpawn;
            if (mobSpawn.getWeight() + searchWeight > randomWeight) {
               break;
            }

            searchWeight += mobSpawn.getWeight();
         }

         return chosenMobSpawn;
      }
   }

   public boolean mobInstanceSpawnCheck(EntityLiving entityLiving, MobSpawn mobSpawn, World world, EntityPlayer player, BlockPos spawnPos, int level, boolean forgeForced) {
      if (entityLiving instanceof BaseCreatureEntity) {
         BaseCreatureEntity entityCreature = (BaseCreatureEntity)entityLiving;
         LycanitesMobs.logDebug("JSONSpawner", "Checking Mob Collision...");
         if (!this.ignoreCollision && !entityCreature.checkSpawnCollision(world, spawnPos)) {
            return false;
         } else {
            LycanitesMobs.logDebug("JSONSpawner", "Checking For Nearby Boss...");
            if (!entityCreature.checkSpawnBoss(world, spawnPos)) {
               return false;
            } else if (mobSpawn.ignoreMobInstanceConditions) {
               LycanitesMobs.logDebug("JSONSpawner", "All Mob Instance Checks Ignored");
               return true;
            } else {
               if (!this.ignoreLightLevel && !mobSpawn.ignoreLightLevel) {
                  LycanitesMobs.logDebug("JSONSpawner", "Checking Light Level...");
                  if (!entityCreature.checkSpawnLightLevel(world, spawnPos)) {
                     return false;
                  }
               }

               if (!this.ignoreGroupLimit && !mobSpawn.ignoreGroupLimit) {
                  LycanitesMobs.logDebug("JSONSpawner", "Checking Group Limit...");
                  if (!entityCreature.checkSpawnGroupLimit(world, spawnPos, this.groupLimitRange)) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else if (!mobSpawn.ignoreMobInstanceConditions) {
         LycanitesMobs.logDebug("JSONSpawner", "None Lycanites Mobs Checks...");
         return entityLiving.getCanSpawnHere();
      } else {
         LycanitesMobs.logDebug("JSONSpawner", "All Mob Instance Checks Ignored");
         return true;
      }
   }

   public void spawnEntity(World world, ExtendedWorld worldExt, EntityLiving entityLiving, int level, MobSpawn mobSpawn, EntityPlayer player, int chain) {
      entityLiving.timeUntilPortal = entityLiving.getPortalCooldown();
      if (entityLiving instanceof BaseCreatureEntity) {
         BaseCreatureEntity entityCreature = (BaseCreatureEntity)entityLiving;
         entityCreature.forceNoDespawn = this.forceNoDespawn;
         entityCreature.spawnedRare = level > 0;
         if (this.blockBreakRadius > -1 && chain == 0) {
            BlockPos creaturePos = entityLiving.getPosition();
            entityCreature.destroyArea(creaturePos.getX(), creaturePos.getY() - 1, creaturePos.getZ(), 100.0F, true, this.blockBreakRadius, this.chainSpawning ? player : null, chain + 1);
         }

         entityCreature.setDropsRequirePlayerDamage(this.dropsRequirePlayerDamage);
         if (!"".equals(this.eventName) && worldExt != null) {
            MobEventPlayerServer mobEventPlayerServer = worldExt.getMobEventPlayerServer(this.eventName);
            if (mobEventPlayerServer != null) {
               entityCreature.spawnEventType = mobEventPlayerServer.mobEvent.title;
               if (mobEventPlayerServer.mobEvent.name.equals(worldExt.getWorldEventName())) {
                  entityCreature.spawnEventCount = worldExt.getWorldEventCount();
               }
            }
         }
      }

      LycanitesMobs.logDebug("JSONSpawner", "Adding Entity To World: " + world + " - " + entityLiving);
      world.spawnEntity(entityLiving);
      mobSpawn.onSpawned(entityLiving, player);
      if (!"".equals(this.eventName) && worldExt != null) {
         MobEventPlayerServer mobEventPlayerServer = worldExt.getMobEventPlayerServer(this.eventName);
         if (mobEventPlayerServer != null) {
            MobEvent mobEvent = mobEventPlayerServer.mobEvent;
            mobEvent.onSpawn(entityLiving, mobEventPlayerServer.world, mobEventPlayerServer.player, mobEventPlayerServer.origin, mobEventPlayerServer.level, mobEventPlayerServer.ticks, -1);
         }
      }

   }
}
