package hunternif.mc.atlas.network.bidirectional;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessage;
import hunternif.mc.atlas.util.Log;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class DeleteMarkerPacket extends AbstractMessage<DeleteMarkerPacket> {
   private static final int GLOBAL = -1;
   private int atlasID;
   private int markerID;

   public DeleteMarkerPacket() {
   }

   public DeleteMarkerPacket(int atlasID, int markerID) {
      this.atlasID = atlasID;
      this.markerID = markerID;
   }

   public DeleteMarkerPacket(int markerID) {
      this(-1, markerID);
   }

   public void read(PacketBuffer buffer) throws IOException {
      this.atlasID = buffer.readVarInt();
      this.markerID = buffer.readVarInt();
   }

   public void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.atlasID);
      buffer.writeVarInt(this.markerID);
   }

   private boolean isGlobal() {
      return this.atlasID == -1;
   }

   protected void process(EntityPlayer player, Side side) {
      if (side.isServer()) {
         if (side.isServer() && SettingsConfig.gameplay.itemNeeded && !player.inventory.hasItemStack(new ItemStack(RegistrarAntiqueAtlas.ATLAS, 1, this.atlasID))) {
            Log.warn("Player %s attempted to delete marker from someone else's Atlas #%d", player.getGameProfile().getName(), this.atlasID);
            return;
         }

         if (this.isGlobal()) {
            AtlasAPI.markers.deleteGlobalMarker(player.getEntityWorld(), this.markerID);
         } else {
            AtlasAPI.markers.deleteMarker(player.getEntityWorld(), this.atlasID, this.markerID);
         }
      } else {
         MarkersData data = (MarkersData)(this.isGlobal() ? AntiqueAtlasMod.globalMarkersData.getData() : AntiqueAtlasMod.markersData.getMarkersData(this.atlasID, player.getEntityWorld()));
         data.removeMarker(this.markerID);
      }

   }
}
