package com.lycanitesmobs.core.network;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.util.TriConsumer;

public class PacketHandler {
   public final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("lycanitesmobs");

   public PacketHandler() {
   }

   public void init() {
      int messageID = 0;
      this.network.registerMessage(this.clientHandler(MessageBeastiary::onMessage), MessageBeastiary.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageCreature::onMessage), MessageCreature.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageCreatureKnowledge::onMessage), MessageCreatureKnowledge.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessagePlayerStats::onMessage), MessagePlayerStats.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessagePetEntry::onMessage), MessagePetEntry.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessagePetEntryRemove::onMessage), MessagePetEntryRemove.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageSummonSet::onMessage), MessageSummonSet.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageSummonSetSelection::onMessage), MessageSummonSetSelection.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageEntityPickedUp::onMessage), MessageEntityPickedUp.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageEntityPerched::onMessage), MessageEntityPerched.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageWorldEvent::onMessage), MessageWorldEvent.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageMobEvent::onMessage), MessageMobEvent.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageSummoningPedestalStats::onMessage), MessageSummoningPedestalStats.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageEntityVelocity::onMessage), MessageEntityVelocity.class, messageID++, Side.CLIENT);
      this.network.registerMessage(this.serverHandler(MessageEntityGUICommand::onMessage), MessageEntityGUICommand.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageSyncRequest::onMessage), MessageSyncRequest.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageGUIRequest::onMessage), MessageGUIRequest.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessagePlayerControl::onMessage), MessagePlayerControl.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessagePlayerLeftClick::onMessage), MessagePlayerLeftClick.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessagePlayerAttack::onMessage), MessagePlayerAttack.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessagePetEntry::onMessage), MessagePetEntry.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessagePetEntryRemove::onMessage), MessagePetEntryRemove.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageSummonSet::onMessage), MessageSummonSet.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageSummonSetSelection::onMessage), MessageSummonSetSelection.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageSummoningPedestalSummonSet::onMessage), MessageSummoningPedestalSummonSet.class, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageTileEntityButton::onMessage), MessageTileEntityButton.class, messageID++, Side.SERVER);
   }

   private <REQ extends IMessage, REPLY extends IMessage> IMessageHandler<REQ, REPLY> clientHandler(TriConsumer<REQ, MessageContext, EntityPlayer> c) {
      return (message, ctx) -> {
         if (ctx.side == Side.CLIENT) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               c.accept(message, ctx, LycanitesMobs.proxy.getPlayer(ctx));
            });
         }

         return null;
      };
   }

   private <REQ extends IMessage, REPLY extends IMessage> IMessageHandler<REQ, REPLY> serverHandler(TriConsumer<REQ, MessageContext, EntityPlayer> c) {
      return (message, ctx) -> {
         if (ctx.side == Side.SERVER) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               c.accept(message, ctx, LycanitesMobs.proxy.getPlayer(ctx));
            });
         }

         return null;
      };
   }

   public void sendToAll(IMessage message) {
      this.network.sendToAll(message);
   }

   public void sendToPlayer(IMessage message, EntityPlayerMP player) {
      this.network.sendTo(message, player);
   }

   public void sendToAllAround(IMessage message, TargetPoint point) {
      this.network.sendToAllAround(message, point);
   }

   public void sendToDimension(IMessage message, int dimensionID) {
      this.network.sendToDimension(message, dimensionID);
   }

   public void sendToServer(IMessage message) {
      this.network.sendToServer(message);
   }
}
