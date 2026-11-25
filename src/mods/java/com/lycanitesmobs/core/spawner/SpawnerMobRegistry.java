package com.lycanitesmobs.core.spawner;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureInfo;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpawnerMobRegistry {
   public static Map<String, SpawnerMobRegistry> SPAWNER_MOB_REGISTRIES = new HashMap();
   public Map<CreatureInfo, MobSpawn> mobSpawns = new HashMap();

   public SpawnerMobRegistry() {
   }

   public static Collection<MobSpawn> getMobSpawns(String spawnerName) {
      LycanitesMobs.logDebug("JSONSpawner", "Getting Global Mobs For Spawner: " + spawnerName);
      if (SPAWNER_MOB_REGISTRIES.containsKey(spawnerName.toLowerCase())) {
         Collection<MobSpawn> mobs = ((SpawnerMobRegistry)SPAWNER_MOB_REGISTRIES.get(spawnerName.toLowerCase())).mobSpawns.values();
         LycanitesMobs.logDebug("JSONSpawner", "Found " + mobs.size() + " Global Mobs For Spawner: " + spawnerName);
         return mobs;
      } else {
         LycanitesMobs.logDebug("JSONSpawner", "No Global Mobs Found For Spawner: " + spawnerName);
         return null;
      }
   }

   public static void createSpawn(CreatureInfo creatureInfo, String spawnerName) {
      if (creatureInfo == null) {
         LycanitesMobs.logWarning("", "Tried to create a global Mob Spawn to " + spawnerName + " from a null Creature Info!");
      }

      if (spawnerName == null) {
         LycanitesMobs.logWarning("", "Tried to create a global Mob Spawn for " + creatureInfo.getName() + " with a null Spawner Name!");
      }

      MobSpawn mobSpawn = new MobSpawn(creatureInfo);
      if (!SPAWNER_MOB_REGISTRIES.containsKey(spawnerName)) {
         SPAWNER_MOB_REGISTRIES.put(spawnerName, new SpawnerMobRegistry());
      }

      if (!((SpawnerMobRegistry)SPAWNER_MOB_REGISTRIES.get(spawnerName)).addMobSpawn(mobSpawn)) {
         LycanitesMobs.logWarning("", "Tried to create a duplicate global MobSpawn for " + creatureInfo.getName() + " in Spawner: " + spawnerName + "!");
      }

      LycanitesMobs.logDebug("JSONSpawner", "Added " + creatureInfo.getName() + " to " + spawnerName + " global spawn list.");
   }

   public boolean addMobSpawn(MobSpawn mobSpawn) {
      if (this.mobSpawns.containsKey(mobSpawn.creatureInfo)) {
         return false;
      } else {
         this.mobSpawns.put(mobSpawn.creatureInfo, mobSpawn);
         return true;
      }
   }
}
