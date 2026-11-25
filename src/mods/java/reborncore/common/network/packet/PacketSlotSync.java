package reborncore.common.network.packet;

import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.RebornCore;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.tile.SlotConfiguration;

public class PacketSlotSync implements INetworkPacket<PacketSlotSync> {
   BlockPos pos;
   SlotConfiguration slotConfig;

   public PacketSlotSync(BlockPos pos, SlotConfiguration slotConfig) {
      this.pos = pos;
      this.slotConfig = slotConfig;
   }

   public PacketSlotSync() {
   }

   public void writeData(ExtendedPacketBuffer buffer) throws IOException {
      buffer.writeBlockPos(this.pos);
      buffer.writeCompoundTag(this.slotConfig.serializeNBT());
   }

   public void readData(ExtendedPacketBuffer buffer) throws IOException {
      this.pos = buffer.readBlockPos();
      this.slotConfig = new SlotConfiguration(buffer.readCompoundTag());
   }

   public void processData(PacketSlotSync message, MessageContext context) {
      if (RebornCore.proxy.getClientWorld().isBlockLoaded(this.pos, false)) {
         RebornMachineTile machineBase = (RebornMachineTile)RebornCore.proxy.getClientWorld().getTileEntity(this.pos);
         if (machineBase == null || machineBase.slotConfiguration == null || this.slotConfig == null || this.slotConfig.getSlotDetails() == null) {
            RebornCore.logHelper.error("Failed to sync slot data to " + this.pos);
         }

         Minecraft.getMinecraft().addScheduledTask(() -> {
            this.slotConfig.getSlotDetails().forEach((slotConfigHolder) -> {
               machineBase.slotConfiguration.updateSlotDetails(slotConfigHolder);
            });
         });
      }
   }
}
