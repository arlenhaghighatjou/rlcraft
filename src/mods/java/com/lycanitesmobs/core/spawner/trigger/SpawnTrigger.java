package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.spawner.Spawner;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class SpawnTrigger {
   public Spawner spawner;
   public int count = 1;
   public double chance = 1.0D;
   public List<SpawnCondition> conditions = new ArrayList();
   public int conditionsRequired = 0;
   public int cooldown = 0;
   protected Map<EntityPlayer, Long> playerUsedTicks = new HashMap();

   public static SpawnTrigger createFromJSON(JsonObject json, Spawner spawner) {
      String type = json.get("type").getAsString();
      SpawnTrigger spawnTrigger = null;
      if ("world".equalsIgnoreCase(type)) {
         spawnTrigger = new WorldSpawnTrigger(spawner);
      } else if (!"player".equalsIgnoreCase(type) && !"tick".equalsIgnoreCase(type)) {
         if ("entitySpawned".equalsIgnoreCase(type)) {
            spawnTrigger = new EntitySpawnedSpawnTrigger(spawner);
         } else if ("kill".equalsIgnoreCase(type)) {
            spawnTrigger = new KillSpawnTrigger(spawner);
         } else if ("chunk".equalsIgnoreCase(type)) {
            spawnTrigger = new ChunkSpawnTrigger(spawner);
         } else if ("block".equalsIgnoreCase(type)) {
            spawnTrigger = new BlockSpawnTrigger(spawner);
         } else if ("ore".equalsIgnoreCase(type)) {
            spawnTrigger = new OreBlockSpawnTrigger(spawner);
         } else if ("crop".equalsIgnoreCase(type)) {
            spawnTrigger = new CropBlockSpawnTrigger(spawner);
         } else if ("tree".equalsIgnoreCase(type)) {
            spawnTrigger = new TreeBlockSpawnTrigger(spawner);
         } else if ("sleep".equalsIgnoreCase(type)) {
            spawnTrigger = new SleepSpawnTrigger(spawner);
         } else if ("fishing".equalsIgnoreCase(type)) {
            spawnTrigger = new FishingSpawnTrigger(spawner);
         } else if ("explosion".equalsIgnoreCase(type)) {
            spawnTrigger = new ExplosionSpawnTrigger(spawner);
         } else if ("mobEvent".equalsIgnoreCase(type)) {
            spawnTrigger = new MobEventSpawnTrigger(spawner);
         } else if ("mix".equalsIgnoreCase(type)) {
            spawnTrigger = new MixBlockSpawnTrigger(spawner);
         }
      } else {
         spawnTrigger = new PlayerSpawnTrigger(spawner);
      }

      spawnTrigger.loadFromJSON(json);
      return spawnTrigger;
   }

   public SpawnTrigger(Spawner spawner) {
      this.spawner = spawner;
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("count")) {
         this.count = json.get("count").getAsInt();
      }

      if (json.has("chance")) {
         this.chance = json.get("chance").getAsDouble();
      }

      if (json.has("conditionsRequired")) {
         this.conditionsRequired = json.get("conditionsRequired").getAsInt();
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

      if (json.has("cooldown")) {
         this.cooldown = json.get("cooldown").getAsInt();
      }

   }

   public boolean trigger(World world, EntityPlayer player, BlockPos triggerPos, int level, int chain) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
      if (this.cooldown > 0 && playerExt != null) {
         long lastUsedTicks = 0L;
         if (!this.playerUsedTicks.containsKey(player)) {
            this.playerUsedTicks.put(player, Long.valueOf(lastUsedTicks));
         } else {
            lastUsedTicks = ((Long)this.playerUsedTicks.get(player)).longValue();
         }

         if (playerExt.timePlayed < lastUsedTicks + (long)this.cooldown) {
            return false;
         }
      }

      if (!this.triggerConditionsMet(world, player, triggerPos)) {
         return false;
      } else {
         if ("".equals(this.spawner.eventName)) {
            LycanitesMobs.logDebug("JSONSpawner", "Trigger Fired: " + this + " for: " + this.spawner.name);
         }

         return this.spawner.trigger(world, player, this, triggerPos, level, this.count, chain);
      }
   }

   public boolean triggerConditionsMet(World world, EntityPlayer player, BlockPos triggerPos) {
      if (this.conditions.size() == 0) {
         return true;
      } else {
         int conditionsMet = 0;
         int conditionsRequired = this.conditionsRequired > 0 ? this.conditionsRequired : this.conditions.size();

         for(SpawnCondition condition : this.conditions) {
            boolean met = condition.isMet(world, player, triggerPos);
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

   public void applyToEntity(EntityLiving entityLiving) {
   }
}
