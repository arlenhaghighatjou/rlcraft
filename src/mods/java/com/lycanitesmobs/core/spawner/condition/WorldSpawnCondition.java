package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.helpers.JSONHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class WorldSpawnCondition extends SpawnCondition {
   public int[] dimensionIds;
   public String dimensionListType = "whitelist";
   public String biomeTagListType = "whitelist";
   public List<String> biomeTags = new ArrayList();
   public List<Biome> biomesFromTags = null;
   public String biomeIdListType = "whitelist";
   public List<String> biomeIds = new ArrayList();
   public List<Biome> biomes = new ArrayList();
   public double worldDayMin = -1.0D;
   public double worldDayMax = -1.0D;
   public double worldDayN = -1.0D;
   public int dayTimeMin = -1;
   public int dayTimeMax = -1;
   public String weather = "any";
   public short difficultyMin = -1;
   public short difficultyMax = -1;
   public int moonPhase = -1;

   public WorldSpawnCondition() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("dimensionIds")) {
         JsonArray jsonArray = json.get("dimensionIds").getAsJsonArray();
         this.dimensionIds = new int[jsonArray.size()];
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         for(int i = 0; jsonIterator.hasNext(); ++i) {
            this.dimensionIds[i] = ((JsonElement)jsonIterator.next()).getAsInt();
         }
      }

      if (json.has("dimensionListType")) {
         this.dimensionListType = json.get("dimensionListType").getAsString();
      }

      if (json.has("biomeTags")) {
         this.biomeTags.clear();
         this.biomesFromTags = null;
         this.biomeTags = JSONHelper.getJsonStrings(json.get("biomeTags").getAsJsonArray());
      } else if (json.has("biomes")) {
         this.biomeTags.clear();
         this.biomesFromTags = null;
         this.biomeTags = JSONHelper.getJsonStrings(json.get("biomes").getAsJsonArray());
      }

      if (json.has("biomeTagListType")) {
         this.biomeTagListType = json.get("biomeTagListType").getAsString();
      }

      if (json.has("biomeIds")) {
         this.biomeIds.clear();
         this.biomes = null;
         this.biomeIds = JSONHelper.getJsonStrings(json.get("biomeIds").getAsJsonArray());
      }

      if (json.has("biomeIdListType")) {
         this.biomeIdListType = json.get("biomeIdListType").getAsString();
      }

      if (json.has("worldDayMin")) {
         this.worldDayMin = (double)json.get("worldDayMin").getAsInt();
      }

      if (json.has("worldDayMax")) {
         this.worldDayMax = (double)json.get("worldDayMax").getAsInt();
      }

      if (json.has("worldDayN")) {
         this.worldDayN = (double)json.get("worldDayN").getAsInt();
      }

      if (json.has("dayTimeMin")) {
         this.dayTimeMin = json.get("dayTimeMin").getAsInt();
      }

      if (json.has("dayTimeMax")) {
         this.dayTimeMax = json.get("dayTimeMax").getAsInt();
      }

      if (json.has("weather")) {
         this.weather = json.get("weather").getAsString();
      }

      if (json.has("difficultyMin")) {
         this.difficultyMin = json.get("difficultyMin").getAsShort();
      }

      if (json.has("difficultyMax")) {
         this.difficultyMax = json.get("difficultyMax").getAsShort();
      }

      if (json.has("moonPhase")) {
         this.moonPhase = json.get("moonPhase").getAsInt();
      }

      super.loadFromJSON(json);
   }

   public boolean isMet(World world, EntityPlayer player, BlockPos position) {
      ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
      int time = (int)Math.floor((double)world.getWorldTime() % 24000.0D);
      int day = (int)Math.floor((double)(worldExt.useTotalWorldTime ? world.getTotalWorldTime() : world.getWorldTime()) / 23999.0D);
      if (this.dimensionIds != null && this.dimensionIds.length > 0) {
         boolean dimensionIdFound = false;

         for(int dimensionId : this.dimensionIds) {
            if (world.provider.getDimension() == dimensionId) {
               dimensionIdFound = true;
               break;
            }
         }

         if (!dimensionIdFound && "whitelist".equalsIgnoreCase(this.dimensionListType)) {
            return false;
         }

         if (dimensionIdFound && "blacklist".equalsIgnoreCase(this.dimensionListType)) {
            return false;
         }
      }

      if (this.worldDayMin >= 0.0D && (double)day < this.worldDayMin) {
         return false;
      } else if (this.worldDayMax >= 0.0D && (double)day > this.worldDayMax) {
         return false;
      } else if (this.worldDayN < 0.0D || day != 0 && (double)day % this.worldDayN == 0.0D) {
         if (this.dayTimeMin >= 0 && time < this.dayTimeMin) {
            return false;
         } else if (this.dayTimeMax >= 0 && time > this.dayTimeMax) {
            return false;
         } else if (!"clear".equalsIgnoreCase(this.weather) || !world.isRaining() && !world.isThundering()) {
            if (!"rain".equalsIgnoreCase(this.weather) || world.isRaining() && !world.isThundering()) {
               if ("storm".equalsIgnoreCase(this.weather) && !world.isThundering()) {
                  return false;
               } else if (!"rainstorm".equalsIgnoreCase(this.weather) || world.isRaining() && world.isThundering()) {
                  if ("notclear".equalsIgnoreCase(this.weather) && !world.isRaining() && !world.isThundering()) {
                     return false;
                  } else if (this.difficultyMin >= 0 && world.getDifficulty().getDifficultyId() < this.difficultyMin) {
                     return false;
                  } else if (this.difficultyMax >= 0 && world.getDifficulty().getDifficultyId() > this.difficultyMax) {
                     return false;
                  } else if (this.moonPhase >= 0 && world.provider.getMoonPhase(world.getWorldTime()) != this.moonPhase) {
                     return false;
                  } else {
                     return !this.isAllowedBiome(world, position) ? false : super.isMet(world, player, position);
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isAllowedBiome(World world, BlockPos position) {
      if (position != null) {
         Biome biome = world.getBiome(position);
         if (!this.biomeIds.isEmpty()) {
            if (this.biomes == null) {
               this.biomes = JSONHelper.getBiomes(this.biomeIds);
            }

            if (this.biomes.contains(biome)) {
               return !"blacklist".equalsIgnoreCase(this.biomeIdListType);
            }
         }

         if (!this.biomeTags.isEmpty()) {
            if (this.biomesFromTags == null) {
               this.biomesFromTags = JSONHelper.getBiomesFromTags(this.biomeTags);
            }

            if (this.biomesFromTags.contains(biome)) {
               return !"blacklist".equalsIgnoreCase(this.biomeTagListType);
            }
         }
      }

      if (this.biomeIds.isEmpty() && this.biomeTags.isEmpty()) {
         return true;
      } else {
         return "blacklist".equalsIgnoreCase(this.biomeIdListType) && "blacklist".equalsIgnoreCase(this.biomeTagListType);
      }
   }
}
