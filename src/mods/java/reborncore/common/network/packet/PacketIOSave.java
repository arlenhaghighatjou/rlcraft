package reborncore.common.network.packet;

import java.io.IOException;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.network.NetworkManager;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.tile.SlotConfiguration;

public class PacketIOSave implements INetworkPacket<PacketIOSave> {
   BlockPos pos;
   int slotID;
   boolean input;
   boolean output;
   boolean filter;

   public PacketIOSave(BlockPos pos, int slotID, boolean input, boolean output, boolean filter) {
      this.pos = pos;
      this.slotID = slotID;
      this.input = input;
      this.output = output;
      this.filter = filter;
   }

   public PacketIOSave() {
   }

   public void writeData(ExtendedPacketBuffer buffer) throws IOException {
      buffer.writeBlockPos(this.pos);
      buffer.writeInt(this.slotID);
      buffer.writeBoolean(this.input);
      buffer.writeBoolean(this.output);
      buffer.writeBoolean(this.filter);
   }

   public void readData(ExtendedPacketBuffer buffer) throws IOException {
      this.pos = buffer.readBlockPos();
      this.slotID = buffer.readInt();
      this.input = buffer.readBoolean();
      this.output = buffer.readBoolean();
      this.filter = buffer.readBoolean();
   }

   public void processData(PacketIOSave message, MessageContext context) {
      RebornMachineTile legacyMachineBase = (RebornMachineTile)context.getServerHandler().player.world.getTileEntity(this.pos);
      SlotConfiguration.SlotConfigHolder holder = legacyMachineBase.slotConfiguration.getSlotDetails(this.slotID);
      if (holder != null) {
         holder.setInput(this.input);
         holder.setOutput(this.output);
         holder.setfilter(this.filter);
         PacketSlotSync packetSlotSync = new PacketSlotSync(this.pos, legacyMachineBase.slotConfiguration);
         NetworkManager.sendToAll(packetSlotSync);
      }
   }
}
