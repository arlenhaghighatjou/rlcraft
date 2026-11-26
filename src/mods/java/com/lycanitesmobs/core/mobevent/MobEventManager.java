package com.lycanitesmobs.core.mobevent;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class MobEventManager extends JSONLoader {
   protected static MobEventManager INSTANCE;
   public Map<String, MobEvent> mobEvents = new HashMap();
   public List<MobEventSchedule> mobEventSchedules = new ArrayList();
   public List<SpawnCondition> globalEventConditions = new ArrayList();
   public boolean mobEventsEnabled = true;
   public boolean mobEventsRandom = false;
   public int defaultMobDuration = 12000;
   public int minEventsRandomDay = 0;
   public int minTicksUntilEvent = 72000;
   public int maxTicksUntilEvent = 144000;

   public MobEventManager() {
   }

   public static MobEventManager getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new MobEventManager();
      }

      return INSTANCE;
   }

   public void loadConfig() {
      ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.modInfo, "mobevents");
      config.setCategoryComment("Global", "These are various settings that apply to all events.");
      this.mobEventsEnabled = config.getBool("Global", "Mob Events Enabled", this.mobEventsEnabled, "Set to false to completely disable the entire event system for every world.");
      this.mobEventsRandom = config.getBool("Global", "Random Mob Events Enabled", this.mobEventsRandom, "Set to false to disable random mob events for every world.");
      this.defaultMobDuration = config.getInt("Global", "Default Mob Duration", this.defaultMobDuration, "The default temporary time applied to mobs spawned from events, where it will forcefully despawn after the specified time (in ticks). MobSpawns can override this.");
      this.minEventsRandomDay = config.getInt("Global", "Default Random Mob Events Day Minimum", this.minEventsRandomDay, "If random events are enabled, they wont occur until this day is reached. Set to 0 to have random events enabled from the start of a world.");
      this.minTicksUntilEvent = config.getInt("Global", "Default Min Ticks Until Random Event", this.minTicksUntilEvent, "Minimum time in ticks until a random event can occur. 20 Ticks = 1 Second.");
      this.maxTicksUntilEvent = config.getInt("Global", "Default Max Ticks Until Random Event", this.maxTicksUntilEvent, "Maximum time in ticks until a random event can occur. 20 Ticks = 1 Second.");
   }

   public void loadAllFromJSON(ModInfo groupInfo) {
      LycanitesMobs.logDebug("MobEvents", "Loading JSON Mob Events!");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
      Map<String, JsonObject> mobEventJSONs = new HashMap();
      String path = Utilities.getAssetPath(groupInfo.getClass(), groupInfo.modid, "mobevents");
      Map<String, JsonObject> defaultMobEventJSONs = new HashMap();
      this.loadJsonObjects(gson, path, defaultMobEventJSONs, "name", "event");
      String configPath = "config/" + "lycanitesmobs" + "/";
      VFile2 customDir = new VFile2(configPath + "mobevents");
      Map<String, JsonObject> customMobEventJSONs = new HashMap();
      this.loadJsonObjects(gson, customDir, customMobEventJSONs, "name", "event");
      this.writeDefaultJSONObjects(gson, defaultMobEventJSONs, customMobEventJSONs, mobEventJSONs, true, "mobevents");
      LycanitesMobs.logDebug("MobEvents", "Loading " + mobEventJSONs.size() + " Mob Events...");

      for(String spawnerJSONName : mobEventJSONs.keySet()) {
         try {
            JsonObject spawnerJSON = (JsonObject)mobEventJSONs.get(spawnerJSONName);
            LycanitesMobs.logDebug("MobEvents", "Loading Mob Event JSON: " + spawnerJSON);
            MobEvent mobEvent = new MobEvent();
            mobEvent.loadFromJSON(spawnerJSON);
            this.addMobEvent(mobEvent);
         } catch (JsonParseException var23) {
            LycanitesMobs.logWarning("", "Parsing error loading JSON Mob Event: " + spawnerJSONName);
            var23.printStackTrace();
         } catch (Exception var24) {
            LycanitesMobs.logWarning("", "There was a problem loading JSON Mob Event: " + spawnerJSONName);
            var24.printStackTrace();
         }
      }

      LycanitesMobs.logDebug("MobEvents", "Complete! " + this.mobEvents.size() + " JSON Mob Events Loaded In Total.");
      this.mobEventSchedules.clear();
      String defaultSchedulePath = Utilities.getAssetPath(groupInfo.getClass(), groupInfo.modid, "mobeventschedule.json");
      JsonObject defaultScheduleJson = this.loadJsonObject(gson, defaultSchedulePath);
      VFile2 customScheduleFile = new VFile2(configPath + "mobeventschedule.json");
      JsonObject customScheduleJson = null;
      if (customScheduleFile.exists()) {
         customScheduleJson = this.loadJsonObject(gson, customScheduleFile);
      }

      JsonObject scheduleJson = this.writeDefaultJSONObject(gson, "mobeventschedule", defaultScheduleJson, customScheduleJson);
      if (scheduleJson.has("schedules")) {
         JsonArray jsonArray = scheduleJson.get("schedules").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject scheduleEntryJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            MobEventSchedule mobEventSchedule = MobEventSchedule.createFromJSON(scheduleEntryJson);
            this.mobEventSchedules.add(mobEventSchedule);
         }
      }

      if (this.mobEventSchedules.size() > 0) {
         LycanitesMobs.logDebug("MobEvents", "Loaded " + this.mobEventSchedules.size() + " Mob Event Schedules.");
      }

      this.globalEventConditions.clear();
      String defaultGlobalPath = Utilities.getAssetPath(this.getClass(), LycanitesMobs.modInfo.modid, "globalmobevent.json");
      JsonObject defaultGlobalJson = this.loadJsonObject(gson, defaultGlobalPath);
      VFile2 customGlobalFile = new VFile2(configPath + "globalmobevent.json");
      JsonObject customGlobalJson = null;
      if (customGlobalFile.exists()) {
         customGlobalJson = this.loadJsonObject(gson, customGlobalFile);
      }

      JsonObject globalJson = this.writeDefaultJSONObject(gson, "globalmobevent", defaultGlobalJson, customGlobalJson);
      if (globalJson.has("conditions")) {
         JsonArray jsonArray = globalJson.get("conditions").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject spawnConditionJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            SpawnCondition spawnCondition = SpawnCondition.createFromJSON(spawnConditionJson);
            this.globalEventConditions.add(spawnCondition);
         }
      }

      if (this.globalEventConditions.size() > 0) {
         LycanitesMobs.logDebug("JSONSpawner", "Loaded " + this.globalEventConditions.size() + " Global Mob Event Conditions.");
      }

   }

   public void parseJson(ModInfo groupInfo, String name, JsonObject json) {
   }

   public void reload() {
      LycanitesMobs.logDebug("MobEvents", "Destroying JSON Mob Events!");

      for(MobEvent mobEvent : (MobEvent[])this.mobEvents.values().toArray(new MobEvent[this.mobEvents.size()])) {
         mobEvent.destroy();
      }

      this.loadAllFromJSON(LycanitesMobs.modInfo);
   }

   public void addMobEvent(MobEvent mobEvent) {
      if (mobEvent != null) {
         this.mobEvents.put(mobEvent.name, mobEvent);

         try {
            AssetManager.addSound("mobevent_" + mobEvent.title.toLowerCase(), LycanitesMobs.modInfo, "mobevent." + mobEvent.title.toLowerCase());
         } catch (Exception var3) {
            ;
         }

      }
   }

   public void removeMobEvent(MobEvent mobEvent) {
      if (!this.mobEvents.containsKey(mobEvent.name)) {
         LycanitesMobs.logWarning("", "[MobEvents] Tried to remove a Mob Event that hasn't been added: " + mobEvent.name);
      } else {
         this.mobEvents.remove(mobEvent.name);
      }
   }

   public MobEvent getMobEvent(String mobEventName) {
      return !this.mobEvents.containsKey(mobEventName) ? null : (MobEvent)this.mobEvents.get(mobEventName);
   }

   public void register(EventBus bus) {
      bus.register(WorldTickEvent.class, this::onWorldUpdate);
      bus.register(ClientTickEvent.class, this::onClientUpdate);
   }

   public void onWorldUpdate(WorldTickEvent event) {
      World world = event.world;
      if (!world.isRemote) {
         ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
         if (worldExt != null) {
            if (worldExt.lastEventUpdateTime != world.getTotalWorldTime()) {
               worldExt.lastEventUpdateTime = world.getTotalWorldTime();
               if (worldExt.serverWorldEventPlayer != null) {
                  worldExt.serverWorldEventPlayer.onUpdate();
               }

               if (worldExt.serverMobEventPlayers.size() > 0) {
                  for(MobEventPlayerServer mobEventPlayerServer : (MobEventPlayerServer[])worldExt.serverMobEventPlayers.values().toArray(new MobEventPlayerServer[worldExt.serverMobEventPlayers.size()])) {
                     mobEventPlayerServer.onUpdate();
                  }
               }

            }
         }
      }
   }

   public void onClientUpdate(ClientTickEvent event) {
      if (LycanitesMobs.proxy.getClientPlayer() != null) {
         World world = LycanitesMobs.proxy.getClientPlayer().getEntityWorld();
         if (world.isRemote) {
            ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
            if (worldExt != null) {
               for(MobEventPlayerClient mobEventPlayerClient : worldExt.clientMobEventPlayers.values()) {
                  mobEventPlayerClient.onUpdate();
               }

               if (worldExt.clientWorldEventPlayer != null) {
                  worldExt.clientWorldEventPlayer.onUpdate();
               }

            }
         }
      }
   }
}
