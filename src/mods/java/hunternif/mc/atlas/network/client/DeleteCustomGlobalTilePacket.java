package hunternif.mc.atlas.network.client;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.network.AbstractMessage;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

public class DeleteCustomGlobalTilePacket extends AbstractMessage.AbstractClientMessage<DeleteCustomGlobalTilePacket> {
   private int dimension;
   private int chunkX;
   private int chunkZ;

   public DeleteCustomGlobalTilePacket() {
   }

   public DeleteCustomGlobalTilePacket(int dimension, int chunkX, int chunkZ) {
      this.dimension = dimension;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
   }

   protected void read(PacketBuffer buffer) throws IOException {
      this.dimension = buffer.readInt();
      this.chunkX = buffer.readInt();
      this.chunkZ = buffer.readInt();
   }

   protected void write(PacketBuffer buffer) throws IOException {
      buffer.writeInt(this.dimension);
      buffer.writeInt(this.chunkX);
      buffer.writeInt(this.chunkZ);
   }

   protected void process(EntityPlayer player, Side side) {
      ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
      data.removeBiomeAt(this.dimension, this.chunkX, this.chunkZ);
   }
}
