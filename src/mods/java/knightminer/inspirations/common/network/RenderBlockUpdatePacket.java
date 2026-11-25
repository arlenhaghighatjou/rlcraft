package knightminer.inspirations.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class RenderBlockUpdatePacket extends AbstractPacketThreadsafe {
   protected BlockPos pos;

   public RenderBlockUpdatePacket() {
   }

   public RenderBlockUpdatePacket(BlockPos pos) {
      this.pos = pos;
   }

   public void handleClientSafe(NetHandlerPlayClient netHandler) {
      Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate((World)null, this.pos, (IBlockState)null, (IBlockState)null, 0);
   }

   public void handleServerSafe(NetHandlerPlayServer netHandler) {
      throw new UnsupportedOperationException("Clientside only");
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = this.readPos(buf);
   }

   public void toBytes(ByteBuf buf) {
      this.writePos(this.pos, buf);
   }
}
