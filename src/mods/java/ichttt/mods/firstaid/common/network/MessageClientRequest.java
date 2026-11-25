package ichttt.mods.firstaid.common.network;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.common.CapProvider;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientRequest implements IMessage {
   private MessageClientRequest.Type type;

   public MessageClientRequest() {
   }

   public MessageClientRequest(MessageClientRequest.Type type) {
      this.type = type;
   }

   public void fromBytes(ByteBuf buf) {
      this.type = MessageClientRequest.Type.TYPES[buf.readByte()];
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.type.ordinal());
   }

   public static class Handler implements IMessageHandler<MessageClientRequest, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageClientRequest message, MessageContext ctx) {
         EntityPlayerMP player = ctx.getServerHandler().player;
         player.getServer().addScheduledTask(() -> {
            if (message.type == MessageClientRequest.Type.TUTORIAL_COMPLETE) {
               CapProvider.tutorialDone.add(player.getName());
               ((MinecraftServer)Objects.requireNonNull(player.getServer())).addScheduledTask(() -> {
                  ((AbstractPlayerDamageModel)Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null))).hasTutorial = true;
               });
            } else if (message.type == MessageClientRequest.Type.REQUEST_REFRESH) {
               FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel((AbstractPlayerDamageModel)Objects.requireNonNull(player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null)), true), player);
            }

         });
         return null;
      }
   }

   public static enum Type {
      TUTORIAL_COMPLETE,
      REQUEST_REFRESH;

      private static final MessageClientRequest.Type[] TYPES = values();

      private Type() {
      }
   }
}
