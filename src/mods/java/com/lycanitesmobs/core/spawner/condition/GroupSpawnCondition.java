package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GroupSpawnCondition extends SpawnCondition {
   public List<SpawnCondition> conditions = new ArrayList();
   public int conditionsRequired = 0;

   public GroupSpawnCondition() {
   }

   public void loadFromJSON(JsonObject json) {
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

      super.loadFromJSON(json);
   }

   public boolean isMet(World world, EntityPlayer player, BlockPos position) {
      int conditionsMet = 0;
      int conditionsRequired = this.conditionsRequired > 0 ? this.conditionsRequired : this.conditions.size();

      for(SpawnCondition condition : this.conditions) {
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
