package hunternif.mc.atlas.network.server;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.network.AbstractMessage;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.TileNameIDPacket;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class RegisterTileIdPacket extends AbstractMessage.AbstractServerMessage<RegisterTileIdPacket> {
   private String name;

   public RegisterTileIdPacket() {
   }

   public RegisterTileIdPacket(String uniqueTileName) {
      this.name = uniqueTileName;
   }

   protected void read(PacketBuffer buffer) throws IOException {
      this.name = ByteBufUtils.readUTF8String(buffer);
   }

   protected void write(PacketBuffer buffer) throws IOException {
      ByteBufUtils.writeUTF8String(buffer, this.name);
   }

   protected void process(EntityPlayer player, Side side) {
      int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(this.name);
      TileNameIDPacket packet = new TileNameIDPacket();
      packet.put(this.name, biomeID);
      PacketDispatcher.sendToAll(packet);
   }
}
