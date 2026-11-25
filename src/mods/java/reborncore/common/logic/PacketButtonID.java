package reborncore.common.logic;

import java.io.IOException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;

public class PacketButtonID implements INetworkPacket<PacketButtonID> {
   private int ID;
   private BlockPos pos;

   public PacketButtonID(BlockPos pos, int id) {
      this.pos = pos;
      this.ID = id;
   }

   public PacketButtonID() {
   }

   public void writeData(ExtendedPacketBuffer buffer) throws IOException {
      buffer.writeBlockPos(this.pos);
      buffer.writeInt(this.ID);
   }

   public void readData(ExtendedPacketBuffer buffer) throws IOException {
      this.pos = buffer.readBlockPos();
      this.ID = buffer.readInt();
   }

   public void processData(PacketButtonID message, MessageContext context) {
      World world = context.getServerHandler().player.world;
      if (world.getTileEntity(this.pos) != null && world.getTileEntity(this.pos) instanceof LogicController) {
         LogicController controller = (LogicController)world.getTileEntity(this.pos);
         controller.actionPerformed(this.ID);
      }

   }
}
