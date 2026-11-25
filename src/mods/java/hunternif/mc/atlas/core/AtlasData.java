package hunternif.mc.atlas.core;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MapDataPacket;
import hunternif.mc.atlas.util.IntVec2;
import hunternif.mc.atlas.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldSavedData;

public class AtlasData extends WorldSavedData {
   public static final int VERSION = 3;
   public static final String TAG_VERSION = "aaVersion";
   public static final String TAG_DIMENSION_MAP_LIST = "qDimensionMap";
   public static final String TAG_DIMENSION_ID = "qDimensionID";
   public static final String TAG_VISITED_CHUNKS = "qVisitedChunks";
   public static final String TAG_BROWSING_X = "qBrowseX";
   public static final String TAG_BROWSING_Y = "qBrowseY";
   public static final String TAG_BROWSING_ZOOM = "qBrowseZoom";
   private final Map<Integer, IBiomeDetector> biomeAnalyzers = new HashMap();
   private final BiomeDetectorBase biomeDetectorOverworld = new BiomeDetectorBase();
   private final BiomeDetectorNether biomeDetectorNether = new BiomeDetectorNether();
   private final BiomeDetectorEnd biomeDetectorEnd = new BiomeDetectorEnd();
   private final Map<Integer, DimensionData> dimensionMap = new ConcurrentHashMap(2, 0.75F, 2);
   private final Set<EntityPlayer> playersSentTo = new HashSet();
   private NBTTagCompound nbt;

   public AtlasData(String key) {
      super(key);
      this.biomeDetectorOverworld.setScanPonds(SettingsConfig.performance.doScanPonds);
      this.biomeDetectorOverworld.setScanRavines(SettingsConfig.performance.doScanRavines);
      this.setBiomeDetectorForDimension(0, this.biomeDetectorOverworld);
      this.setBiomeDetectorForDimension(-1, this.biomeDetectorNether);
      this.setBiomeDetectorForDimension(1, this.biomeDetectorEnd);
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.nbt = compound;
      int version = compound.getInteger("aaVersion");
      if (version < 3) {
         Log.warn("Outdated atlas data format! Was %d but current is %d. Updating.", version, Integer.valueOf(3));
         this.readFromNBT2(compound);
      } else {
         NBTTagList dimensionMapList = compound.getTagList("qDimensionMap", 10);

         for(int d = 0; d < dimensionMapList.tagCount(); ++d) {
            NBTTagCompound dimTag = dimensionMapList.getCompoundTagAt(d);
            int dimensionID = dimTag.getInteger("qDimensionID");
            NBTTagList dimensionTag = (NBTTagList)dimTag.getTag("qVisitedChunks");
            DimensionData dimData = this.getDimensionData(dimensionID);
            dimData.readFromNBT(dimensionTag);
            double zoom = (double)dimTag.getInteger("qBrowseZoom") / 1024.0D;
            if (zoom == 0.0D) {
               zoom = 0.5D;
            }

            dimData.setBrowsingPosition(dimTag.getInteger("qBrowseX"), dimTag.getInteger("qBrowseY"), zoom);
         }

      }
   }

   public void readFromNBT2(NBTTagCompound compound) {
      this.nbt = compound;
      int version = compound.getInteger("aaVersion");
      if (version < 2) {
         Log.warn("Loading map with version 2 failed");
         this.markDirty();
      } else {
         NBTTagList dimensionMapList = compound.getTagList("qDimensionMap", 10);

         for(int d = 0; d < dimensionMapList.tagCount(); ++d) {
            NBTTagCompound dimTag = dimensionMapList.getCompoundTagAt(d);
            int dimensionID = dimTag.getInteger("qDimensionID");
            int[] intArray = dimTag.getIntArray("qVisitedChunks");
            DimensionData dimData = this.getDimensionData(dimensionID);

            for(int i = 0; i < intArray.length; i += 3) {
               if (dimData.getTile(intArray[i], intArray[i + 1]) != null) {
                  Log.warn("Duplicate tile at " + intArray[i] + ", " + intArray[i]);
               }

               dimData.setTile(intArray[i], intArray[i + 1], new Tile(intArray[i + 2]));
            }

            Log.info("Updated " + intArray.length / 3 + " chunks");
            double zoom = (double)dimTag.getInteger("qBrowseZoom") / 1024.0D;
            if (zoom == 0.0D) {
               zoom = 0.5D;
            }

            dimData.setBrowsingPosition(dimTag.getInteger("qBrowseX"), dimTag.getInteger("qBrowseY"), zoom);
         }

      }
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      return this.writeToNBT(compound, true);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound, boolean includeTileData) {
      NBTTagList dimensionMapList = new NBTTagList();
      compound.setInteger("aaVersion", 3);

      for(Entry<Integer, DimensionData> dimensionEntry : this.dimensionMap.entrySet()) {
         NBTTagCompound dimTag = new NBTTagCompound();
         dimTag.setInteger("qDimensionID", ((Integer)dimensionEntry.getKey()).intValue());
         DimensionData dimData = (DimensionData)dimensionEntry.getValue();
         if (includeTileData) {
            dimTag.setTag("qVisitedChunks", dimData.writeToNBT());
         }

         dimTag.setInteger("qBrowseX", dimData.getBrowsingX());
         dimTag.setInteger("qBrowseY", dimData.getBrowsingY());
         dimTag.setInteger("qBrowseZoom", (int)Math.round(dimData.getBrowsingZoom() * 1024.0D));
         dimensionMapList.appendTag(dimTag);
      }

      compound.setTag("qDimensionMap", dimensionMapList);
      return compound;
   }

   private void setBiomeDetectorForDimension(int dimension, IBiomeDetector biomeAnalyzer) {
      this.biomeAnalyzers.put(Integer.valueOf(dimension), biomeAnalyzer);
   }

   private IBiomeDetector getBiomeDetectorForDimension(int dimension) {
      IBiomeDetector biomeAnalyzer = (IBiomeDetector)this.biomeAnalyzers.get(Integer.valueOf(dimension));
      return (IBiomeDetector)(biomeAnalyzer == null ? this.biomeDetectorOverworld : biomeAnalyzer);
   }

   public ArrayList<TileInfo> updateMapAroundPlayer(EntityPlayer player) {
      ArrayList<TileInfo> updatedTiles = new ArrayList();
      int newScanInterval = Math.round(SettingsConfig.performance.newScanInterval * 20.0F);
      int rescanInterval = newScanInterval * SettingsConfig.performance.rescanRate;
      if (player.ticksExisted % newScanInterval != 0) {
         return updatedTiles;
      } else {
         int playerX = MathHelper.floor(player.posX) >> 4;
         int playerZ = MathHelper.floor(player.posZ) >> 4;
         ITileStorage seenChunks = this.getDimensionData(player.dimension);
         IBiomeDetector biomeDetector = this.getBiomeDetectorForDimension(player.dimension);
         int scanRadius = SettingsConfig.performance.scanRadius;
         boolean rescanRequired = SettingsConfig.performance.doRescan && player.ticksExisted % rescanInterval == 0;

         for(double dx = (double)(-scanRadius); dx <= (double)scanRadius; ++dx) {
            for(double dz = (double)(-scanRadius); dz <= (double)scanRadius; ++dz) {
               if (dx * dx + dz * dz <= (double)(scanRadius * scanRadius)) {
                  int x = (int)((double)playerX + dx);
                  int z = (int)((double)playerZ + dz);
                  Tile oldTile = seenChunks.getTile(x, z);
                  int biomeId = AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(player.dimension, x, z);
                  if (biomeId == -1) {
                     if (oldTile == null || rescanRequired) {
                        Chunk chunk = player.getEntityWorld().getChunkProvider().getLoadedChunk(x, z);
                        if (SettingsConfig.performance.forceChunkLoading && chunk == null) {
                           chunk = player.getEntityWorld().getChunkProvider().provideChunk(x, z);
                        }

                        if (chunk != null) {
                           if (oldTile != null) {
                              biomeId = biomeDetector.getBiomeID(chunk);
                              if (biomeId == -1) {
                                 this.removeTile(player.dimension, x, z);
                              } else if (oldTile.biomeID != biomeId) {
                                 this.setTile(player.dimension, x, z, new Tile(biomeId));
                                 updatedTiles.add(new TileInfo(x, z, biomeId));
                              }
                           } else {
                              biomeId = biomeDetector.getBiomeID(chunk);
                              if (biomeId != -1) {
                                 this.setTile(player.dimension, x, z, new Tile(biomeId));
                                 updatedTiles.add(new TileInfo(x, z, biomeId));
                              }
                           }
                        }
                     }
                  } else if (oldTile == null || oldTile.biomeID != biomeId) {
                     this.setTile(player.dimension, x, z, new Tile(biomeId));
                     updatedTiles.add(new TileInfo(x, z, biomeId));
                     this.markDirty();
                  }
               }
            }
         }

         return updatedTiles;
      }
   }

   public void setTile(int dimension, int x, int y, Tile tile) {
      DimensionData dimData = this.getDimensionData(dimension);
      dimData.setTile(x, y, tile);
   }

   private Tile removeTile(int dimension, int x, int y) {
      DimensionData dimData = this.getDimensionData(dimension);
      return dimData.removeTile(x, y);
   }

   public Set<Integer> getVisitedDimensions() {
      return this.dimensionMap.keySet();
   }

   public DimensionData getDimensionData(int dimension) {
      return (DimensionData)this.dimensionMap.computeIfAbsent(Integer.valueOf(dimension), (k) -> {
         return new DimensionData(this, dimension);
      });
   }

   public Map<IntVec2, Tile> getSeenChunksInDimension(int dimension) {
      return this.getDimensionData(dimension).getSeenChunks();
   }

   public Collection<EntityPlayer> getSyncedPlayers() {
      return Collections.unmodifiableCollection(this.playersSentTo);
   }

   public boolean isSyncedOnPlayer(EntityPlayer player) {
      return this.playersSentTo.contains(player);
   }

   public void syncOnPlayer(int atlasID, EntityPlayer player) {
      if (this.nbt == null) {
         this.nbt = new NBTTagCompound();
      }

      this.writeToNBT(this.nbt, false);
      PacketDispatcher.sendTo(new MapDataPacket(atlasID, this.nbt), (EntityPlayerMP)player);

      for(Integer i : this.dimensionMap.keySet()) {
         ((DimensionData)this.dimensionMap.get(i)).syncOnPlayer(atlasID, player);
      }

      Log.info("Sent Atlas #%d data to player %s", atlasID, player.getName());
      this.playersSentTo.add(player);
   }

   public boolean isEmpty() {
      return this.dimensionMap.isEmpty();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof AtlasData)) {
         return false;
      } else {
         AtlasData other = (AtlasData)obj;
         if (other.dimensionMap.size() != this.dimensionMap.size()) {
            return false;
         } else {
            for(Integer key : this.dimensionMap.keySet()) {
               if (!((DimensionData)this.dimensionMap.get(key)).equals(other.dimensionMap.get(key))) {
                  return false;
               }
            }

            return true;
         }
      }
   }
}
