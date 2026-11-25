package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.network.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class ShortDimensionUpdatePacket extends AbstractMessage.AbstractClientMessage<ShortDimensionUpdatePacket> implements TilesPacket {
   private static final int ENTRY_SIZE_BYTES = 6;
   private int atlasID;
   private int dimension;
   private int tileCount;
   private ByteBuf tileData;

   public ShortDimensionUpdatePacket() {
   }

   public ShortDimensionUpdatePacket(int atlasID, int dimension) {
      this.dimension = dimension;
      this.tileCount = 0;
      this.tileData = Unpooled.buffer();
   }

   public void addTile(int x, int y, int biomeID) {
      this.tileData.writeShort(x);
      this.tileData.writeShort(y);
      this.tileData.writeShort(biomeID);
      ++this.tileCount;
   }

   public boolean isEmpty() {
      return this.tileCount == 0;
   }

   public void read(PacketBuffer buffer) throws IOException {
      this.atlasID = buffer.readVarInt();
      this.dimension = buffer.readVarInt();
      this.tileCount = buffer.readVarInt();
      this.tileData = buffer.readBytes(this.tileCount * 6);
   }

   public void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.atlasID);
      buffer.writeVarInt(this.dimension);
      buffer.writeVarInt(this.tileCount);
      buffer.writeBytes(this.tileData);
   }

   protected void process(EntityPlayer player, Side side) {
      AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(this.atlasID, player.world);

      for(int i = 0; i < this.tileCount; ++i) {
         data.getDimensionData(this.dimension).setTile(this.tileData.readShort(), this.tileData.readShort(), new Tile(this.tileData.readShort()));
      }

   }
}
