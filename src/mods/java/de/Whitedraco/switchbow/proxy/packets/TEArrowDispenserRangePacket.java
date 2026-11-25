package de.Whitedraco.switchbow.proxy.packets;

import de.Whitedraco.switchbow.tileentity.TileEntityArrowDispenser;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TEArrowDispenserRangePacket implements IMessage {
   int x;
   int y;
   int z;
   float Werte;

   public TEArrowDispenserRangePacket() {
   }

   public TEArrowDispenserRangePacket(int i, int j, int k, float Werte) {
      this.x = i;
      this.y = j;
      this.z = k;
      this.Werte = Werte;
   }

   public void fromBytes(ByteBuf buf) {
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
      this.Werte = buf.readFloat();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
      buf.writeFloat(this.Werte);
   }

   public void execute(MessageContext ctx) {
      TileEntityArrowDispenser tilePfeilwerfer = (TileEntityArrowDispenser)ctx.getServerHandler().player.world.getTileEntity(new BlockPos(this.x, this.y, this.z));
      tilePfeilwerfer.setRange(this.Werte);
      IBlockState blockstats = ctx.getServerHandler().player.world.getBlockState(new BlockPos(this.x, this.y, this.z));
      ctx.getServerHandler().player.world.notifyBlockUpdate(new BlockPos(this.x, this.y, this.z), blockstats, blockstats, 3);
   }

   public static class Handler implements IMessageHandler<TEArrowDispenserRangePacket, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(TEArrowDispenserRangePacket message, MessageContext ctx) {
         message.execute(ctx);
         return null;
      }
   }
}
