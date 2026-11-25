package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.helpers.JSONHelper;
import java.util.HashSet;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public abstract class DungeonPlacer {
   public DungeonPlacer() {
   }

   public static DungeonPlacer createFromJson(JsonObject json) {
      String type = json.get("type").getAsString();
      DungeonPlacer spawnCondition = null;
      if ("random".equalsIgnoreCase(type)) {
         spawnCondition = new DungeonPlacer.RandomPlacer();
      } else if ("surface".equalsIgnoreCase(type)) {
         spawnCondition = new DungeonPlacer.SurfacePlacer();
      }

      spawnCondition.loadFromJSON(json);
      return spawnCondition;
   }

   public void loadFromJSON(JsonObject json) {
   }

   public abstract BlockPos calculatePosition(World var1, BlockPos var2, EaglercraftRandom var3);

   public static class RandomPlacer extends DungeonPlacer {
      private int minY = 64;
      private int maxY = 64;

      public RandomPlacer() {
      }

      public void loadFromJSON(JsonObject json) {
         if (json.has("minY")) {
            this.minY = json.get("minY").getAsInt();
         }

         if (json.has("maxY")) {
            this.maxY = json.get("maxY").getAsInt();
         }

      }

      public BlockPos calculatePosition(World world, BlockPos pos, EaglercraftRandom random) {
         int y = this.minY;
         if (this.minY < this.maxY) {
            y += random.nextInt(this.maxY - this.minY);
         }

         return new BlockPos(pos.getX(), y, pos.getZ());
      }
   }

   public static class SurfacePlacer extends DungeonPlacer {
      private Set<Material> materialsToIgnore = (Set)Stream.of(Material.AIR, Material.WOOD, Material.LEAVES, Material.PLANTS, Material.VINE, Material.SNOW, Material.CACTUS).collect(Collectors.toSet());

      public SurfacePlacer() {
      }

      public void loadFromJSON(JsonObject json) {
         this.materialsToIgnore = new HashSet(JSONHelper.getJsonMaterials(json));
      }

      public BlockPos calculatePosition(World world, BlockPos pos, EaglercraftRandom random) {
         Chunk chunk = world.getChunkFromBlockCoords(pos);
         MutableBlockPos mutablePos = new MutableBlockPos(pos.getX(), chunk.getTopFilledSegment() + 15, pos.getZ());

         for(Material material = chunk.getBlockState(mutablePos).getMaterial(); mutablePos.getY() > 0 && this.materialsToIgnore.contains(material); material = chunk.getBlockState(mutablePos).getMaterial()) {
            mutablePos.setY(mutablePos.getY() - 1);
         }

         return new BlockPos(pos.getX(), mutablePos.getY(), pos.getZ());
      }
   }
}
