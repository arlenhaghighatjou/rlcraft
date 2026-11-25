package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.MarkerAPI;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.bidirectional.DeleteMarkerPacket;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.network.server.AddMarkerPacket;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.Log;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.World;

public class MarkerApiImpl implements MarkerAPI {
   private static final int GLOBAL = -1;

   public MarkerApiImpl() {
   }

   @Nullable
   public Marker putMarker(@Nonnull World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
      return this.doPutMarker(world, visibleAhead, atlasID, markerType, label, x, z);
   }

   @Nullable
   public Marker putMarker(@Nonnull World world, boolean visibleAhead, int atlasID, MarkerType markerType, String label, int x, int z) {
      return this.doPutMarker(world, visibleAhead, atlasID, markerType.getRegistryName().toString(), label, x, z);
   }

   @Nullable
   public Marker putGlobalMarker(@Nonnull World world, boolean visibleAhead, String markerType, String label, int x, int z) {
      return this.doPutMarker(world, visibleAhead, -1, markerType, label, x, z);
   }

   @Nullable
   public Marker putGlobalMarker(@Nonnull World world, boolean visibleAhead, MarkerType markerType, String label, int x, int z) {
      return this.doPutMarker(world, visibleAhead, -1, markerType.getRegistryName().toString(), label, x, z);
   }

   private Marker doPutMarker(World world, boolean visibleAhead, int atlasID, String markerType, String label, int x, int z) {
      Marker marker = null;
      if (world.isRemote) {
         if (atlasID == -1) {
            Log.warn("Client tried to add a global marker!");
         } else {
            PacketDispatcher.sendToServer(new AddMarkerPacket(atlasID, world.provider.getDimension(), markerType, label, x, z, visibleAhead));
         }
      } else if (atlasID == -1) {
         MarkersData data = AntiqueAtlasMod.globalMarkersData.getData();
         marker = data.createAndSaveMarker(markerType, label, world.provider.getDimension(), x, z, visibleAhead);
         PacketDispatcher.sendToAll(new MarkersPacket(world.provider.getDimension(), new Marker[]{marker}));
      } else {
         MarkersData data = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
         marker = data.createAndSaveMarker(markerType, label, world.provider.getDimension(), x, z, visibleAhead);
         PacketDispatcher.sendToAll(new MarkersPacket(atlasID, world.provider.getDimension(), new Marker[]{marker}));
      }

      return marker;
   }

   public void deleteMarker(@Nonnull World world, int atlasID, int markerID) {
      this.doDeleteMarker(world, atlasID, markerID);
   }

   public void deleteGlobalMarker(@Nonnull World world, int markerID) {
      this.doDeleteMarker(world, -1, markerID);
   }

   private void doDeleteMarker(World world, int atlasID, int markerID) {
      DeleteMarkerPacket packet = atlasID == -1 ? new DeleteMarkerPacket(markerID) : new DeleteMarkerPacket(atlasID, markerID);
      if (world.isRemote) {
         if (atlasID == -1) {
            Log.warn("Client tried to delete a global marker!");
         } else {
            PacketDispatcher.sendToServer(packet);
         }
      } else {
         MarkersData data = (MarkersData)(atlasID == -1 ? AntiqueAtlasMod.globalMarkersData.getData() : AntiqueAtlasMod.markersData.getMarkersData(atlasID, world));
         data.removeMarker(markerID);
         PacketDispatcher.sendToAll(packet);
      }

   }

   public void registerMarker(MarkerType markerType) {
      MarkerRegistry.register(markerType);
   }
}
