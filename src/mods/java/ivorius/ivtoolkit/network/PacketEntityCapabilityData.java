package ivorius.ivtoolkit.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketEntityCapabilityData implements IMessage {
   private int entityID;
   private String context;
   private String capabilityKey;
   private EnumFacing direction;
   private ByteBuf payload;

   public PacketEntityCapabilityData() {
   }

   public PacketEntityCapabilityData(int entityID, String context, String capabilityKey, EnumFacing direction, ByteBuf payload) {
      this.entityID = entityID;
      this.context = context;
      this.capabilityKey = capabilityKey;
      this.direction = direction;
      this.payload = payload;
   }

   public static <T> PacketEntityCapabilityData packetEntityData(Entity entity, String capabilityKey, EnumFacing facing, String context, Object... params) {
      Capability<T> capability = CapabilityUpdateRegistry.INSTANCE.capability(capabilityKey);
      T t = (T)entity.getCapability(capability, facing);
      if (!(t instanceof PartialUpdateHandler)) {
         throw new IllegalArgumentException("Capability must implement PartialUpdateHandler to send update packets!");
      } else {
         ByteBuf buf = Unpooled.buffer();
         ((PartialUpdateHandler)t).writeUpdateData(buf, context, params);
         return new PacketEntityCapabilityData(entity.getEntityId(), context, capabilityKey, facing, buf);
      }
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

   public String getCapabilityKey() {
      return this.capabilityKey;
   }

   public void setCapabilityKey(String capabilityKey) {
      this.capabilityKey = capabilityKey;
   }

   public EnumFacing getDirection() {
      return this.direction;
   }

   public void setDirection(EnumFacing direction) {
      this.direction = direction;
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
      this.capabilityKey = ByteBufUtils.readUTF8String(buf);
      this.direction = (EnumFacing)IvPacketHelper.maybeRead(buf, (Object)null, () -> {
         return this.direction = EnumFacing.getFront(buf.readInt());
      });
      this.payload = IvPacketHelper.readByteBuffer(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityID);
      ByteBufUtils.writeUTF8String(buf, this.context);
      ByteBufUtils.writeUTF8String(buf, this.capabilityKey);
      IvPacketHelper.maybeWrite(buf, this.direction, () -> {
         this.direction.getIndex();
      });
      IvPacketHelper.writeByteBuffer(buf, this.payload);
   }
}
