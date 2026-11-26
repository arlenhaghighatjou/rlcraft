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

public class PacketHandler {
   public final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("lycanitesmobs");

   public PacketHandler() {
   }

   public void init() {
      int messageID = 0;
      this.network.registerMessage(this.clientHandler(MessageBeastiary::onMessage), MessageBeastiary.class, MessageBeastiary::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageCreature::onMessage), MessageCreature.class, MessageCreature::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageCreatureKnowledge::onMessage), MessageCreatureKnowledge.class, MessageCreatureKnowledge::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessagePlayerStats::onMessage), MessagePlayerStats.class, MessagePlayerStats::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessagePetEntry::onMessage), MessagePetEntry.class, MessagePetEntry::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessagePetEntryRemove::onMessage), MessagePetEntryRemove.class, MessagePetEntryRemove::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageSummonSet::onMessage), MessageSummonSet.class, MessageSummonSet::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageSummonSetSelection::onMessage), MessageSummonSetSelection.class, MessageSummonSetSelection::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageEntityPickedUp::onMessage), MessageEntityPickedUp.class, MessageEntityPickedUp::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageEntityPerched::onMessage), MessageEntityPerched.class, MessageEntityPerched::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageWorldEvent::onMessage), MessageWorldEvent.class, MessageWorldEvent::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageMobEvent::onMessage), MessageMobEvent.class, MessageMobEvent::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageSummoningPedestalStats::onMessage), MessageSummoningPedestalStats.class, MessageSummoningPedestalStats::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.clientHandler(MessageEntityVelocity::onMessage), MessageEntityVelocity.class, MessageEntityVelocity::new, messageID++, Side.CLIENT);
      this.network.registerMessage(this.serverHandler(MessageEntityGUICommand::onMessage), MessageEntityGUICommand.class, MessageEntityGUICommand::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageSyncRequest::onMessage), MessageSyncRequest.class, MessageSyncRequest::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageGUIRequest::onMessage), MessageGUIRequest.class, MessageGUIRequest::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessagePlayerControl::onMessage), MessagePlayerControl.class, MessagePlayerControl::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessagePlayerLeftClick::onMessage), MessagePlayerLeftClick.class, MessagePlayerLeftClick::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessagePlayerAttack::onMessage), MessagePlayerAttack.class, MessagePlayerAttack::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessagePetEntry::onMessage), MessagePetEntry.class, MessagePetEntry::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessagePetEntryRemove::onMessage), MessagePetEntryRemove.class, MessagePetEntryRemove::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageSummonSet::onMessage), MessageSummonSet.class, MessageSummonSet::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageSummonSetSelection::onMessage), MessageSummonSetSelection.class, MessageSummonSetSelection::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageSummoningPedestalSummonSet::onMessage), MessageSummoningPedestalSummonSet.class, MessageSummoningPedestalSummonSet::new, messageID++, Side.SERVER);
      this.network.registerMessage(this.serverHandler(MessageTileEntityButton::onMessage), MessageTileEntityButton.class, MessageTileEntityButton::new, messageID++, Side.SERVER);
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

   public interface TriConsumer<A, B, C> {
      void accept(A var1, B var2, C var3);
   }
}
