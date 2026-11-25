package reborncore.common.network.packet;

import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.RebornCore;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.RebornMachineTile;

public class PacketFluidConfigSync implements INetworkPacket<PacketFluidConfigSync> {
   BlockPos pos;
   FluidConfiguration fluidConfiguration;

   public PacketFluidConfigSync(BlockPos pos, FluidConfiguration fluidConfiguration) {
      this.pos = pos;
      this.fluidConfiguration = fluidConfiguration;
   }

   public PacketFluidConfigSync() {
   }

   public void writeData(ExtendedPacketBuffer buffer) throws IOException {
      buffer.writeBlockPos(this.pos);
      buffer.writeCompoundTag(this.fluidConfiguration.serializeNBT());
   }

   public void readData(ExtendedPacketBuffer buffer) throws IOException {
      this.pos = buffer.readBlockPos();
      this.fluidConfiguration = new FluidConfiguration(buffer.readCompoundTag());
   }

   public void processData(PacketFluidConfigSync message, MessageContext context) {
      if (RebornCore.proxy.getClientWorld().isBlockLoaded(this.pos, false)) {
         RebornMachineTile machineBase = (RebornMachineTile)RebornCore.proxy.getClientWorld().getTileEntity(this.pos);
         if (machineBase == null || machineBase.fluidConfiguration == null || this.fluidConfiguration == null) {
            RebornCore.logHelper.error("Failed to sync fluid config data to " + this.pos);
         }

         Minecraft.getMinecraft().addScheduledTask(() -> {
            this.fluidConfiguration.getAllSides().forEach((fluidConfig) -> {
               machineBase.fluidConfiguration.updateFluidConfig(fluidConfig);
            });
            machineBase.fluidConfiguration.setInput(this.fluidConfiguration.autoInput());
            machineBase.fluidConfiguration.setOutput(this.fluidConfiguration.autoOutput());
         });
      }
   }
}
