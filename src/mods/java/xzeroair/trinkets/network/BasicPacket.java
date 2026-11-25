package xzeroair.trinkets.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class BasicPacket implements IMessage {
   protected int entityID;
   protected NBTTagCompound tag;

   public BasicPacket() {
   }

   public BasicPacket(int entityID, NBTTagCompound tag) {
      this();
      this.entityID = entityID;
      this.tag = tag;
   }

   protected abstract IMessage handleClient(NetHandlerPlayClient var1);

   protected abstract IMessage handleServer(NetHandlerPlayServer var1);

   public void fromBytes(ByteBuf buf) {
      this.entityID = buf.readInt();
      this.tag = ByteBufUtils.readTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityID);
      ByteBufUtils.writeTag(buf, this.tag);
   }
}
