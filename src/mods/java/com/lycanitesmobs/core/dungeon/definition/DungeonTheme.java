package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.dungeon.instance.SectorInstance;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import org.apache.commons.lang3.math.NumberUtils;

public class DungeonTheme {
   public String name = "";
   public String floorPattern = "random";
   public String wallPattern = "random";
   public String ceilingPattern = "random";
   public List<ThemeBlock> floorBlocks = new ArrayList();
   public List<ThemeBlock> wallBlocks = new ArrayList();
   public List<ThemeBlock> ceilingBlocks = new ArrayList();
   public List<ThemeBlock> lightBlocks = new ArrayList();
   public List<ThemeBlock> torchBlocks = new ArrayList();
   public List<ThemeBlock> stairBlocks = new ArrayList();
   public List<ThemeBlock> pitBlocks = new ArrayList();
   public List<ThemeBlock> airBlocks = new ArrayList();
   public boolean fillEntranceAndAboveWithAir = true;

   public DungeonTheme() {
   }

   public void loadFromJSON(JsonObject json) {
      this.name = json.get("name").getAsString().toLowerCase();
      if (json.has("floorPattern")) {
         this.floorPattern = json.get("floorPattern").getAsString().toLowerCase();
      }

      if (json.has("wallPattern")) {
         this.wallPattern = json.get("wallPattern").getAsString().toLowerCase();
      }

      if (json.has("ceilingPattern")) {
         this.ceilingPattern = json.get("ceilingPattern").getAsString().toLowerCase();
      }

      if (json.has("floorBlocks")) {
         for(JsonElement jsonElement : json.get("floorBlocks").getAsJsonArray()) {
            ThemeBlock themeBlock = new ThemeBlock();
            themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
            this.floorBlocks.add(themeBlock);
         }
      }

      if (json.has("wallBlocks")) {
         for(JsonElement jsonElement : json.get("wallBlocks").getAsJsonArray()) {
            ThemeBlock themeBlock = new ThemeBlock();
            themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
            this.wallBlocks.add(themeBlock);
         }
      }

      if (json.has("ceilingBlocks")) {
         for(JsonElement jsonElement : json.get("ceilingBlocks").getAsJsonArray()) {
            ThemeBlock themeBlock = new ThemeBlock();
            themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
            this.ceilingBlocks.add(themeBlock);
         }
      }

      if (json.has("lightBlocks")) {
         for(JsonElement jsonElement : json.get("lightBlocks").getAsJsonArray()) {
            ThemeBlock themeBlock = new ThemeBlock();
            themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
            this.lightBlocks.add(themeBlock);
         }
      } else {
         this.lightBlocks.addAll(this.wallBlocks);
      }

      if (json.has("torchBlocks")) {
         for(JsonElement jsonElement : json.get("torchBlocks").getAsJsonArray()) {
            ThemeBlock themeBlock = new ThemeBlock();
            themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
            this.torchBlocks.add(themeBlock);
         }
      } else {
         ThemeBlock themeBlock = new ThemeBlock();
         themeBlock.block = Blocks.AIR;
         this.torchBlocks.add(themeBlock);
      }

      if (json.has("stairBlocks")) {
         for(JsonElement jsonElement : json.get("stairBlocks").getAsJsonArray()) {
            ThemeBlock themeBlock = new ThemeBlock();
            themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
            this.stairBlocks.add(themeBlock);
         }
      }

      if (json.has("pitBlocks")) {
         for(JsonElement jsonElement : json.get("pitBlocks").getAsJsonArray()) {
            ThemeBlock themeBlock = new ThemeBlock();
            themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
            this.pitBlocks.add(themeBlock);
         }
      }

      if (json.has("airBlocks")) {
         for(JsonElement jsonElement : json.get("airBlocks").getAsJsonArray()) {
            ThemeBlock themeBlock = new ThemeBlock();
            themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
            this.airBlocks.add(themeBlock);
         }
      } else {
         ThemeBlock themeBlock = new ThemeBlock();
         themeBlock.block = Blocks.AIR;
         this.airBlocks.add(themeBlock);
      }

      if (json.has("fillEntranceAndAboveWithAir")) {
         this.fillEntranceAndAboveWithAir = json.get("fillEntranceAndAboveWithAir").getAsBoolean();
      }

   }

   public IBlockState getFloor(SectorInstance sectorInstance, char patternChar, EaglercraftRandom random) {
      return this.getBlockState(sectorInstance, patternChar, random, this.floorBlocks);
   }

   public IBlockState getWall(SectorInstance sectorInstance, char patternChar, EaglercraftRandom random) {
      return this.getBlockState(sectorInstance, patternChar, random, this.wallBlocks);
   }

   public IBlockState getCeiling(SectorInstance sectorInstance, char patternChar, EaglercraftRandom random) {
      return this.getBlockState(sectorInstance, patternChar, random, this.ceilingBlocks);
   }

   public IBlockState getLight(char patternChar, EaglercraftRandom random) {
      return this.getBlockState((SectorInstance)null, patternChar, random, this.lightBlocks);
   }

   public IBlockState getTorch(char patternChar, EaglercraftRandom random) {
      return this.getBlockState((SectorInstance)null, patternChar, random, this.torchBlocks);
   }

   public IBlockState getStairs(char patternChar, EaglercraftRandom random) {
      return this.getBlockState((SectorInstance)null, patternChar, random, this.stairBlocks);
   }

   public IBlockState getPit(char patternChar, EaglercraftRandom random) {
      return this.getBlockState((SectorInstance)null, patternChar, random, this.pitBlocks);
   }

   public IBlockState getAir(char patternChar, EaglercraftRandom random) {
      return this.getBlockState((SectorInstance)null, patternChar, random, this.airBlocks);
   }

   public IBlockState getBlockState(SectorInstance sectorInstance, char patternChar, EaglercraftRandom random, List<ThemeBlock> blockList) {
      if (patternChar == '0') {
         return null;
      } else {
         if (sectorInstance != null) {
            if (patternChar == 'L') {
               return sectorInstance.lightBlock;
            }

            if (patternChar == 'T') {
               return sectorInstance.torchBlock;
            }

            if (patternChar == 'R') {
               return sectorInstance.stairBlock;
            }

            if (patternChar == 'P') {
               return sectorInstance.pitBlock;
            }

            if (patternChar == 'A') {
               return sectorInstance.airBlock;
            }
         }

         if (patternChar == 'C') {
            return Blocks.CHEST.getDefaultState();
         } else if (patternChar == 'S') {
            return Blocks.MOB_SPAWNER.getStateFromMeta(1);
         } else if (blockList.isEmpty()) {
            return null;
         } else if (blockList.size() == 1) {
            return ((ThemeBlock)blockList.get(0)).getBlockState();
         } else if (NumberUtils.isCreatable("" + patternChar)) {
            int blockIndex = NumberUtils.toInt("" + patternChar, 1) - 1;
            if (blockIndex >= blockList.size()) {
               blockIndex = 0;
            }

            return ((ThemeBlock)blockList.get(blockIndex % blockList.size())).getBlockState();
         } else {
            int totalWeights = 0;

            for(ThemeBlock themeBlock : blockList) {
               if (themeBlock != null && themeBlock.weight >= 1) {
                  totalWeights += themeBlock.weight;
               }
            }

            int randomWeight = random.nextInt(totalWeights) + 1;
            int searchedWeight = 0;

            for(ThemeBlock themeBlock : blockList) {
               if (themeBlock != null && themeBlock.weight >= 1) {
                  if (randomWeight <= themeBlock.weight + searchedWeight) {
                     return themeBlock.getBlockState();
                  }

                  searchedWeight += themeBlock.weight;
               }
            }

            return ((ThemeBlock)blockList.get(blockList.size() - 1)).getBlockState();
         }
      }
   }
}
