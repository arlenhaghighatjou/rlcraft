package reborncore.common.network.packet;

import java.io.IOException;
import java.util.concurrent.Callable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.network.NetworkManager;
import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.RebornMachineTile;

public class PacketFluidConfigSave implements INetworkPacket<PacketFluidConfigSave> {
   BlockPos pos;
   FluidConfiguration.FluidConfig fluidConfiguration;

   public PacketFluidConfigSave(BlockPos pos, FluidConfiguration.FluidConfig fluidConfiguration) {
      this.pos = pos;
      this.fluidConfiguration = fluidConfiguration;
   }

   public PacketFluidConfigSave() {
   }

   public void writeData(ExtendedPacketBuffer buffer) throws IOException {
      buffer.writeBlockPos(this.pos);
      buffer.writeCompoundTag(this.fluidConfiguration.serializeNBT());
   }

   public void readData(ExtendedPacketBuffer buffer) throws IOException {
      this.pos = buffer.readBlockPos();
      this.fluidConfiguration = new FluidConfiguration.FluidConfig(buffer.readCompoundTag());
   }

   public void processData(PacketFluidConfigSave message, MessageContext context) {
      RebornMachineTile legacyMachineBase = (RebornMachineTile)context.getServerHandler().player.world.getTileEntity(this.pos);
      legacyMachineBase.fluidConfiguration.updateFluidConfig(this.fluidConfiguration);
      legacyMachineBase.markDirty();
      PacketFluidConfigSync packetFluidConfigSync = new PacketFluidConfigSync(this.pos, legacyMachineBase.fluidConfiguration);
      NetworkManager.sendToWorld(packetFluidConfigSync, legacyMachineBase.getWorld());
      context.getServerHandler().player.world.getMinecraftServer().callFromMainThread(() -> {
         World world = legacyMachineBase.getWorld();
         IBlockState blockState = world.getBlockState(legacyMachineBase.getPos());
         world.markAndNotifyBlock(legacyMachineBase.getPos(), world.getChunkFromBlockCoords(legacyMachineBase.getPos()), blockState, blockState, 3);
         return null;
      });
   }
}
