package ivorius.reccomplex.network;

import io.netty.buffer.ByteBuf;
import ivorius.ivtoolkit.blocks.BlockPositions;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFullTileEntityData implements IMessage {
   @Nonnull
   private BlockPos pos = BlockPos.ORIGIN;
   private NBTTagCompound data;

   public PacketFullTileEntityData() {
   }

   public PacketFullTileEntityData(@Nonnull BlockPos pos, NBTTagCompound data) {
      this.pos = pos;
      this.data = data;
   }

   @Nonnull
   public BlockPos getPos() {
      return this.pos;
   }

   public void setPos(@Nonnull BlockPos pos) {
      this.pos = pos;
   }

   public NBTTagCompound getData() {
      return this.data;
   }

   public void setData(NBTTagCompound data) {
      this.data = data;
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = BlockPositions.readFromBuffer(buf);
      this.data = ByteBufUtils.readTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      BlockPositions.writeToBuffer(this.pos, buf);
      ByteBufUtils.writeTag(buf, this.data);
   }
}
