package com.creativemd.creativecore.common.world;

import java.io.File;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class SaveHandlerFake implements ISaveHandler {
   public WorldInfo info;

   public SaveHandlerFake(WorldInfo info) {
      this.info = info;
   }

   public WorldInfo loadWorldInfo() {
      return this.info;
   }

   public void checkSessionLock() throws MinecraftException {
   }

   public IChunkLoader getChunkLoader(WorldProvider provider) {
      return new IChunkLoader() {
         public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {
         }

         public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {
         }

         public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
            return new Chunk(worldIn, x, z);
         }

         public void chunkTick() {
         }

         public boolean isChunkGeneratedAt(int x, int z) {
            return true;
         }

         public void flush() {
         }
      };
   }

   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
   }

   public void saveWorldInfo(WorldInfo worldInformation) {
   }

   public IPlayerFileData getPlayerNBTManager() {
      return new IPlayerFileData() {
         public void writePlayerData(EntityPlayer player) {
         }

         public NBTTagCompound readPlayerData(EntityPlayer player) {
            return new NBTTagCompound();
         }

         public String[] getAvailablePlayerDat() {
            return new String[0];
         }
      };
   }

   public void flush() {
   }

   public File getWorldDirectory() {
      return null;
   }

   public File getMapFileFromName(String mapName) {
      return null;
   }

   public TemplateManager getStructureTemplateManager() {
      return null;
   }
}
