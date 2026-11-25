package org.orecruncher.dsurround.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilitySpeechData;
import org.orecruncher.dsurround.capabilities.speech.ISpeechData;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.WorldUtils;

public class PacketSpeechBubble implements IMessage {
   protected int entityId;
   protected String message;

   public PacketSpeechBubble() {
   }

   public PacketSpeechBubble(@Nonnull Entity player, @Nonnull String message) {
      this.entityId = player.getEntityId();
      this.message = message;
   }

   public void fromBytes(@Nonnull ByteBuf buf) {
      this.entityId = buf.readInt();
      this.message = ByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(@Nonnull ByteBuf buf) {
      buf.writeInt(this.entityId);
      ByteBufUtils.writeUTF8String(buf, this.message);
   }

   public static class PacketHandler implements IMessageHandler<PacketSpeechBubble, IMessage> {
      public PacketHandler() {
      }

      @Nullable
      public IMessage onMessage(@Nonnull PacketSpeechBubble message, @Nullable MessageContext ctx) {
         if (ctx != null && ModOptions.speechbubbles.enableSpeechBubbles) {
            ModBase.proxy().getThreadListener(ctx).addScheduledTask(() -> {
               World world = EnvironStateHandler.EnvironState.getWorld();
               if (world != null) {
                  Entity entity = WorldUtils.locateEntity(world, message.entityId);
                  if (!(entity instanceof EntityPlayer)) {
                     return;
                  }

                  ISpeechData data = CapabilitySpeechData.getCapability(entity);
                  if (data != null) {
                     data.addMessage(message.message, (int)(ModOptions.speechbubbles.speechBubbleDuration * 20.0F));
                  }
               }

            });
         }

         return null;
      }
   }
}
