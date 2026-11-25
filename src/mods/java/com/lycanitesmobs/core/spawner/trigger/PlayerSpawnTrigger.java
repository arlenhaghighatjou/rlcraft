package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class PlayerSpawnTrigger extends SpawnTrigger {
   public boolean creative = false;
   public double tickRate = 400.0D;
   public double lastTickDistanceMin = -1.0D;
   public double lastTickDistanceMax = -1.0D;
   public boolean useWorldTime = false;
   protected Map<EntityPlayer, BlockPos> lastTickPositions = new HashMap();

   public PlayerSpawnTrigger(Spawner spawner) {
      super(spawner);
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("creative")) {
         this.creative = json.get("creative").getAsBoolean();
      }

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

   public void onTick(EntityPlayer player, long ticks) {
      if (SpawnerEventListener.testOnCreative || this.creative || !player.isCreative() && !player.isSpectator()) {
         if (!this.useWorldTime || (double)(player.getEntityWorld().getWorldTime() % 24000L) == this.tickRate) {
            if (ticks != 0L && (double)ticks % this.tickRate == 0.0D) {
               if (this.chance >= 1.0D || player.getRNG().nextDouble() <= this.chance) {
                  if (this.lastTickDistanceMin > -1.0D || this.lastTickDistanceMax > -1.0D) {
                     BlockPos playerPos = player.getPosition();
                     if (!this.lastTickPositions.containsKey(player)) {
                        this.lastTickPositions.put(player, playerPos);
                     } else {
                        playerPos = (BlockPos)this.lastTickPositions.get(player);
                     }

                     double lastTickDistance = player.getPosition().distanceSq(playerPos);
                     if (this.lastTickDistanceMin > -1.0D && lastTickDistance < this.lastTickDistanceMin) {
                        return;
                     }

                     if (this.lastTickDistanceMax > -1.0D && lastTickDistance > this.lastTickDistanceMax) {
                        return;
                     }
                  }

                  this.trigger(player.getEntityWorld(), player, player.getPosition(), 0, 0);
               }
            }
         }
      }
   }
}
