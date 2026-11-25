package reborncore.common.network.packet;

import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;
import reborncore.common.network.NetworkManager;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.tile.SlotConfiguration;

public class PacketConfigSave implements INetworkPacket<PacketConfigSave> {
   BlockPos pos;
   SlotConfiguration slotConfig;
   NBTTagCompound tagCompound;

   public PacketConfigSave(BlockPos pos, SlotConfiguration slotConfig) {
      this.pos = pos;
      this.slotConfig = slotConfig;
   }

   public PacketConfigSave() {
   }

   public void writeData(ExtendedPacketBuffer buffer) throws IOException {
      buffer.writeBlockPos(this.pos);
      buffer.writeCompoundTag(this.slotConfig.serializeNBT());
   }

   public void readData(ExtendedPacketBuffer buffer) throws IOException {
      this.pos = buffer.readBlockPos();
      this.tagCompound = buffer.readCompoundTag();
   }

   public void processData(PacketConfigSave message, MessageContext context) {
      RebornMachineTile legacyMachineBase = (RebornMachineTile)context.getServerHandler().player.world.getTileEntity(this.pos);
      legacyMachineBase.slotConfiguration.deserializeNBT(this.tagCompound);
      legacyMachineBase.markDirty();
      PacketSlotSync packetSlotSync = new PacketSlotSync(this.pos, legacyMachineBase.slotConfiguration);
      NetworkManager.sendToWorld(packetSlotSync, legacyMachineBase.getWorld());
   }
}
