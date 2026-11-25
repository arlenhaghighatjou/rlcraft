package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.spawner.CoordSorterFurthest;
import com.lycanitesmobs.core.spawner.CoordSorterNearest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class SpawnLocation {
   public Vec3i rangeMin = new Vec3i(0, 0, 0);
   public Vec3i rangeMax = new Vec3i(0, 0, 0);
   public int yMin = -1;
   public int yMax = -1;
   public String sorting = "difficulty";

   public SpawnLocation() {
   }

   public static SpawnLocation createFromJSON(JsonObject json) {
      String type = json.get("type").getAsString();
      SpawnLocation spawnLocation = null;
      if ("base".equalsIgnoreCase(type)) {
         spawnLocation = new SpawnLocation();
      } else if ("random".equalsIgnoreCase(type)) {
         spawnLocation = new RandomSpawnLocation();
      } else if ("block".equalsIgnoreCase(type)) {
         spawnLocation = new BlockSpawnLocation();
      } else if ("material".equalsIgnoreCase(type)) {
         spawnLocation = new MaterialSpawnLocation();
      } else if ("village".equalsIgnoreCase(type)) {
         spawnLocation = new VillageSpawnLocation();
      } else if ("structure".equalsIgnoreCase(type)) {
         spawnLocation = new StructureSpawnLocation();
      }

      spawnLocation.loadFromJSON(json);
      return spawnLocation;
   }

   public void loadFromJSON(JsonObject json) {
      this.rangeMin = JSONHelper.getVec3i(json, "rangeMin");
      this.rangeMax = JSONHelper.getVec3i(json, "rangeMax");
      if (json.has("yMin")) {
         this.yMin = json.get("yMin").getAsInt();
      }

      if (json.has("yMax")) {
         this.yMax = json.get("yMax").getAsInt();
      }

      if (json.has("sorting")) {
         this.sorting = json.get("sorting").getAsString();
      }

   }

   public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
      List<BlockPos> spawnPositions = new ArrayList();
      int yPos = this.getOffset(world.rand, this.rangeMin.getY(), this.rangeMax.getY());
      if ((this.yMax < 0 || yPos <= this.yMax) && (this.yMin < 0 || yPos >= this.yMin)) {
         Vec3i offset = new Vec3i(this.getOffset(world.rand, this.rangeMin.getX(), this.rangeMax.getX()), yPos, this.getOffset(world.rand, this.rangeMin.getZ(), this.rangeMax.getZ()));
         spawnPositions.add(triggerPos.add(offset));
      }

      return this.sortSpawnPositions(spawnPositions, world, triggerPos);
   }

   public int getOffset(EaglercraftRandom random, int min, int max) {
      if (max <= min) {
         return 0;
      } else {
         int offset = min + random.nextInt(max - min);
         if (random.nextBoolean()) {
            offset = -offset;
         }

         return offset;
      }
   }

   public List<BlockPos> sortSpawnPositions(List<BlockPos> spawnPositions, World world, BlockPos triggerPos) {
      String sorting = this.sorting;
      if ("difficulty".equalsIgnoreCase(this.sorting)) {
         sorting = "random";
         if (world.getDifficulty().getDifficultyId() <= 1) {
            sorting = "far";
         } else if (world.getDifficulty().getDifficultyId() >= 3) {
            sorting = "near";
         }
      }

      if ("random".equalsIgnoreCase(sorting)) {
         Collections.shuffle(spawnPositions);
      } else if ("near".equalsIgnoreCase(sorting)) {
         Collections.sort(spawnPositions, new CoordSorterNearest(triggerPos));
      } else if ("far".equalsIgnoreCase(sorting)) {
         Collections.sort(spawnPositions, new CoordSorterFurthest(triggerPos));
      }

      return spawnPositions;
   }
}
