package hunternif.mc.atlas.network.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.event.OptionalMarkerEvent;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessage;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class MarkersPacket extends AbstractMessage.AbstractClientMessage<MarkersPacket> {
   private static final int GLOBAL = -1;
   private int atlasID;
   private int dimension;
   private final ListMultimap<String, Marker> markersByType;

   public MarkersPacket() {
      this.markersByType = ArrayListMultimap.create();
   }

   public MarkersPacket(int atlasID, int dimension, Marker... markers) {
      this.markersByType = ArrayListMultimap.create();
      this.atlasID = atlasID;
      this.dimension = dimension;

      for(Marker marker : markers) {
         this.markersByType.put(marker.getType(), marker);
      }

   }

   public MarkersPacket(int dimension, Marker... markers) {
      this(-1, dimension, markers);
   }

   public MarkersPacket putMarker(Marker marker) {
      this.markersByType.put(marker.getType(), marker);
      return this;
   }

   private boolean isGlobal() {
      return this.atlasID == -1;
   }

   public void read(PacketBuffer buffer) throws IOException {
      this.atlasID = buffer.readVarInt();
      this.dimension = buffer.readVarInt();
      int typesLength = buffer.readVarInt();

      for(int i = 0; i < typesLength; ++i) {
         String type = ByteBufUtils.readUTF8String(buffer);
         int markersLength = buffer.readVarInt();

         for(int j = 0; j < markersLength; ++j) {
            Marker marker = new Marker(buffer.readVarInt(), type, ByteBufUtils.readUTF8String(buffer), this.dimension, buffer.readInt(), buffer.readInt(), buffer.readBoolean());
            this.markersByType.put(type, marker);
         }
      }

   }

   public void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.atlasID);
      buffer.writeVarInt(this.dimension);
      Set<String> types = this.markersByType.keySet();
      buffer.writeVarInt(types.size());

      for(String type : types) {
         ByteBufUtils.writeUTF8String(buffer, type);
         List<Marker> markers = this.markersByType.get(type);
         buffer.writeVarInt(markers.size());

         for(Marker marker : markers) {
            buffer.writeVarInt(marker.getId());
            ByteBufUtils.writeUTF8String(buffer, marker.getLabel());
            buffer.writeInt(marker.getX());
            buffer.writeInt(marker.getZ());
            buffer.writeBoolean(marker.isVisibleAhead());
         }
      }

   }

   protected void process(EntityPlayer player, Side side) {
      if (this.isGlobal()) {
         MarkersData globalMarkersData = AntiqueAtlasMod.globalMarkersData.getData();

         for(Marker marker : this.markersByType.values()) {
            MarkerType type = MarkerRegistry.find(marker.getType());
            if (type != null && !MinecraftForge.EVENT_BUS.post(new OptionalMarkerEvent(-1, this.dimension, type, marker.getLabel(), marker.getX(), marker.getZ(), marker.isVisibleAhead()))) {
               globalMarkersData.loadMarker(marker);

               for(MarkersData markersData : AntiqueAtlasMod.markersData.getAllLoadedMarkersData()) {
                  Marker localCopy = new Marker(-marker.getId(), marker.getType(), marker.getLabel(), marker.getDimension(), marker.getX(), marker.getZ(), marker.isVisibleAhead());
                  markersData.loadMarker(localCopy);
               }
            }
         }
      } else {
         MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(this.atlasID, player.getEntityWorld());

         for(Marker marker : this.markersByType.values()) {
            markersData.loadMarker(marker);
         }
      }

   }
}
