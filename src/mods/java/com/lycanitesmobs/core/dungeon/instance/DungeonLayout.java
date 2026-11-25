package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import com.lycanitesmobs.core.worldgen.WorldGeneratorDungeon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;

public class DungeonLayout {
   public DungeonInstance dungeonInstance;
   public List<SectorInstance> sectors = new ArrayList();
   public Map<ChunkPos, List<SectorInstance>> sectorChunkMap = new HashMap();
   public SectorConnector originConnector;
   List<SectorConnector> openConnectors = new ArrayList();

   public DungeonLayout(DungeonInstance dungeonInstance) {
      this.dungeonInstance = dungeonInstance;
   }

   public void generate(Random random) {
      this.sectors.clear();
      this.sectorChunkMap.clear();
      this.openConnectors.clear();
      SectorInstance entranceSector = this.start(random);
      LycanitesMobs.logDebug("Dungeon", "Created Entrance Sector: " + entranceSector);
      this.openConnectors.clear();
      SectorInstance exitSector = entranceSector;
      int level = 1;

      for(boolean onLastLevel = false; !onLastLevel && level <= 10; ++level) {
         int sectorCount = this.dungeonInstance.schematic.getRandomSectorCount(random);
         LycanitesMobs.logDebug("Dungeon", "Starting Underground Level -" + level + " - Sector Count: " + sectorCount);
         int snakeCount = Math.round((float)sectorCount * 0.4F);
         exitSector = this.snake(random, exitSector, Math.max(3, snakeCount));
         LycanitesMobs.logDebug("Dungeon", "Snake Sectors: " + snakeCount + " - From Sector: " + exitSector);
         if (exitSector == null) {
            onLastLevel = true;
         }

         sectorCount = sectorCount - snakeCount;
         LycanitesMobs.logDebug("Dungeon", "Stem Sectors: " + sectorCount + " Open Snake Sectors: " + this.openConnectors.size());

         while(sectorCount > 0) {
            int stemmedSectors = this.stem(random, sectorCount).size();
            if (stemmedSectors == 0) {
               LycanitesMobs.logWarning("Dungeon", "Unable to stem any sectors.");
               break;
            }

            sectorCount -= stemmedSectors;
         }

         this.openConnectors.clear();
         LycanitesMobs.logDebug("Dungeon", "Completed Underground Level -" + level + (onLastLevel ? " (Final)" : ""));
      }

      if (this.dungeonInstance.schematic.canGenerateAsTower && random.nextDouble() <= WorldGeneratorDungeon.TOWER_CHANCE) {
         exitSector = entranceSector;

         for(int var10 = 1; var10 <= 10; ++var10) {
            LycanitesMobs.logDebug("Dungeon", "Starting Tower Level " + var10);
            exitSector = this.tower(random, exitSector);
            LycanitesMobs.logDebug("Dungeon", "Tower Sector: " + exitSector);
            if (exitSector == null) {
               break;
            }

            int ledgeCount = random.nextInt(exitSector.getOpenConnectors((SectorInstance)null).size()) + 1;
            LycanitesMobs.logDebug("Dungeon", "Ledges: " + ledgeCount);
            this.ledge(random, exitSector, ledgeCount);
            this.openConnectors.clear();
            LycanitesMobs.logDebug("Dungeon", "Completed Tower Level " + var10);
         }
      }

      LycanitesMobs.logDebug("Dungeon", "Dungeon Instance Generation Complete!");
   }

   public SectorInstance start(Random random) {
      this.originConnector = new SectorConnector(this.dungeonInstance.originPos, (SectorInstance)null, 0, EnumFacing.HORIZONTALS[random.nextInt(EnumFacing.HORIZONTALS.length)]);
      DungeonSector entranceDungeonSector = this.dungeonInstance.schematic.getRandomSector("entrance", random);
      SectorInstance entranceSector = new SectorInstance(this, entranceDungeonSector, random);
      entranceSector.connect(this.originConnector);
      entranceSector.init(random);
      this.addSectorInstance(entranceSector);
      DungeonSector dungeonSector = this.dungeonInstance.schematic.getRandomSector("stairs", random);
      SectorInstance sectorInstance = new SectorInstance(this, dungeonSector, random);
      sectorInstance.connect(entranceSector.getRandomConnector(random, sectorInstance));
      sectorInstance.init(random);
      this.addSectorInstance(sectorInstance);
      return sectorInstance;
   }

   public SectorInstance snake(Random random, SectorInstance startSector, int length) {
      SectorInstance lastSector = startSector;

      for(int i = 0; i < length; ++i) {
         String nextType = this.dungeonInstance.schematic.getNextConnectingSector(lastSector.dungeonSector.type, random);
         if (i == length - 2) {
            nextType = "bossRoom";
         } else if (i == length - 1) {
            nextType = "stairs";
         }

         DungeonSector dungeonSector = this.dungeonInstance.schematic.getRandomSector(nextType, random);
         SectorInstance sectorInstance = new SectorInstance(this, dungeonSector, random);
         SectorConnector sectorConnector = lastSector.getRandomConnector(random, sectorInstance);
         sectorInstance.connect(sectorConnector);
         if ("stairs".equals(nextType) && sectorInstance.getOccupiedBoundsMin().getY() <= 1) {
            nextType = "finish";
            dungeonSector = this.dungeonInstance.schematic.getRandomSector(nextType, random);
            sectorInstance = new SectorInstance(this, dungeonSector, random);
            sectorInstance.connect(sectorConnector);
         }

         sectorInstance.init(random);
         this.addSectorInstance(sectorInstance);
         lastSector = sectorInstance;
         if ("finish".equalsIgnoreCase(nextType)) {
            lastSector = null;
         }
      }

      return lastSector;
   }

   public List<SectorInstance> stem(Random random, int maxSectors) {
      List<SectorInstance> generatedSectors = new ArrayList();
      int stemmedSectors = 0;

      for(SectorConnector connector : (SectorConnector[])this.openConnectors.toArray(new SectorConnector[this.openConnectors.size()])) {
         String nextType = this.dungeonInstance.schematic.getNextConnectingSector(connector.parentSector.dungeonSector.type, random);
         DungeonSector dungeonSector = this.dungeonInstance.schematic.getRandomSector(nextType, random);
         SectorInstance sectorInstance = new SectorInstance(this, dungeonSector, random);
         if (connector.canConnect(this, sectorInstance)) {
            sectorInstance.connect(connector);
            sectorInstance.init(random);
            this.addSectorInstance(sectorInstance);
            generatedSectors.add(sectorInstance);
            if (this.openConnectors.contains(connector)) {
               this.openConnectors.remove(connector);
            }

            connector.closed = true;
            ++stemmedSectors;
            if (stemmedSectors >= maxSectors) {
               break;
            }
         }
      }

      return generatedSectors;
   }

   public SectorInstance tower(Random random, SectorInstance startSector) {
      DungeonSector dungeonSector = this.dungeonInstance.schematic.getRandomSector("tower", random);
      SectorInstance sectorInstance = new SectorInstance(this, dungeonSector, random);
      SectorConnector sectorConnector = startSector.getRandomConnector(random, sectorInstance);
      sectorInstance.connect(sectorConnector);
      if (sectorInstance.getOccupiedBoundsMax().getY() > 255) {
         return null;
      } else {
         sectorInstance.init(random);
         this.addSectorInstance(sectorInstance);
         return sectorInstance;
      }
   }

   public SectorInstance ledge(Random random, SectorInstance startSector, int ledgeCount) {
      SectorInstance lastSectorInstance = null;
      int bossIndex = 0;
      if (ledgeCount > 0) {
         bossIndex = random.nextInt(ledgeCount);
      }

      for(int i = 0; i < ledgeCount; ++i) {
         DungeonSector corridorSector = this.dungeonInstance.schematic.getRandomSector("corridor", random);
         SectorInstance corridorInstance = new SectorInstance(this, corridorSector, random);
         SectorConnector corridorConnector = startSector.getRandomConnector(random, corridorInstance);
         if (corridorConnector == null) {
            break;
         }

         corridorInstance.connect(corridorConnector);
         corridorInstance.init(random);
         this.addSectorInstance(corridorInstance);
         String roomType = "room";
         if (bossIndex == i) {
            roomType = "bossRoom";
         }

         DungeonSector roomSector = this.dungeonInstance.schematic.getRandomSector(roomType, random);
         SectorInstance roomInstance = new SectorInstance(this, roomSector, random);
         SectorConnector roomConnector = corridorInstance.getRandomConnector(random, roomInstance);
         roomInstance.connect(roomConnector);
         roomInstance.init(random);
         this.addSectorInstance(roomInstance);
         lastSectorInstance = roomInstance;
      }

      return lastSectorInstance;
   }

   public void addSectorInstance(SectorInstance sectorInstance) {
      this.sectors.add(sectorInstance);
      ChunkPos minChunkPos = new ChunkPos(sectorInstance.getOccupiedBoundsMin());
      if (this.dungeonInstance.chunkMin == null) {
         this.dungeonInstance.chunkMin = minChunkPos;
      } else {
         if (minChunkPos.x < this.dungeonInstance.chunkMin.x) {
            this.dungeonInstance.chunkMin = new ChunkPos(minChunkPos.x, this.dungeonInstance.chunkMin.z);
         }

         if (minChunkPos.z < this.dungeonInstance.chunkMin.z) {
            this.dungeonInstance.chunkMin = new ChunkPos(this.dungeonInstance.chunkMin.x, minChunkPos.z);
         }
      }

      ChunkPos maxChunkPos = new ChunkPos(sectorInstance.getOccupiedBoundsMax());
      if (this.dungeonInstance.chunkMax == null) {
         this.dungeonInstance.chunkMax = maxChunkPos;
      } else {
         if (maxChunkPos.x > this.dungeonInstance.chunkMax.x) {
            this.dungeonInstance.chunkMax = new ChunkPos(maxChunkPos.x, this.dungeonInstance.chunkMax.z);
         }

         if (maxChunkPos.z > this.dungeonInstance.chunkMax.z) {
            this.dungeonInstance.chunkMax = new ChunkPos(this.dungeonInstance.chunkMax.x, maxChunkPos.z);
         }
      }

      for(ChunkPos chunkPos : sectorInstance.getChunkPositions()) {
         if (!this.sectorChunkMap.containsKey(chunkPos)) {
            this.sectorChunkMap.put(chunkPos, new ArrayList());
         }

         ((List)this.sectorChunkMap.get(chunkPos)).add(sectorInstance);
      }

      if (!"stairs".equalsIgnoreCase(sectorInstance.dungeonSector.type)) {
         this.openConnectors.addAll(sectorInstance.getOpenConnectors((SectorInstance)null));
      }

   }
}
