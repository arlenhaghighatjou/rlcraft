package com.lycanitesmobs.core.mobevent.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.mobevent.MobEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TickMobEventTrigger extends MobEventTrigger {
   public double chance = 1.0D;
   public double tickRate = 400.0D;

   public TickMobEventTrigger(MobEvent mobEvent) {
      super(mobEvent);
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("chance")) {
         this.chance = json.get("chance").getAsDouble();
      }

      if (json.has("tickRate")) {
         this.tickRate = json.get("tickRate").getAsDouble();
      }

      super.loadFromJSON(json);
   }

   public void onTick(World world, long ticks) {
      if (ticks != 0L && (double)ticks % this.tickRate == 0.0D) {
         if (this.canTrigger(world, (EntityPlayer)null)) {
            if (this.chance >= 1.0D || world.rand.nextDouble() <= this.chance) {
               this.trigger(world, (EntityPlayer)null, new BlockPos(0, 0, 0), 0, this.subspecies);
            }
         }
      }
   }
}
