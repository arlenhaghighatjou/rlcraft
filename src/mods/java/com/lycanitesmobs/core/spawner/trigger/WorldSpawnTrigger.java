package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldSpawnTrigger extends SpawnTrigger {
   public double tickRate = 400.0D;
   public double lastTickDistanceMin = -1.0D;
   public double lastTickDistanceMax = -1.0D;
   public boolean useWorldTime = false;

   public WorldSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("tickRate")) {
         this.tickRate = json.get("tickRate").getAsDouble();
      }

      if (json.has("lastTickDistanceMin")) {
         this.lastTickDistanceMin = json.get("lastTickDistanceMin").getAsDouble();
      }

      if (json.has("lastTickDistanceMax")) {
         this.lastTickDistanceMax = json.get("lastTickDistanceMax").getAsDouble();
      }

      if (json.has("useWorldTime")) {
         this.useWorldTime = json.get("useWorldTime").getAsBoolean();
      }

      super.loadFromJSON(json);
   }

   public void onTick(World world, BlockPos position, long ticks) {
      if (!this.useWorldTime || (double)(world.getWorldTime() % 24000L) == this.tickRate) {
         if (ticks != 0L && (double)ticks % this.tickRate == 0.0D) {
            if (this.chance >= 1.0D || world.rand.nextDouble() <= this.chance) {
               this.trigger(world, (EntityPlayer)null, position, 0, 0);
            }
         }
      }
   }
}
