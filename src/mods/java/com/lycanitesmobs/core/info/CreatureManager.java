package com.lycanitesmobs.core.info;

import com.lycanitesmobs.core.LycanitesClasses;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.CreatureStats;
import com.lycanitesmobs.core.spawner.SpawnerMobRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

public class CreatureManager extends JSONLoader {
   public static CreatureManager INSTANCE;
   public CreatureConfig config = new CreatureConfig();
   public CreatureSpawnConfig spawnConfig = new CreatureSpawnConfig();
   public Map<String, CreatureType> creatureTypes = new HashMap();
   public Map<String, CreatureGroup> creatureGroups = new HashMap();
   public Map<String, CreatureInfo> creatures = new HashMap();
   public Map<Class, CreatureInfo> creatureClassMap = new HashMap();
   protected int nextCreatureNetworkId = 100;
   public List<ModInfo> loadedMods = new ArrayList();
   public Map<String, Double> difficultyMultipliers = new HashMap();
   public Map<String, Double> levelMultipliers = new HashMap();
   public double tamedHealthMultiplier = 3.0D;
   public boolean dlDungeonsLoaded = false;

   public static CreatureManager getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new CreatureManager();
      }

      return INSTANCE;
   }

   public CreatureManager() {
   }

   public void startup(ModInfo modInfo) {
      this.loadConfig();
      this.loadCreatureTypesFromJSON(modInfo);
      this.loadCreatureGroupsFromJSON(modInfo);
      this.loadCreaturesFromJSON(modInfo);

      for(CreatureType creatureType : this.creatureTypes.values()) {
         creatureType.load();
      }

      for(CreatureGroup creatureGroup : this.creatureGroups.values()) {
         creatureGroup.init();
      }

      for(CreatureInfo creatureInfo : this.creatures.values()) {
         creatureInfo.load();
      }

   }

   public void lateStartup(ModInfo modInfo) {
      for(CreatureInfo creatureInfo : this.creatures.values()) {
         creatureInfo.lateLoad();
      }

   }

   public void loadConfig() {
      ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
      this.config.loadConfig(config);
      this.spawnConfig.loadConfig(ConfigBase.getConfig(LycanitesMobs.modInfo, "spawning"));
      String[] difficultyNames = new String[]{"easy", "normal", "hard"};
      double[] difficultyDefaults = new double[]{0.8D, 1.0D, 1.1D};
      this.difficultyMultipliers = new HashMap();
      config.setCategoryComment("Difficulty Multipliers", "Here you can scale the stats of every mob on a per difficulty basis. Note that on easy, speed is kept at 1.0 by default as 0.5 makes them stupidly slow.");
      int difficultyIndex = 0;

      for(String difficultyName : difficultyNames) {
         for(String statName : CreatureStats.STAT_NAMES) {
            double defaultValue = difficultyDefaults[difficultyIndex];
            if ("easy".equalsIgnoreCase(difficultyName) && "speed".equalsIgnoreCase(statName)) {
               defaultValue = 1.0D;
            }

            if ("hard".equalsIgnoreCase(difficultyName) && ("attackSpeed".equalsIgnoreCase(statName) || "rangedSpeed".equalsIgnoreCase(statName))) {
               defaultValue = 1.5D;
            }

            if ("armor".equalsIgnoreCase(statName)) {
               defaultValue = 1.0D;
            }

            if ("sight".equalsIgnoreCase(statName)) {
               defaultValue = 1.0D;
            }

            this.difficultyMultipliers.put((difficultyName + "-" + statName).toUpperCase(Locale.ENGLISH), Double.valueOf(config.getDouble("Difficulty Multipliers", difficultyName + " " + statName, defaultValue)));
         }

         ++difficultyIndex;
      }

      config.setCategoryComment("Mob Level Multipliers", "Normally mobs are level 1, but Spawners can increase their level. Here you can adjust the percentage of each stat that is added per extra level. So by default at level 2 a mobs health is increased by 10%, at level 3 20% and so on.");

      for(String statName : CreatureStats.STAT_NAMES) {
         double levelValue = 0.01D;
         if ("health".equalsIgnoreCase(statName)) {
            levelValue = 0.1D;
         }

         if ("defense".equalsIgnoreCase(statName)) {
            levelValue = 0.01D;
         }

         if ("armor".equalsIgnoreCase(statName)) {
            levelValue = 0.0D;
         }

         if ("speed".equalsIgnoreCase(statName)) {
            levelValue = 0.01D;
         }

         if ("damage".equalsIgnoreCase(statName)) {
            levelValue = 0.02D;
         }

         if ("attackSpeed".equalsIgnoreCase(statName)) {
            levelValue = 0.01D;
         }

         if ("rangedSpeed".equalsIgnoreCase(statName)) {
            levelValue = 0.01D;
         }

         if ("effect".equalsIgnoreCase(statName)) {
            levelValue = 0.02D;
         }

         if ("pierce".equalsIgnoreCase(statName)) {
            levelValue = 0.02D;
         }

         if ("sight".equalsIgnoreCase(statName)) {
            levelValue = 0.0D;
         }

         this.levelMultipliers.put(statName.toUpperCase(Locale.ENGLISH), Double.valueOf(config.getDouble("Mob Level Multipliers", statName, levelValue)));
      }

   }

   public void loadCreatureTypesFromJSON(ModInfo groupInfo) {
      try {
         this.loadAllJson(groupInfo, "Creature Type", "creaturetypes", "name", true);
         LycanitesMobs.logDebug("Creature", "Complete! " + this.creatureTypes.size() + " JSON Creature Types Loaded In Total.");
      } catch (Exception var3) {
         LycanitesMobs.logWarning("", "No Creature Types loaded for: " + groupInfo.name);
      }

   }

   public void loadCreatureGroupsFromJSON(ModInfo groupInfo) {
      try {
         this.loadAllJson(groupInfo, "Creature Group", "creaturegroups", "name", true);
         LycanitesMobs.logDebug("Creature", "Complete! " + this.creatureGroups.size() + " JSON Creature Groups Loaded In Total.");
      } catch (Exception var3) {
         LycanitesMobs.logWarning("", "No Creature Groups loaded for: " + groupInfo.name);
      }

   }

   public void loadCreaturesFromJSON(ModInfo groupInfo) {
      try {
         if (!this.loadedMods.contains(groupInfo)) {
            this.loadedMods.add(groupInfo);
         }

         this.loadAllJson(groupInfo, "Creature", "creatures", "name", false);
         LycanitesMobs.logDebug("Creature", "Complete! " + this.creatures.size() + " JSON Creature Info Loaded In Total.");
      } catch (Exception var3) {
         LycanitesMobs.logWarning("", "No Creatures loaded for: " + groupInfo.name);
      }

   }

   public void parseJson(ModInfo groupInfo, String loadGroup, JsonObject json) {
      if ("Creature Type".equals(loadGroup)) {
         CreatureType creatureType = new CreatureType(groupInfo);
         creatureType.loadFromJSON(json);
         if (creatureType.name == null) {
            LycanitesMobs.logWarning("", "[Creature] Unable to load " + loadGroup + " json due to missing name.");
         } else {
            if (this.creatureTypes.containsKey(creatureType.name)) {
               creatureType = (CreatureType)this.creatureTypes.get(creatureType.name);
               creatureType.loadFromJSON(json);
            }

            this.creatureTypes.put(creatureType.name, creatureType);
         }
      } else if ("Creature Group".equals(loadGroup)) {
         CreatureGroup creatureGroup = new CreatureGroup();
         creatureGroup.loadFromJson(json);
         if (this.creatureGroups.containsKey(creatureGroup.name)) {
            creatureGroup = (CreatureGroup)this.creatureGroups.get(creatureGroup.name);
            creatureGroup.loadFromJson(json);
         }

         this.creatureGroups.put(creatureGroup.name, creatureGroup);
      } else {
         if ("Creature".equals(loadGroup)) {
            CreatureInfo creatureInfo = new CreatureInfo(groupInfo);
            creatureInfo.loadFromJSON(json);
            if (creatureInfo.name == null) {
               LycanitesMobs.logWarning("", "[Creature] Unable to load " + loadGroup + " json due to missing name.");
               return;
            }

            if (this.creatures.containsKey(creatureInfo.name)) {
               creatureInfo = (CreatureInfo)this.creatures.get(creatureInfo.name);
               creatureInfo.loadFromJSON(json);
            }

            this.creatures.put(creatureInfo.name, creatureInfo);
            this.creatureClassMap.put(creatureInfo.entityClass, creatureInfo);
         }

      }
   }

   public int getNextCreatureNetworkId() {
      return this.nextCreatureNetworkId++;
   }

   public void register(EventBus bus) {
      bus.registerGeneric(Register.class, EntityEntry.class, this::registerEntities);
   }

   public void registerEntities(Register<EntityEntry> event) {
      ModInfo modInfo = LycanitesMobs.modInfo;
      LycanitesMobs.logDebug("Creature", "Forge registering all " + this.creatures.size() + " creatures from the mod: " + modInfo.name + "...");

      for(CreatureInfo creatureInfo : this.creatures.values()) {
         if (creatureInfo.modInfo == modInfo) {
            try {
               EntityEntry entityEntry = EntityEntryBuilder.create().entity(creatureInfo.entityClass).factory(LycanitesClasses.worldFactory(creatureInfo.entityClass)).id(creatureInfo.getEntityId(), this.getNextCreatureNetworkId()).name(creatureInfo.getName()).tracker(creatureInfo.isBoss() ? 160 : 80, 3, false).build();
               event.getRegistry().register(entityEntry);
            } catch (Exception var6) {
               LycanitesMobs.logWarning("", "Unable to find entity class for: " + creatureInfo.getName() + ".");
               throw var6;
            }
         }
      }

   }

   public void reload() {
      this.loadConfig();
      SpawnerMobRegistry.SPAWNER_MOB_REGISTRIES.clear();

      for(ModInfo group : this.loadedMods) {
         this.loadCreaturesFromJSON(group);
      }

   }

   public CreatureType getCreatureType(String creatureTypeName) {
      return !this.creatureTypes.containsKey(creatureTypeName) ? null : (CreatureType)this.creatureTypes.get(creatureTypeName);
   }

   public CreatureGroup getCreatureGroup(String creatureGroupName) {
      return !this.creatureGroups.containsKey(creatureGroupName) ? null : (CreatureGroup)this.creatureGroups.get(creatureGroupName);
   }

   public CreatureInfo getCreature(String creatureName) {
      return !this.creatures.containsKey(creatureName) ? null : (CreatureInfo)this.creatures.get(creatureName);
   }

   public CreatureInfo getCreature(Class creatureClass) {
      return !this.creatureClassMap.containsKey(creatureClass) ? null : (CreatureInfo)this.creatureClassMap.get(creatureClass);
   }

   public CreatureInfo getCreatureFromId(String entityId) {
      entityId = entityId.replace(".", ":");
      String[] mobIdParts = entityId.toLowerCase().split(":");
      return this.getCreature(mobIdParts[mobIdParts.length - 1]);
   }

   public Class<? extends EntityLiving> getEntityClass(String creatureName) {
      CreatureInfo creatureInfo = this.getCreature(creatureName);
      return creatureInfo == null ? null : creatureInfo.entityClass;
   }

   public double getDifficultyMultiplier(String difficultyName, String statName) {
      String key = difficultyName.toUpperCase(Locale.ENGLISH) + "-" + statName.toUpperCase(Locale.ENGLISH);
      return !this.difficultyMultipliers.containsKey(key) ? 1.0D : ((Double)this.difficultyMultipliers.get(key)).doubleValue();
   }

   public double getLevelMultiplier(String statName) {
      return !this.levelMultipliers.containsKey(statName.toUpperCase(Locale.ENGLISH)) ? 1.0D : ((Double)this.levelMultipliers.get(statName.toUpperCase(Locale.ENGLISH))).doubleValue();
   }
}
