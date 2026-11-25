package ivorius.ivtoolkit.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import ivorius.ivtoolkit.blocks.BlockPositions;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketTileEntityClientEvent implements IMessage {
   private int dimension;
   private BlockPos pos;
   private String context;
   private ByteBuf payload;

   public PacketTileEntityClientEvent() {
   }

   public PacketTileEntityClientEvent(int dimension, BlockPos pos, String context, ByteBuf payload) {
      this.dimension = dimension;
      this.pos = pos;
      this.context = context;
      this.payload = payload;
   }

   public static <ETileEntity extends TileEntity & ClientEventHandler> PacketTileEntityClientEvent packetEntityData(ETileEntity entity, String context, Object... params) {
      ByteBuf buf = Unpooled.buffer();
      ((ClientEventHandler)entity).assembleClientEvent(buf, context, params);
      return new PacketTileEntityClientEvent(entity.getWorld().provider.getDimension(), entity.getPos(), context, buf);
   }

   public int getDimension() {
      return this.dimension;
   }

   public void setDimension(int dimension) {
      this.dimension = dimension;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void setPos(BlockPos pos) {
      this.pos = pos;
   }

   public String getContext() {
      return this.context;
   }

   public void setContext(String context) {
      this.context = context;
   }

   public ByteBuf getPayload() {
      return this.payload;
   }

   public void setPayload(ByteBuf payload) {
      this.payload = payload;
   }

   public void fromBytes(ByteBuf buf) {
      this.dimension = buf.readInt();
      this.pos = BlockPositions.readFromBuffer(buf);
      this.context = ByteBufUtils.readUTF8String(buf);
      this.payload = IvPacketHelper.readByteBuffer(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.dimension);
      BlockPositions.writeToBuffer(this.pos, buf);
      ByteBufUtils.writeUTF8String(buf, this.context);
      IvPacketHelper.writeByteBuffer(buf, this.payload);
   }
}
