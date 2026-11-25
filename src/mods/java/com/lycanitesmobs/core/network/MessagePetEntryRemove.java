package com.lycanitesmobs.core.network;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.pets.PetEntry;
import com.lycanitesmobs.core.pets.PetManager;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePetEntryRemove implements IMessage {
   public UUID petEntryID;

   public MessagePetEntryRemove() {
   }

   public MessagePetEntryRemove(ExtendedPlayer playerExt, PetEntry petEntry) {
      this.petEntryID = petEntry.petEntryID;
   }

   public static void onMessage(MessagePetEntryRemove message, MessageContext ctx, EntityPlayer player) {
      if (ctx.side == Side.SERVER) {
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
         PetManager petManager = playerExt.petManager;
         PetEntry petEntry = petManager.getEntry(message.petEntryID);
         if (petEntry == null) {
            LycanitesMobs.logWarning("", "Tried to remove a null PetEntry from server!");
            return;
         }

         petEntry.remove();
      } else {
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
         if (playerExt == null) {
            return;
         }

         PetManager petManager = playerExt.petManager;
         PetEntry petEntry = petManager.getEntry(message.petEntryID);
         if (petEntry == null) {
            LycanitesMobs.logWarning("", "Tried to remove a null PetEntry from client!");
            return;
         }

         petEntry.remove();
      }

   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);

      try {
         this.petEntryID = packet.readUniqueId();
      } catch (Exception var4) {
         LycanitesMobs.logWarning("", "There was a problem decoding the packet: " + packet + ".");
         var4.printStackTrace();
      }

   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);

      try {
         packet.writeUniqueId(this.petEntryID);
      } catch (Exception var4) {
         LycanitesMobs.logWarning("", "There was a problem encoding the packet: " + packet + ".");
         var4.printStackTrace();
      }

   }
}
