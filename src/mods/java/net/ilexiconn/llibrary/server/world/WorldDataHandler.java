package net.ilexiconn.llibrary.server.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.ISaveHandler;

public enum WorldDataHandler {
   INSTANCE;

   private List<IWorldDataAdapter> dataAdapterList = new ArrayList();

   private WorldDataHandler() {
   }

   public void registerDataAdapter(IWorldDataAdapter dataAdapter) {
      this.dataAdapterList.add(dataAdapter);
   }

   public void loadWorldData(ISaveHandler saveHandler, World world) {
      if (world.provider.getDimension() == 0) {
         for(IWorldDataAdapter dataAdapter : this.dataAdapterList) {
            File dataFile = new File(this.getSaveFolder(saveHandler, world), dataAdapter.getID() + ".dat");
            if (dataFile.exists()) {
               try {
                  FileInputStream inputStream = new FileInputStream(dataFile);
                  dataAdapter.loadNBTData(CompressedStreamTools.readCompressed(inputStream), world);
                  inputStream.close();
               } catch (IOException var7) {
                  LLibrary.LOGGER.error("Failed to read file", var7);
               }
            }
         }
      }

   }

   public void saveWorldData(ISaveHandler saveHandler, World world) {
      if (world.provider.getDimension() == 0) {
         for(IWorldDataAdapter dataAdapter : this.dataAdapterList) {
            File dataFile = new File(this.getSaveFolder(saveHandler, world), dataAdapter.getID() + ".dat");
            if (!dataFile.exists()) {
               try {
                  dataFile.createNewFile();
               } catch (IOException var9) {
                  LLibrary.LOGGER.error("Failed to create file {}", dataFile.getName(), var9);
                  continue;
               }

               NBTTagCompound compound = new NBTTagCompound();
               dataAdapter.saveNBTData(compound, world);

               try {
                  FileOutputStream outputStream = new FileOutputStream(dataFile);
                  CompressedStreamTools.writeCompressed(compound, outputStream);
                  outputStream.close();
               } catch (IOException var8) {
                  LLibrary.LOGGER.error("Failed to write file {}", dataFile.getName(), var8);
               }
            }
         }
      }

   }

   private File getSaveFolder(ISaveHandler saveHandler, World world) {
      IChunkLoader chunkLoader = saveHandler.getChunkLoader(world.provider);
      File worldFolder;
      if (chunkLoader instanceof AnvilChunkLoader) {
         worldFolder = ((AnvilChunkLoader)chunkLoader).chunkSaveLocation;
      } else {
         worldFolder = saveHandler.getWorldDirectory();
      }

      File saveFolder = new File(worldFolder, "llibrary");
      if (!saveFolder.exists()) {
         saveFolder.mkdirs();
      }

      return saveFolder;
   }
}
