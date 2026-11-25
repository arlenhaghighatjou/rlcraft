package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.event.OptionalMarkerEvent;
import hunternif.mc.atlas.network.AbstractMessage;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class OptionalMarkerPacket extends AbstractMessage.AbstractClientMessage<OptionalMarkerPacket> {
   private int atlasID;
   private int dimension;
   private String typeName;
   private String label;
   private int x;
   private int z;
   private boolean visibleAhead;

   public OptionalMarkerPacket() {
   }

   public OptionalMarkerPacket(int atlasID, int dimension, MarkerType type, String label, int x, int z, boolean visibleAhead) {
      this.atlasID = atlasID;
      this.dimension = dimension;
      this.typeName = type.getRegistryName().toString();
      this.label = label;
      this.x = x;
      this.z = z;
      this.visibleAhead = visibleAhead;
   }

   public void read(PacketBuffer buffer) throws IOException {
      this.atlasID = buffer.readVarInt();
      this.dimension = buffer.readVarInt();
      this.typeName = ByteBufUtils.readUTF8String(buffer);
      this.label = ByteBufUtils.readUTF8String(buffer);
      this.x = buffer.readInt();
      this.z = buffer.readInt();
      this.visibleAhead = buffer.readBoolean();
   }

   public void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.atlasID);
      buffer.writeVarInt(this.dimension);
      ByteBufUtils.writeUTF8String(buffer, this.typeName);
      ByteBufUtils.writeUTF8String(buffer, this.label);
      buffer.writeInt(this.x);
      buffer.writeInt(this.z);
      buffer.writeBoolean(this.visibleAhead);
   }

   protected void process(EntityPlayer player, Side side) {
      MarkerType type = MarkerRegistry.find(this.typeName);
      if (type != null) {
         if (!MinecraftForge.EVENT_BUS.post(new OptionalMarkerEvent(this.atlasID, this.dimension, type, this.label, this.x, this.z, this.visibleAhead))) {
            AtlasAPI.markers.putMarker(player.getEntityWorld(), this.visibleAhead, this.atlasID, type, this.label, this.x, this.z);
         }

      }
   }
}
