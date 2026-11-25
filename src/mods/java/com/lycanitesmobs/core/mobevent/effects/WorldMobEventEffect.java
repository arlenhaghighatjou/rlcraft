package com.lycanitesmobs.core.mobevent.effects;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldMobEventEffect extends MobEventEffect {
   protected String rain = "none";
   protected String thunder = "none";
   protected int dayTime = -1;

   public WorldMobEventEffect() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("rain")) {
         this.rain = json.get("rain").getAsString();
      }

      if (json.has("thunder")) {
         this.thunder = json.get("thunder").getAsString();
      }

      if (json.has("dayTime")) {
         this.dayTime = json.get("dayTime").getAsInt();
      }

      super.loadFromJSON(json);
   }

   public void onUpdate(World world, EntityPlayer player, BlockPos pos, int level, int ticks, int variant) {
      if (ticks == 0) {
         if ("start".equalsIgnoreCase(this.rain)) {
            world.getWorldInfo().setRaining(true);
         } else if ("stop".equalsIgnoreCase(this.rain)) {
            world.getWorldInfo().setRaining(false);
         }

         if ("start".equalsIgnoreCase(this.thunder)) {
            world.getWorldInfo().setThundering(true);
         } else if ("stop".equalsIgnoreCase(this.thunder)) {
            world.getWorldInfo().setThundering(false);
         }

         if (this.dayTime >= 0) {
            int dayTime = 23999;
            long currentTime = world.provider.getWorldTime();
            int targetTime = this.dayTime;
            long excessTime = currentTime % (long)dayTime;
            if (excessTime > (long)targetTime) {
               targetTime += dayTime;
            }

            world.provider.setWorldTime(currentTime - excessTime + (long)targetTime);
         }
      }

   }
}
