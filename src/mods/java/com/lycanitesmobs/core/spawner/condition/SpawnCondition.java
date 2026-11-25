package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class SpawnCondition {
   public SpawnCondition() {
   }

   public static SpawnCondition createFromJSON(JsonObject json) {
      String type = json.get("type").getAsString();
      SpawnCondition spawnCondition = null;
      if ("world".equalsIgnoreCase(type)) {
         spawnCondition = new WorldSpawnCondition();
      } else if ("player".equalsIgnoreCase(type)) {
         spawnCondition = new PlayerSpawnCondition();
      } else if ("event".equalsIgnoreCase(type)) {
         spawnCondition = new EventSpawnCondition();
      } else if ("date".equalsIgnoreCase(type)) {
         spawnCondition = new DateSpawnCondition();
      } else if ("group".equalsIgnoreCase(type)) {
         spawnCondition = new GroupSpawnCondition();
      }

      spawnCondition.loadFromJSON(json);
      return spawnCondition;
   }

   public void loadFromJSON(JsonObject json) {
   }

   public boolean isMet(World world, EntityPlayer player, BlockPos position) {
      return true;
   }
}
