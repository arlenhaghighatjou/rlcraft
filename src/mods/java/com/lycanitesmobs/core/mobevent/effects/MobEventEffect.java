package com.lycanitesmobs.core.mobevent.effects;

import com.google.gson.JsonObject;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MobEventEffect {
   public double eventTicksMin = 0.0D;
   public double eventTicksMax = 0.0D;
   public double eventTicksN = 0.0D;

   public MobEventEffect() {
   }

   public static MobEventEffect createFromJSON(JsonObject json) {
      String type = json.get("type").getAsString();
      MobEventEffect mobEventEffect = null;
      if ("world".equalsIgnoreCase(type)) {
         mobEventEffect = new WorldMobEventEffect();
      } else if ("structure".equalsIgnoreCase(type)) {
         mobEventEffect = new StructureMobEventEffect();
      } else if ("command".equalsIgnoreCase(type)) {
         mobEventEffect = new CommandMobEventEffect();
      }

      mobEventEffect.loadFromJSON(json);
      return mobEventEffect;
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("eventTicksMin")) {
         this.eventTicksMin = json.get("eventTicksMin").getAsDouble();
      }

      if (json.has("eventTicksMax")) {
         this.eventTicksMax = json.get("eventTicksMax").getAsDouble();
      }

      if (json.has("eventTicksN")) {
         this.eventTicksN = json.get("eventTicksN").getAsDouble();
      }

   }

   public boolean canActivate(World world, EntityPlayer player, BlockPos pos, int level, int ticks) {
      if (this.eventTicksMin >= 0.0D && (double)ticks < this.eventTicksMin) {
         return false;
      } else if (this.eventTicksMax >= 0.0D && (double)ticks > this.eventTicksMax) {
         return false;
      } else {
         return this.eventTicksN < 0.0D || (double)ticks % this.eventTicksN == 0.0D;
      }
   }

   public void onUpdate(World world, EntityPlayer player, BlockPos pos, int level, int ticks, int variant) {
   }

   public void onSpawn(EntityLiving entity, World world, EntityPlayer player, BlockPos pos, int level, int ticks) {
   }
}
