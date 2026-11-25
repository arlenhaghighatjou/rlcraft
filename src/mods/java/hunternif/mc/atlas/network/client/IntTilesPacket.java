package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.network.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class IntTilesPacket extends AbstractMessage.AbstractClientMessage<IntTilesPacket> implements TilesPacket {
   private static final int ENTRY_SIZE_BYTES = 10;
   private int dimension;
   private int tileCount;
   private ByteBuf tileData;

   public IntTilesPacket() {
   }

   public IntTilesPacket(int dimension) {
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
      this.dimension = buffer.readVarInt();
      this.tileCount = buffer.readVarInt();
      this.tileData = buffer.readBytes(this.tileCount * 10);
   }

   public void write(PacketBuffer buffer) throws IOException {
      buffer.writeVarInt(this.dimension);
      buffer.writeVarInt(this.tileCount);
      buffer.writeBytes(this.tileData);
   }

   protected void process(EntityPlayer player, Side side) {
      ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();

      for(int i = 0; i < this.tileCount; ++i) {
         data.setBiomeIdAt(this.dimension, this.tileData.readInt(), this.tileData.readInt(), this.tileData.readShort());
      }

   }
}
