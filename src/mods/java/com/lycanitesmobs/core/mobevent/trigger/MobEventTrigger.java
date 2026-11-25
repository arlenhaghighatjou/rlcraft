package com.lycanitesmobs.core.mobevent.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.mobevent.MobEvent;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MobEventTrigger {
   public MobEvent mobEvent;
   public List<SpawnCondition> conditions = new ArrayList();
   public int conditionsRequired = 0;
   public int subspecies = -1;

   public static MobEventTrigger createFromJSON(JsonObject json, MobEvent mobEvent) {
      String type = json.get("type").getAsString();
      MobEventTrigger mobEventTrigger = null;
      if ("random".equalsIgnoreCase(type)) {
         mobEventTrigger = new RandomMobEventTrigger(mobEvent);
      } else if ("tick".equalsIgnoreCase(type)) {
         mobEventTrigger = new TickMobEventTrigger(mobEvent);
      } else if ("altar".equalsIgnoreCase(type)) {
         mobEventTrigger = new AltarMobEventTrigger(mobEvent);
      }

      mobEventTrigger.loadFromJSON(json);
      return mobEventTrigger;
   }

   public MobEventTrigger(MobEvent mobEvent) {
      this.mobEvent = mobEvent;
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("conditionsRequired")) {
         this.conditionsRequired = json.get("conditionsRequired").getAsInt();
      }

      if (json.has("subspecies")) {
         this.subspecies = json.get("subspecies").getAsInt();
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

   }

   public boolean canTrigger(World world, EntityPlayer player) {
      return !this.mobEvent.canStart(world, player) ? false : this.triggerConditionsMet(world, player);
   }

   public boolean triggerConditionsMet(World world, EntityPlayer player) {
      if (this.conditions.size() == 0) {
         return true;
      } else {
         int conditionsMet = 0;
         int conditionsRequired = this.conditionsRequired > 0 ? this.conditionsRequired : this.conditions.size();

         for(SpawnCondition condition : this.conditions) {
            BlockPos position = null;
            if (player != null) {
               position = player.getPosition();
            }

            boolean met = condition.isMet(world, player, position);
            if (met) {
               ++conditionsMet;
               if (conditionsMet >= conditionsRequired) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean trigger(World world, EntityPlayer player, BlockPos pos, int level, int variant) {
      LycanitesMobs.logDebug("MobEvents", "Trigger Fired: " + this + " for: " + this.mobEvent.name + " channel: " + this.mobEvent.channel);
      return this.mobEvent.trigger(world, player, pos, level, variant);
   }
}
