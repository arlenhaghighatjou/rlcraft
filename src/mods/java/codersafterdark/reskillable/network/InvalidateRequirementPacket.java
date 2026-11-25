package codersafterdark.reskillable.network;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementCache;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class InvalidateRequirementPacket implements IMessage, IMessageHandler<InvalidateRequirementPacket, IMessage> {
   private Class<? extends Requirement>[] cacheTypes;
   private UUID uuid;

   public InvalidateRequirementPacket() {
   }

   public InvalidateRequirementPacket(UUID uuid, Class<? extends Requirement>... cacheTypes) {
      this.uuid = uuid;
      this.cacheTypes = cacheTypes;
   }

   public void fromBytes(ByteBuf buf) {
      this.uuid = new UUID(buf.readLong(), buf.readLong());
      this.cacheTypes = new Class[buf.readInt()];

      for(int i = 0; i < this.cacheTypes.length; ++i) {
         try {
            Class<?> rClass = Class.forName(ByteBufUtils.readUTF8String(buf));
            if (Requirement.class.isAssignableFrom(rClass)) {
               this.cacheTypes[i] = rClass;
            }
         } catch (ClassNotFoundException var4) {
            var4.printStackTrace();
         }
      }

   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.uuid.getMostSignificantBits());
      buf.writeLong(this.uuid.getLeastSignificantBits());
      buf.writeInt(this.cacheTypes.length);

      for(Class<? extends Requirement> rClass : this.cacheTypes) {
         ByteBufUtils.writeUTF8String(buf, rClass.getName());
      }

   }

   public IMessage onMessage(InvalidateRequirementPacket message, MessageContext ctx) {
      if (ctx.side.isServer()) {
         FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            this.handleMessage(message, ctx);
         });
      } else {
         Minecraft.getMinecraft().addScheduledTask(() -> {
            return this.handleMessage(message, ctx);
         });
      }

      return null;
   }

   public IMessage handleMessage(InvalidateRequirementPacket message, MessageContext ctx) {
      if (message.cacheTypes.length == 0) {
         RequirementCache.getCache(message.uuid, ctx.side.isClient()).forceClear();
      } else {
         RequirementCache.invalidateCacheNoPacket(message.uuid, ctx.side.isClient(), message.cacheTypes);
      }

      return null;
   }
}
