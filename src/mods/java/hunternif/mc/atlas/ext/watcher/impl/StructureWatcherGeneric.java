package hunternif.mc.atlas.ext.watcher.impl;

import com.google.common.collect.Sets;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.ext.watcher.IStructureWatcher;
import hunternif.mc.atlas.ext.watcher.StructureWatcher;
import hunternif.mc.atlas.ext.watcher.WatcherPos;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.registry.MarkerType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.tuple.Pair;

public class StructureWatcherGeneric implements IStructureWatcher {
   private final Set<WatcherPos> visited;
   private final String datFileName;
   private MarkerType marker;
   private MarkerType tileMarker;
   private String markerLabel;
   private String tileMarkerLabel;
   private final DimensionType dimension;

   public StructureWatcherGeneric(String datFileName, DimensionType dimType, MarkerType marker, String label) {
      this.visited = new HashSet();
      this.marker = marker;
      this.markerLabel = label;
      this.dimension = dimType;
      this.datFileName = datFileName;
      StructureWatcher.INSTANCE.addWatcher(this);
   }

   public StructureWatcherGeneric(String datFileName, int dimension, MarkerType marker, String label) {
      this(datFileName, DimensionManager.getProviderType(dimension), marker, label);
   }

   @Nonnull
   public Set<WatcherPos> getVisited() {
      return this.visited;
   }

   public boolean isDimensionValid(DimensionType type) {
      return type.getId() == this.dimension.getId();
   }

   @Nullable
   public NBTTagCompound getStructureData(@Nonnull World world) {
      MapGenStructureData data = (MapGenStructureData)world.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, this.datFileName);
      return data == null ? null : data.getTagCompound();
   }

   @Nonnull
   public Set<Pair<WatcherPos, String>> visitStructure(@Nonnull World world, @Nonnull NBTTagCompound structureTag) {
      Set<Pair<WatcherPos, String>> visits = Sets.newHashSet();

      for(String coords : structureTag.getKeySet()) {
         if (WatcherPos.POS_PATTERN.matcher(coords).matches()) {
            WatcherPos pos = new WatcherPos(coords);
            if (!this.visited.contains(pos)) {
               NBTTagCompound tag = structureTag.getCompoundTag(coords);
               this.visit(world, tag);
               this.visited.add(pos);
               visits.add(Pair.of(pos, this.datFileName));
            }
         }
      }

      return visits;
   }

   public StructureWatcherGeneric setTileMarker(MarkerType type, String label) {
      this.tileMarker = type;
      this.tileMarkerLabel = label;
      return this;
   }

   private void visit(World world, NBTTagCompound tag) {
      int chunkX = tag.getInteger("ChunkX");
      int chunkZ = tag.getInteger("ChunkZ");
      boolean foundMarker = false;
      boolean foundTileMarker = false;
      List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData().getMarkersAtChunk(world.provider.getDimension(), chunkX / 8, chunkZ / 8);
      if (markers != null) {
         for(Marker marker : markers) {
            if (!foundMarker && marker.getChunkX() == chunkX && marker.getChunkZ() == chunkZ && marker.isOfType(this.marker)) {
               foundMarker = true;
            }

            if (!foundTileMarker && this.tileMarker != null && marker.getChunkX() == chunkX && marker.getChunkZ() == chunkZ && marker.isOfType(this.tileMarker)) {
               foundTileMarker = true;
            }
         }
      }

      if (SettingsConfig.gameplay.autoVillageMarkers) {
         if (!foundMarker) {
            AtlasAPI.markers.putGlobalMarker(world, false, this.marker.getRegistryName().toString(), this.markerLabel, (chunkX << 4) + 8, (chunkZ << 4) + 8);
         }

         if (this.tileMarker != null && !foundTileMarker) {
            AtlasAPI.markers.putGlobalMarker(world, false, this.tileMarker.getRegistryName().toString(), this.tileMarkerLabel, (chunkX << 4) + 8, (chunkZ << 4) + 8);
         }
      }

   }
}
