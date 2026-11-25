package ivorius.ivtoolkit.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketEntityData implements IMessage {
   private int entityID;
   private String context;
   private ByteBuf payload;

   public PacketEntityData() {
   }

   public PacketEntityData(int entityID, String context, ByteBuf payload) {
      this.entityID = entityID;
      this.context = context;
      this.payload = payload;
   }

   public static <UEntity extends Entity & PartialUpdateHandler> PacketEntityData packetEntityData(UEntity entity, String context, Object... params) {
      ByteBuf buf = Unpooled.buffer();
      ((PartialUpdateHandler)entity).writeUpdateData(buf, context, params);
      return new PacketEntityData(entity.getEntityId(), context, buf);
   }

   public int getEntityID() {
      return this.entityID;
   }

   public void setEntityID(int entityID) {
      this.entityID = entityID;
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
      this.entityID = buf.readInt();
      this.context = ByteBufUtils.readUTF8String(buf);
      this.payload = IvPacketHelper.readByteBuffer(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityID);
      ByteBufUtils.writeUTF8String(buf, this.context);
      IvPacketHelper.writeByteBuffer(buf, this.payload);
   }
}
