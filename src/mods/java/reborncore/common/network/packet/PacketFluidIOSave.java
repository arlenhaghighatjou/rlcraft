package reborncore.common.network.packet;

import java.io.IOException;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.network.NetworkManager;
import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.RebornMachineTile;

public class PacketFluidIOSave implements INetworkPacket<PacketFluidIOSave> {
   BlockPos pos;
   boolean input;
   boolean output;

   public PacketFluidIOSave(BlockPos pos, boolean input, boolean output) {
      this.pos = pos;
      this.input = input;
      this.output = output;
   }

   public PacketFluidIOSave() {
   }

   public void writeData(ExtendedPacketBuffer buffer) throws IOException {
      buffer.writeBlockPos(this.pos);
      buffer.writeBoolean(this.input);
      buffer.writeBoolean(this.output);
   }

   public void readData(ExtendedPacketBuffer buffer) throws IOException {
      this.pos = buffer.readBlockPos();
      this.input = buffer.readBoolean();
      this.output = buffer.readBoolean();
   }

   public void processData(PacketFluidIOSave message, MessageContext context) {
      RebornMachineTile legacyMachineBase = (RebornMachineTile)context.getServerHandler().player.world.getTileEntity(this.pos);
      FluidConfiguration config = legacyMachineBase.fluidConfiguration;
      if (config != null) {
         config.setInput(this.input);
         config.setOutput(this.output);
         PacketFluidConfigSync packetFluidConfigSync = new PacketFluidConfigSync(this.pos, legacyMachineBase.fluidConfiguration);
         NetworkManager.sendToWorld(packetFluidConfigSync, legacyMachineBase.getWorld());
      }
   }
}
