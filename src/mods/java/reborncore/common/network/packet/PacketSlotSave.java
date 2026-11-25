package reborncore.common.network.packet;

import java.io.IOException;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.network.NetworkManager;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.tile.SlotConfiguration;

public class PacketSlotSave implements INetworkPacket<PacketSlotSave> {
   BlockPos pos;
   SlotConfiguration.SlotConfig slotConfig;

   public PacketSlotSave(BlockPos pos, SlotConfiguration.SlotConfig slotConfig) {
      this.pos = pos;
      this.slotConfig = slotConfig;
   }

   public PacketSlotSave() {
   }

   public void writeData(ExtendedPacketBuffer buffer) throws IOException {
      buffer.writeBlockPos(this.pos);
      buffer.writeCompoundTag(this.slotConfig.serializeNBT());
   }

   public void readData(ExtendedPacketBuffer buffer) throws IOException {
      this.pos = buffer.readBlockPos();
      this.slotConfig = new SlotConfiguration.SlotConfig(buffer.readCompoundTag());
   }

   public void processData(PacketSlotSave message, MessageContext context) {
      RebornMachineTile legacyMachineBase = (RebornMachineTile)context.getServerHandler().player.world.getTileEntity(this.pos);
      legacyMachineBase.slotConfiguration.getSlotDetails(this.slotConfig.getSlotID()).updateSlotConfig(this.slotConfig);
      legacyMachineBase.markDirty();
      PacketSlotSync packetSlotSync = new PacketSlotSync(this.pos, legacyMachineBase.slotConfiguration);
      NetworkManager.sendToWorld(packetSlotSync, legacyMachineBase.getWorld());
   }
}
