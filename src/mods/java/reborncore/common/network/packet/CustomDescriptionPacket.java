package reborncore.common.network.packet;

import java.io.IOException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.RebornCore;
import reborncore.common.network.ExtendedPacketBuffer;
import reborncore.common.network.INetworkPacket;

public class CustomDescriptionPacket implements INetworkPacket<CustomDescriptionPacket> {
   private BlockPos blockPos;
   private NBTTagCompound nbt;

   public CustomDescriptionPacket(BlockPos blockPos, NBTTagCompound nbt) {
      this.blockPos = blockPos;
      this.nbt = nbt;
   }

   public CustomDescriptionPacket() {
   }

   public CustomDescriptionPacket(TileEntity tileEntity) {
      this.blockPos = tileEntity.getPos();
      this.nbt = tileEntity.writeToNBT(new NBTTagCompound());
   }

   public void writeData(ExtendedPacketBuffer buffer) throws IOException {
      buffer.writeBlockPos(this.blockPos);
      buffer.writeCompoundTag(this.nbt);
   }

   public void readData(ExtendedPacketBuffer buffer) throws IOException {
      this.blockPos = buffer.readBlockPos();
      this.nbt = buffer.readCompoundTag();
   }

   public void processData(CustomDescriptionPacket message, MessageContext context) {
      if (message.blockPos != null && message.nbt != null) {
         World world = RebornCore.proxy.getClientWorld();
         if (world.isBlockLoaded(message.blockPos)) {
            TileEntity tileentity = world.getTileEntity(message.blockPos);
            if (tileentity != null && message.nbt != null) {
               tileentity.readFromNBT(message.nbt);
            }
         }

      }
   }
}
