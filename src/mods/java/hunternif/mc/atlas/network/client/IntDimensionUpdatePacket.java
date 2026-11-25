package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.network.AbstractMessage;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class IntDimensionUpdatePacket extends AbstractMessage.AbstractClientMessage<IntDimensionUpdatePacket> implements TilesPacket {
   private static final int ENTRY_SIZE_BYTES = 10;
   private int atlasID;
   private int dimension;
   private int tileCount;
   private ByteBuf tileData;

   public IntDimensionUpdatePacket() {
   }

   public IntDimensionUpdatePacket(int atlasID, int dimension) {
      this.dimension = dimension;
      this.tileCount = 0;
      this.tileData = Unpooled.buffer();
   }

   public void addTile(int x, int y, int biomeID) {
      this.tileData.writeInt(x);
      this.tileData.writeInt(y);
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
      this.tileData = buffer.readBytes(this.tileCount * 10);
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
         data.getDimensionData(this.dimension).setTile(this.tileData.readInt(), this.tileData.readInt(), new Tile(this.tileData.readShort()));
      }

   }
}
