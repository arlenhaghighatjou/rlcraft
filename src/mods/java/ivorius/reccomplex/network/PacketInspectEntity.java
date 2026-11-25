package ivorius.reccomplex.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import ivorius.ivtoolkit.network.IvPacketHelper;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Supplier;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketInspectEntity implements IMessage {
   private NBTTagCompound data;
   private EaglercraftUUID uuid;
   private String name;

   public PacketInspectEntity() {
   }

   public PacketInspectEntity(NBTTagCompound data, EaglercraftUUID uuid, String name) {
      this.data = data;
      this.uuid = uuid;
      this.name = name;
   }

   public NBTTagCompound getData() {
      return this.data;
   }

   public void setData(NBTTagCompound data) {
      this.data = data;
   }

   public EaglercraftUUID getUuid() {
      return this.uuid;
   }

   public void setUuid(EaglercraftUUID uuid) {
      this.uuid = uuid;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void fromBytes(ByteBuf buf) {
      this.data = (NBTTagCompound)IvPacketHelper.maybeRead(buf, (Object)null, () -> {
         return ByteBufUtils.readTag(buf);
      });
      this.uuid = EaglercraftUUID.fromString(ByteBufUtils.readUTF8String(buf));
      this.name = ByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      IvPacketHelper.maybeWrite(buf, this.data, () -> {
         ByteBufUtils.writeTag(buf, this.data);
      });
      ByteBufUtils.writeUTF8String(buf, this.uuid.toString());
      ByteBufUtils.writeUTF8String(buf, this.name);
   }
}
