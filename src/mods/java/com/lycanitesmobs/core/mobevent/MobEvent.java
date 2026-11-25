package com.lycanitesmobs.core.mobevent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.mobevent.effects.MobEventEffect;
import com.lycanitesmobs.core.mobevent.trigger.MobEventTrigger;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobEvent {
   protected List<SpawnCondition> conditions = new ArrayList();
   protected List<MobEventTrigger> triggers = new ArrayList();
   protected List<MobEventEffect> effects = new ArrayList();
   public String name = "mobevent";
   public String title = "mobevent";
   protected boolean enabled = true;
   public String channel = "world";
   public int duration = 1200;
   protected int conditionsRequired = 0;
   protected boolean interuptWorldEvents = false;
   protected int levelBoostMin = 0;
   protected int levelBoostMax = 9;

   public MobEvent() {
   }

   public void loadFromJSON(JsonObject json) {
      this.name = json.get("name").getAsString();
      this.title = this.name;
      if (json.has("title")) {
         this.title = json.get("title").getAsString();
      }

      if (json.has("enabled")) {
         this.enabled = json.get("enabled").getAsBoolean();
      }

      if (json.has("channel")) {
         this.channel = json.get("channel").getAsString();
      }

      if (json.has("duration")) {
         this.duration = json.get("duration").getAsInt();
      }

      if (json.has("conditionsRequired")) {
         this.conditionsRequired = json.get("conditionsRequired").getAsInt();
      }

      if (json.has("interuptWorldEvents")) {
         this.interuptWorldEvents = json.get("interuptWorldEvents").getAsBoolean();
      }

      if (json.has("levelBoostMin")) {
         this.levelBoostMin = json.get("levelBoostMin").getAsInt();
      }

      if (json.has("levelBoostMax")) {
         this.levelBoostMax = json.get("levelBoostMax").getAsInt();
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
            MobEventTrigger mobEventTrigger = MobEventTrigger.createFromJSON(triggerJson, this);
            this.triggers.add(mobEventTrigger);
            if (this.enabled) {
               MobEventListener.getInstance().addTrigger(mobEventTrigger);
            }
         }
      }

      if (json.has("effects")) {
         JsonArray jsonArray = json.get("effects").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject effectJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            MobEventEffect mobEventEffect = MobEventEffect.createFromJSON(effectJson);
            this.effects.add(mobEventEffect);
         }
      }

   }

   public void destroy() {
      for(MobEventTrigger mobEventTrigger : this.triggers) {
         MobEventListener.getInstance().removeTrigger(mobEventTrigger);
      }

      MobEventManager.getInstance().removeMobEvent(this);
   }

   public String getTitle() {
      return LanguageManager.translate("mobevent." + this.title + ".name");
   }

   public String getDisplayTitle() {
      String title = this.getTitle().replaceAll(" ", "").toLowerCase();
      return title.equalsIgnoreCase(this.title) ? "" : title;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean canStart(World world, EntityPlayer player) {
      if (world.provider == null) {
         return false;
      } else if (this.enabled && MobEventManager.getInstance().mobEventsEnabled) {
         ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
         if (worldExt == null) {
            return false;
         } else if ("world".equalsIgnoreCase(this.channel) && worldExt.getWorldEvent() != null && !this.interuptWorldEvents) {
            return false;
         } else {
            if (!MobEventManager.getInstance().globalEventConditions.isEmpty()) {
               LycanitesMobs.logDebug("MobEvents", "Global Conditions Required: " + MobEventManager.getInstance().globalEventConditions.size());

               for(SpawnCondition condition : MobEventManager.getInstance().globalEventConditions) {
                  if (!condition.isMet(world, player, player != null ? player.getPosition() : BlockPos.ORIGIN)) {
                     LycanitesMobs.logDebug("MobEvents", "Global Condition: " + condition + "Failed");
                     return false;
                  }
               }
            }

            if (this.conditions.isEmpty()) {
               LycanitesMobs.logDebug("MobEvents", "No Conditions");
               return true;
            } else {
               LycanitesMobs.logDebug("MobEvents", "Conditions Required: " + (this.conditionsRequired > 0 ? this.conditionsRequired : "All"));
               int conditionsMet = 0;
               int conditionsRequired = this.conditionsRequired > 0 ? this.conditionsRequired : this.conditions.size();

               for(SpawnCondition condition : this.conditions) {
                  BlockPos position = null;
                  if (player != null) {
                     position = player.getPosition();
                  }

                  boolean met = condition.isMet(world, player, position);
                  LycanitesMobs.logDebug("MobEvents", "Condition: " + condition + " " + (met ? "Passed" : "Failed"));
                  if (met) {
                     ++conditionsMet;
                     if (conditionsMet >= conditionsRequired) {
                        LycanitesMobs.logDebug("MobEvents", "Sufficient Conditions Met");
                        return true;
                     }
                  }
               }

               LycanitesMobs.logDebug("MobEvents", "Insufficient Conditions Met: " + conditionsMet + "/" + conditionsRequired);
               return false;
            }
         }
      } else {
         LycanitesMobs.logDebug("MobEvents", "Mob Events System Disabled");
         return false;
      }
   }

   public boolean trigger(World world, EntityPlayer player, BlockPos pos, int level, int variant) {
      LycanitesMobs.logDebug("MobEvents", "~O==================== Mob Event Triggered: " + this.name + " ====================O~");
      ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
      if (worldExt == null) {
         return false;
      } else {
         if ("world".equalsIgnoreCase(this.channel)) {
            worldExt.startWorldEvent(this);
         } else if (pos != null) {
            worldExt.startMobEvent(this, player, pos, level, variant);
         }

         return true;
      }
   }

   public void onUpdate(World world, EntityPlayer player, BlockPos pos, int level, int ticks, int variant) {
      for(MobEventEffect mobEventEffect : this.effects) {
         mobEventEffect.onUpdate(world, player, pos, level, ticks, variant);
      }

   }

   public void onSpawn(EntityLiving entity, World world, EntityPlayer player, BlockPos pos, int level, int ticks, int variant) {
      for(MobEventEffect mobEventEffect : this.effects) {
         mobEventEffect.onSpawn(entity, world, player, pos, level, ticks);
      }

      if (entity instanceof BaseCreatureEntity) {
         BaseCreatureEntity entityCreature = (BaseCreatureEntity)entity;
         entityCreature.onFirstSpawn();
         int levelBoost = this.levelBoostMin;
         if (this.levelBoostMax > this.levelBoostMin) {
            levelBoost += entityCreature.getRNG().nextInt(this.levelBoostMax - this.levelBoostMin + 1);
         }

         entityCreature.addLevel(levelBoost);
         if (!entityCreature.isTemporary && !"boss".equalsIgnoreCase(this.channel)) {
            entityCreature.setTemporary(MobEventManager.getInstance().defaultMobDuration);
         }

         if (variant >= 0) {
            entityCreature.applyVariant(variant);
         }
      }

   }

   public MobEventPlayerServer getServerEventPlayer(World world) {
      return new MobEventPlayerServer(this, world);
   }

   public MobEventPlayerClient getClientEventPlayer(World world) {
      return new MobEventPlayerClient(this, world);
   }
}
