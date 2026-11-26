package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.dungeon.definition.DungeonSchematic;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class DungeonInstance {
   public EaglercraftUUID uuid;
   public DungeonSchematic schematic;
   public boolean complete = false;
   public int chunksBuilt = 0;
   public BlockPos originPos;
   public ChunkPos chunkMin;
   public ChunkPos chunkMax;
   public World world;
   long seed = 0L;
   public EaglercraftRandom random;
   public DungeonLayout layout;

   public DungeonInstance() {
   }

   public void setOrigin(BlockPos blockPos) {
      this.originPos = blockPos;
   }

   public boolean init(World world) {
      this.world = world;
      if (this.complete) {
         return true;
      } else if (this.world != null && this.originPos != null) {
         if (this.schematic == null) {
            List<DungeonSchematic> schematics = new ArrayList();

            for(DungeonSchematic schematic : DungeonManager.getInstance().schematics.values()) {
               if (schematic.canBuild(world, this.originPos)) {
                  schematics.add(schematic);
               }
            }

            if (schematics.isEmpty()) {
               return false;
            }

            if (schematics.size() == 1) {
               this.schematic = (DungeonSchematic)schematics.get(0);
            } else {
               this.schematic = (DungeonSchematic)schematics.get(this.world.rand.nextInt(schematics.size()));
            }
         }

         if (this.seed == 0L) {
            this.seed = world.rand.nextLong();
         }

         this.random = new EaglercraftRandom(this.seed);
         this.originPos = this.schematic.placer.calculatePosition(world, this.originPos, this.random);
         LycanitesMobs.logDebug("Dungeon", "Starting Dungeon Instance Generation For " + this);
         if (this.layout == null) {
            this.layout = new DungeonLayout(this);
            this.layout.generate(this.random);
         }

         LycanitesMobs.logInfo("Dungeon", "Generated New Dungeon Instance " + this);
         ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
         if (extendedWorld != null) {
            extendedWorld.markDirty();
         }

         return true;
      } else {
         LycanitesMobs.logWarning("Dungeon", "Tried to initialise a dungeon with a missing world or origin. " + this);
         return false;
      }
   }

   public boolean isChunkPosWithin(ChunkPos chunkPos, int padding) {
      if (chunkPos.x >= this.chunkMin.x - padding && chunkPos.x <= this.chunkMax.x + padding) {
         return chunkPos.z >= this.chunkMin.z - padding && chunkPos.z <= this.chunkMax.z + padding;
      } else {
         return false;
      }
   }

   public void buildChunk(World world, ChunkPos chunkPos) {
      if (!this.complete && this.layout != null && this.layout.sectorChunkMap.containsKey(chunkPos)) {
         for(SectorInstance sectorInstance : (List<SectorInstance>)this.layout.sectorChunkMap.get(chunkPos)) {
            sectorInstance.build(world, chunkPos, world.rand);
         }

         if (++this.chunksBuilt >= this.layout.sectorChunkMap.size()) {
            this.complete = true;
         }

      }
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      this.uuid = nbtTagCompound.getUniqueId("Id");
      this.schematic = DungeonManager.getInstance().getSchematic(nbtTagCompound.getString("Schematic"));
      this.seed = nbtTagCompound.getLong("Seed");
      this.complete = nbtTagCompound.getBoolean("Complete");
      this.chunksBuilt = nbtTagCompound.getInteger("ChunksBuilt");
      if (this.schematic == null) {
         this.complete = true;
      }

      int[] originPos = nbtTagCompound.getIntArray("OriginPos");
      this.originPos = new BlockPos(originPos[0], originPos[1], originPos[2]);
      int[] chunkMin = nbtTagCompound.getIntArray("ChunkMin");
      this.chunkMin = new ChunkPos(chunkMin[0], chunkMin[1]);
      int[] chunkMax = nbtTagCompound.getIntArray("ChunkMax");
      this.chunkMax = new ChunkPos(chunkMax[0], chunkMax[1]);
      LycanitesMobs.logDebug("Dungeon", "Loaded Dungeon Instance from NBT: " + this);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
      if (this.uuid != null && this.schematic != null) {
         nbtTagCompound.setUniqueId("Id", this.uuid);
         nbtTagCompound.setString("Schematic", this.schematic.name);
         nbtTagCompound.setLong("Seed", this.seed);
         nbtTagCompound.setBoolean("Complete", this.complete);
         nbtTagCompound.setInteger("ChunksBuilt", this.chunksBuilt);
         nbtTagCompound.setIntArray("OriginPos", new int[]{this.originPos.getX(), this.originPos.getY(), this.originPos.getZ()});
         nbtTagCompound.setIntArray("ChunkMin", new int[]{this.chunkMin.z, this.chunkMin.z});
         nbtTagCompound.setIntArray("ChunkMax", new int[]{this.chunkMax.z, this.chunkMax.z});
         LycanitesMobs.logDebug("Dungeon", "Saved Dungeon Instance to NBT: " + this);
         return nbtTagCompound;
      } else {
         return null;
      }
   }

   public String toString() {
      String schematic = "";
      if (this.schematic != null) {
         schematic = " - Schematic: " + this.schematic.name;
      }

      String tpCommand = "/tp " + this.originPos.getX() + " " + (this.originPos.getY() + 2) + " " + this.originPos.getZ() + " ";
      return "Dungeon Instance" + schematic + " - TP Command: " + tpCommand + " - Origin: " + this.originPos + " - Complete: " + this.complete + " - Seed: " + this.seed + " - ID: " + this.uuid;
   }
}
