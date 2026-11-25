package cursedflames.bountifulbaubles.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class NBTPacket implements IMessage {
   private NBTTagCompound tag;

   public void fromBytes(ByteBuf buf) {
      this.setTag(ByteBufUtils.readTag(buf));
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeTag(buf, this.getTag());
   }

   public NBTPacket() {
   }

   public NBTPacket(NBTTagCompound tag) {
      this.tag = tag;
   }

   public NBTPacket(NBTTagCompound tag, int id) {
      tag.setByte("id", (byte)id);
      this.tag = tag;
   }

   public NBTTagCompound getTag() {
      return this.tag;
   }

   public void setTag(NBTTagCompound tag) {
      this.tag = tag;
   }
}
