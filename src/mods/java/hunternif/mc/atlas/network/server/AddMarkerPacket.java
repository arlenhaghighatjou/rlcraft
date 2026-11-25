package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import hunternif.mc.atlas.network.AbstractMessage;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.MarkersPacket;
import hunternif.mc.atlas.util.Log;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class AddMarkerPacket extends AbstractMessage.AbstractServerMessage<AddMarkerPacket> {
   private int atlasID;
   private int dimension;
   private String type;
   private String label;
   private int x;
   private int y;
   private boolean visibleAhead;

   public AddMarkerPacket() {
   }

   public AddMarkerPacket(int atlasID, int dimension, String type, String label, int x, int y, boolean visibleAhead) {
      this.atlasID = atlasID;
      this.dimension = dimension;
      this.type = type;
      this.label = label;
      this.x = x;
      this.y = y;
      this.visibleAhead = visibleAhead;
   }

   public void read(PacketBuffer buffer) throws IOException {
      this.atlasID = buffer.readVarInt();
      this.dimension = buffer.readVarInt();
      this.type = ByteBufUtils.readUTF8String(buffer);
      this.label = ByteBufUtils.readUTF8String(buffer);
      this.x = buffer.readInt();
      this.y = buffer.readInt();
      this.visibleAhead = buffer.readBoolean();
   }

   public void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.atlasID);
      buffer.writeVarInt(this.dimension);
      ByteBufUtils.writeUTF8String(buffer, this.type);
      ByteBufUtils.writeUTF8String(buffer, this.label);
      buffer.writeInt(this.x);
      buffer.writeInt(this.y);
      buffer.writeBoolean(this.visibleAhead);
   }

   protected void process(EntityPlayer player, Side side) {
      if (SettingsConfig.gameplay.itemNeeded && !player.inventory.hasItemStack(new ItemStack(RegistrarAntiqueAtlas.ATLAS, 1, this.atlasID))) {
         Log.warn("Player %s attempted to put marker into someone else's Atlas #%d", player.getGameProfile().getName(), this.atlasID);
      } else {
         MarkersData markersData = AntiqueAtlasMod.markersData.getMarkersData(this.atlasID, player.getEntityWorld());
         Marker marker = markersData.createAndSaveMarker(this.type, this.label, this.dimension, this.x, this.y, this.visibleAhead);
         MarkersPacket packetForClients = new MarkersPacket(this.atlasID, this.dimension, new Marker[]{marker});
         PacketDispatcher.sendToAll(packetForClients);
      }
   }
}
